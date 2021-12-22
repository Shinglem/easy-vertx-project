package io.github.shinglem.easyvertx.web.core.impl

import io.github.shinglem.easyvertx.web.core.*
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.*

data class RouterModel(
    val initRouters: List<KFunction<*>>,
    val routeFilters: List<RouteFilterModel>,
    val basePath: String?,
    val baseProduces: List<String>?,
    val baseConsumes: List<String>?,
    val routeMatchers: List<RouteMatcher>,
    val controller: Any
)

data class RouteFilterModel(
    val order: Int = 0,
    val routes: List<RouteMatcher>
)

data class RouteMatcher(
    val path: String? = null,
    val regex: String? = null,
    val produces: List<String>,
    val consumes: List<String>,
    val methods: List<Route.HttpMethod>,
    val order: Int = 0,
    val type: Route.HandlerType,
    val fc: KFunction<*>
)

private val logger = LoggerFactory.getLogger("controller-progress")

fun getRouterModel(controller: Any): RouterModel {
    val controllerInstance = controller
    val klz = controller::class
    val annotations = klz.annotations

    val routeBase = annotations
        .findLast { it is RouteBase }
        ?.safeCastTo<RouteBase>()
    val basePath = routeBase?.path ?: ""
    val baseProduces = routeBase?.produces?.toList() ?: listOf()
    val baseConsumes = routeBase?.consumes?.toList() ?: listOf()

    val initRouters = mutableListOf<KFunction<*>>()
    val routeFilters = mutableListOf<RouteFilterModel>()
    val routeMatchers = mutableListOf<RouteMatcher>()
    for (fc in klz.memberFunctions) {

        if (fc.annotations.isNullOrEmpty()) {
            continue
        }
        val params = fc.valueParameters
        if (fc.annotations.any { it.annotationClass == InitRouter::class }) {
            if (params.size != 1 || !params.single().type.isSupertypeOf(io.vertx.ext.web.Router::class.starProjectedType)) {
                error("Router init function need only one io.vertx.ext.web.Router type parameter : ${controller::class.qualifiedName}#${fc.name}")
            }

            initRouters.add(fc)
        }

        if (fc.annotations.any { it.annotationClass == RouteFilter::class }) {
            if (params.size != 1 || !params.single().type.isSupertypeOf(RoutingContext::class.starProjectedType)) {
                error("Router filter function need only one RoutingContext type parameter : ${controller::class.qualifiedName}#${fc.name}")
            }
            val filter = fc.annotations.single() { it.annotationClass == RouteFilter::class } as RouteFilter
            val routes = if (filter.routers.isEmpty()) {
                listOf(
                    RouteMatcher(
                        null,
                        null,
                        listOf(),
                        listOf(),
                        listOf(),
                        FilterBaseLevel,
                        Route.HandlerType.NORMAL,
                        fc
                    )
                )
            } else {
                filter.routers.map {
                    RouteMatcher(
                        it.path,
                        it.regex,
                        it.produces.toList(),
                        it.consumes.toList(),
                        it.methods.toList(),
                        FilterBaseLevel + it.order,
                        it.type,
                        fc
                    )
                }
            }


            routeFilters.add(RouteFilterModel(filter.value, routes))
        }

        val routes = fc.annotations.filter { it.annotationClass == Route::class }.map { it as Route }
        if (routes.isNotEmpty()) {
            val r = routes.map {
                RouteMatcher(
                    it.path,
                    it.regex,
                    it.produces.toList(),
                    it.consumes.toList(),
                    it.methods.toList(),
                    it.order,
                    it.type,
                    fc
                )
            }
            routeMatchers.addAll(r)
        }
        if (fc.annotations.any { it.annotationClass == Route.Routes::class }) {
            val rts = fc.annotations.single() { it.annotationClass == Route.Routes::class } as Route.Routes
            val r = rts.value.map {
                RouteMatcher(
                    it.path,
                    it.regex,
                    it.produces.toList(),
                    it.consumes.toList(),
                    it.methods.toList(),
                    it.order,
                    it.type,
                    fc
                )
            }
            routeMatchers.addAll(r)
        }


    }


    val model = RouterModel(
        initRouters,
        routeFilters,
        basePath,
        baseProduces,
        baseConsumes,
        routeMatchers,
        controller
    )
    return model
}


