package io.github.shinglem.easyvertx.web.handler


import io.github.shinglem.easyvertx.core.util.ClassHandler
import io.github.shinglem.easyvertx.core.util.FunctionHandler
import io.github.shinglem.easyvertx.web.core.impl.Route
import io.github.shinglem.easyvertx.web.core.impl.Route.Routes
import io.github.shinglem.easyvertx.web.core.impl.RouteBase
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import kotlin.reflect.KClass

open class RouteBaseHandler : ClassHandler {

    companion object {
        @JvmStatic
        val INSTANCE = RouteBaseHandler()
    }


    override fun annotationKClass(): KClass<out Annotation> {
        return RouteBase::class
    }

    override fun resolve(classMetadata: MutableMap<String , Any?>, annotation: Annotation) {
        val anno = annotation as RouteBase
        classMetadata.put("routeBasePath", anno.path)
        classMetadata.put("routeBaseProduce", anno.produces)
        classMetadata.put("routeBaseConsumes", anno.consumes)
    }

}

open class RouteHandler : FunctionHandler {

    companion object {
        @JvmStatic
        val INSTANCE = RouteHandler()
    }

    override fun annotationKClass(): KClass<out Annotation> {
        return Route::class
    }

    @Suppress("UNCHECKED_CAST")
    override fun resolve(classMetadata: MutableMap<String , Any?>, annotation: Annotation) {
        val anno = annotation as Route
        val router = classMetadata.get("router") as Router
        val routeBasePath = classMetadata.get("routeBasePath") as String? ?: ""


        val route = router.route()

        val routes = classMetadata.get("routes") as MutableList<io.vertx.ext.web.Route>? ?: kotlin.run {
            classMetadata.put("routes",mutableListOf<io.vertx.ext.web.Route>())
            classMetadata.get("routes") as MutableList<io.vertx.ext.web.Route>
        }

        routes.add(route)


        var path = ""

        if (routeBasePath.endsWith("/")) {
            path += routeBasePath.dropLast(1)
        } else {
            path += routeBasePath
        }

        if (anno.path == "/" || anno.path.isBlank()) {

        } else if (anno.path.startsWith("/")) {
            path += "/" + anno.path.removePrefix("/")
        }

        if (path.isBlank()) {
            path = "/"
        }

        if (anno.regex.isNotBlank()) {
            route.pathRegex(path)
        } else {
            route.path(path)
        }

        anno.methods.forEach {
            route.method(it.method)
        }

        val baseConsumes = classMetadata.get("routeBaseConsumes") as Array<String>
        baseConsumes.forEach {
            route.consumes(it)
        }
        anno.consumes.forEach {
            route.consumes(it)
        }

        val baseProduce = classMetadata.get("routeBaseProduce") as Array<String>
        baseProduce.forEach {
            route.produces(it)
        }
        anno.produces.forEach {
            route.produces(it)
        }

        if (anno.order != 0) {
            route.order(anno.order)
        }
        classMetadata.put("handlerType" , anno.type)

    }

}

open class RoutesHandler : RouteHandler() {

    companion object {
        @JvmStatic
        val INSTANCE = RoutesHandler()
    }

    override fun annotationKClass(): KClass<out Annotation> {
        return Routes::class
    }

    @Suppress("UNCHECKED_CAST")
    override fun resolve(classMetadata: MutableMap<String , Any?>, annotation: Annotation) {
        val anno = annotation as Routes

        val routes = classMetadata.get("routes") as MutableList<io.vertx.ext.web.Route>? ?: kotlin.run {
            classMetadata.put("routes",mutableListOf<io.vertx.ext.web.Route>())
            classMetadata.get("routes") as MutableList<io.vertx.ext.web.Route>
        }

        anno.value.forEach {
            super.resolve(classMetadata, it)
        }

    }

}
