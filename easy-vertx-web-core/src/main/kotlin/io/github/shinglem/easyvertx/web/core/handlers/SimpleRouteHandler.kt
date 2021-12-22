package io.github.shinglem.easyvertx.web.core.handlers

import io.github.shinglem.easyvertx.web.core.*
import io.github.shinglem.easyvertx.web.core.impl.Route
import io.github.shinglem.easyvertx.web.core.impl.RouteFilter
import io.github.shinglem.easyvertx.web.core.impl.toVertxHttpMethod
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.*

open class SimpleRouteHandler : RouteFunctionHandler() {

    private val logger = LoggerFactory.getLogger(this::class.java.name)


    @OptIn(ExperimentalStdlibApi::class)
    override fun register() {
        logger.debug("SimpleRouteHandler")


        val (controller, func, firstParamHandler, firstResultHandler, firstResponseHandler) = routeFunctionHandlerChainProp
        val (router, route, vertx, scope) = handlerChainProp

        val rp = routeFunctionHandlerChainProp
        val hp = handlerChainProp

        logger.debug("${controller::class.simpleName}")
        logger.debug(func.name)

        val routes = func.findAnnotations<Route>()

        if (routes.isEmpty()) {
            next()
            return
        }

        routes.forEach {

            if (it.regex.isNotBlank()) {
                route.pathRegex(it.path)
            } else {
                route.path(it.path)
            }

            it.methods.forEach {
                route.method(it.toVertxHttpMethod())
            }

            it.consumes.forEach {
                route.consumes(it)
            }

            it.produces.forEach {
                route.produces(it)
            }

            route.order(it.order)


            when (it.type) {
                Route.HandlerType.NORMAL ->
                    route.handler {
                        scope.launch {
                            handle(rp,hp,it)
                        }
                    }
                Route.HandlerType.BLOCKING ->
                    route.blockingHandler {
                        scope.launch {
                            handle(rp,hp,it)
                        }
                    }
                Route.HandlerType.FAILURE ->
                    route.failureHandler {
                        scope.launch {
                            handle(rp,hp,it)
                        }
                    }
            }


        }

        next()
    }

    private suspend fun handle(
        rp: RouteFunctionHandlerChainProp,
        hp: HandlerChainProp,
        ctx: RoutingContext
    ) {
        try {
            val params = getParams(rp, hp, ctx)
            val result = execFunction(params, rp.func)
            if (!ctx.response().ended()) {
                val resultStr = getResult(rp, hp, result, rp.func.returnType)
                val info = ResponseInfo(
                    resp = resultStr,
                )
                val resp = getResponse(rp, hp, info)
                ctx.response().end(resp)

            }
        } catch (e: Throwable) {
            logger.error("route ${ctx.request().path()} ------- ")
            logger.error("route fail => ", e)
            ctx.fail(e)
        }
    }

    private fun getParams(
        rp: RouteFunctionHandlerChainProp,
        hp: HandlerChainProp,
        ctx: RoutingContext
    ): MutableMap<KParameter, Any?> {

        val firstParamHandler = rp.firstParamHandler
        val paramMap = mutableMapOf<KParameter, Any?>()
        rp.func.parameters.forEach {
            firstParamHandler.apply {
                this.handlerChainProp = hp

                this.paramHandlerChainProp = ParamHandlerChainProp(
                    param = it,
                    context = ctx,
                    paramMap = paramMap
                )


                this.register()
            }
        }
        paramMap.put(rp.func.parameters.first { it.kind == KParameter.Kind.INSTANCE }, rp.controller)

        return paramMap

    }

    private suspend fun execFunction(
        params: MutableMap<KParameter, Any?>,
        func: KFunction<*>,
    ): Any? {

        val result = func.callSuspendBy(
            params
        )
        return result
    }


    private fun getResult(
        rp: RouteFunctionHandlerChainProp,
        hp: HandlerChainProp,
        result: Any?,
        resultType: KType
    ): String {

        val resultContainer = ResultContainer()
        rp.firstResultHandler.apply {
            this.handlerChainProp = hp

            this.resultHandlerChainProp = ResultHandlerChainProp(
                result,
                resultType,
                resultContainer
            )

            this.register()
        }

        val resultStr = resultContainer.value
        return resultStr
    }


    private fun getResponse(
        rp: RouteFunctionHandlerChainProp,
        hp: HandlerChainProp,
        respInfo: ResponseInfo
    ): Buffer {

        val responseBufferContainer = ResponseBufferContainer()
        rp.firstResponseHandler.apply {
            this.handlerChainProp = hp
            this.responseHandlerChainProp = ResponseHandlerChainProp(
                responseInfo = respInfo,
                responseBufferContainer = responseBufferContainer
            )

            this.register()
        }
        val resp = responseBufferContainer.respBuffer
        return resp
    }


}

open class SimpleRouteFileterHandler : RouteFunctionHandler() {

    private val logger = LoggerFactory.getLogger(this::class.java.name)


    @OptIn(ExperimentalStdlibApi::class)
    override fun register() {
        logger.debug("SimpleRouteFileterHandler")

        val (controller, func, firstParamHandler, firstResultHandler, firstResponseHandler) = routeFunctionHandlerChainProp
        val (router, route, vertx, scope) = handlerChainProp

        val rp = routeFunctionHandlerChainProp
        val hp = handlerChainProp

        logger.debug("${controller::class.simpleName}")
        logger.debug(func.name)

        val routes = func.findAnnotations<RouteFilter>()
        routes.forEach {
            val list = func.parameters.filter { it.kind == KParameter.Kind.VALUE }
            val ins = func.parameters.first { it.kind == KParameter.Kind.INSTANCE }
            if (list.size == 1 && list.first().type.isSupertypeOf(RoutingContext::class.starProjectedType)) {
                route.handler {
                    scope.launch {
                        try {
                            func.callBy(
                                mapOf(
                                    ins to routeFunctionHandlerChainProp.controller,
                                    list.first() to it
                                )
                            )
                        } catch (e: Throwable) {
                            logger.error("route filter ${it.request().path()} filter : ------- ${controller::class.simpleName}#${func.name}")
                            logger.error("route filter fail => ", e)
                            it.fail(e)
                        }




                    }


                }
            }

        }

        next()
    }

//    override var next: RouteFunctionHandler? = null


}