fun createRoute(router: io.vertx.ext.web.Router, model: RouterModel, scope: CoroutineScope) {
    val (initRouters, routeFilters, basePath, baseProduces, baseConsumes, routeMatchers, instance) = model

    initRouters.forEach {
        val paramMap = it.parameters.map {
            if (it.kind == KParameter.Kind.INSTANCE) {
                it to instance
            } else {
                it to router
            }
        }.toMap()
        runBlocking {
            it.callSuspendBy(paramMap)
        }

    }

    routeFilters.forEach {

        it.routes.forEach { rm ->
            routeMatcherToRoute(rm, router, basePath, Handler { rc ->
                val paramMap = rm.fc.parameters.map {
                    if (it.kind == KParameter.Kind.INSTANCE) {
                        it to instance
                    } else {
                        it to rc
                    }
                }.toMap()
                scope.launch {
                    try{
                        rm.fc.callSuspendBy(paramMap)
                    } catch (e: Throwable) {
                        logger.error("route filter failed ${rm.fc.name} ------- ")
                        logger.error("route filter failed => ", e)
                        rc.fail(500, e)
                    }
                }

            })
        }
    }

    routeMatchers.forEach { rm ->
        val paramMap = createParamMap(rm.fc , instance)
        val type = rm.fc.returnType

        val handler = Handler<RoutingContext>{
            val callMap = paramMap.map { paramEntry ->
                paramEntry.key to paramEntry.value(it)
            }.toMap()
            scope.launch {
                try {
                    val result = rm.fc.callSuspendBy(callMap)
                    if (it.failed() || it.response().closed()) {
                        return@launch
                    }
                    val resp = response(type)(result)
                    it.end(resp)
                } catch (e: Throwable) {
                    logger.error("route ${it.request().path()} ------- ")
                    logger.error("route fail => ", e)
                    it.fail(500, e)
                }
            }

        }

        routeMatcherToRoute(rm, router, basePath, handler)

    }


}

private suspend fun response(type: KType,): suspend (Any?)->String {


    return when  {

        type.isSubtypeOf(Unit::class.starProjectedType) ->
            { r : Any? -> "" }


        type.isSubtypeOf(Future::class.starProjectedType) -> {
            { r : Any? ->
                if(r==null)
                    ""
                else {
                    val v = (r as Future<*>).await()
                    if (v is Unit || v is Void) {
                        ""
                    }else{
                        response(v::class.starProjectedType)(v)
                    }

                }
            }

        }
        type.isSubtypeOf(String::class.starProjectedType) -> {
            { r : Any? ->
                if(r==null)
                    ""
                else
                    r as String
            }

        }

        type.isSubtypeOf(JsonObject::class.starProjectedType) -> {
            { r : Any? ->
                if(r==null)
                    ""
                else
                    (r as JsonObject).encodePrettily()
            }

        }

        type.isSubtypeOf(JsonArray::class.starProjectedType) -> {
            { r : Any? ->
                if(r==null)
                    ""
                else
                    (r as JsonArray).encodePrettily()
            }

        }

        else ->
            { r : Any? ->
                if(r==null)
                    ""
                else
                    JsonObject.mapFrom(r).encodePrettily()
            }


    }


}

fun routeMatcherToRoute(
    it: RouteMatcher,
    router: io.vertx.ext.web.Router,
    basePath: String?,
    handler: io.vertx.core.Handler<RoutingContext>
) {
    val route: io.vertx.ext.web.Route
    if (it.regex != null && it.regex.isNotEmpty()) {
        val p = if (basePath == null) {
            ""
        } else if (basePath.endsWith("/")) {
            basePath + it.regex
        } else {
            basePath + "/" + it.regex
        }
        route = router.routeWithRegex(p)
    } else if (it.path != null && it.path.isNotEmpty()) {
        val p = if (basePath == null) {
            it.path
        } else if (basePath.endsWith("/")) {
            if (it.path.startsWith("/")) {
                basePath + it.path.removePrefix("/")
            } else {
                basePath + it.path
            }
        } else {
            if (it.path.startsWith("/")) {
                basePath + it.path
            } else {
                basePath + "/" + it.path
            }
        }
        route = router.route(p)
    } else {
        route = router.route()
    }

    if (it.order > 0) {
        route.order(it.order)
    }

    if (it.consumes.isNotEmpty()) {
        it.consumes.forEach { c ->
            route.consumes(c)
        }
    }

    if (it.produces.isNotEmpty()) {
        it.produces.forEach { c ->
            route.produces(c)
        }
    }

    if (it.type == Route.HandlerType.NORMAL) {
        route.handler(handler)
    }
    if (it.type == Route.HandlerType.BLOCKING) {
        route.blockingHandler(handler)
    }
    if (it.type == Route.HandlerType.FAILURE) {
        route.failureHandler(handler)
    }


}

fun createParamMap(fc: KFunction<*>, instance: Any): Map<KParameter, (RoutingContext) -> Any?> {
    val map = fc.parameters.map { param ->
        if (param.kind == KParameter.Kind.INSTANCE) {
           return@map param to {rc:RoutingContext  -> instance }
        }

        if (param.annotations.isEmpty() && !param.type.isSubtypeOf(RoutingContext::class.starProjectedType)) {
            error("error parameter : ${instance::class.qualifiedName}#${fc.name} ${param.name}")
        }

        if (param.type.isSubtypeOf(RoutingContext::class.starProjectedType)) {
            return@map param to {rc:RoutingContext  ->  rc}
        }

        return@map   param.annotations.map { ann ->
            when {
                ann is Id && param.type.isSubtypeOf(String::class.starProjectedType) -> {
                    val f = { rc: RoutingContext ->
                        rc.get("internalId", "no-id")
                    }
                    param to f
                }
                ann is Header && param.type.isSubtypeOf(String::class.starProjectedType) -> {
                    val f = { rc: RoutingContext ->
                        rc.request().getHeader(ann.value)
                    }
                    param to f
                }
                ann is PathParam -> {
                    val name = if (ann.value == PathParam.ELEMENT_NAME) {
                        param.name
                    } else {
                        ann.value
                    }

                    val isList = param.type.isSubtypeOf(List::class.starProjectedType)
                    val f = { rc: RoutingContext ->
                        val value = rc.queryParam(name)?.let {
                            if (isList) {
                                it
                            } else {
                                it.firstOrNull()
                            }
                        } ?: rc.pathParam(name)
                        ?: try {
                            rc.bodyAsJson.getValue(name)
                        } catch (e: Throwable) {
                            null
                        }
                        value
                    }
                    param to f

                }
                ann is Body -> {
                    val getFunc = when {
                        param.type.isSubtypeOf(JsonObject::class.starProjectedType) -> { rc: RoutingContext ->
                            rc.bodyAsJson
                        }
                        param.type.isSubtypeOf(Map::class.starProjectedType) -> { rc: RoutingContext -> rc.bodyAsJson.map }
                        param.type.isSubtypeOf(JsonArray::class.starProjectedType) -> { rc: RoutingContext -> rc.bodyAsJsonArray }
                        param.type.isSubtypeOf(List::class.starProjectedType) -> { rc: RoutingContext -> rc.bodyAsJsonArray.list }
                        param.type.isSubtypeOf(String::class.starProjectedType) -> { rc: RoutingContext -> rc.bodyAsString }
                        param.type.isSubtypeOf(Buffer::class.starProjectedType) -> { rc: RoutingContext -> rc.body }
                        else -> { rc: RoutingContext ->
                            rc.bodyAsJson.let {
                                it ?: JsonObject()

                            }.mapTo(
                                (param.type.classifier!! as KClass<*>).java
                            )
                        }
                    }
                    val f = { rc: RoutingContext ->
                        getFunc(rc)
                    }
                    param to f
                }
                else -> {
                    val f = { rc: RoutingContext ->
                        null
                    }
                    param to f
//                    error("error parameter : ${instance::class.qualifiedName}#${fc.name} ${param.name}")
                }

            }
        }.also {
            if (it.size>1)
                error("error parameter : ${instance::class.qualifiedName}#${fc.name} ${param.name}")
        }.single()

    }.toMap()

    return map

}

inline fun <reified T : Any> Any?.safeCastTo(): T? {

    val klz = T::class

    return klz.safeCast(this)
}
