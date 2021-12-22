package io.github.shinglem.easyvertx.web.core.handlers

import io.github.shinglem.easyvertx.web.core.ControllerHandler
import io.github.shinglem.easyvertx.web.core.HandlerChainProp
import io.github.shinglem.easyvertx.web.core.RouteFunctionHandlerChainProp
import io.github.shinglem.easyvertx.web.core.impl.RouteBase
import io.vertx.ext.web.Router
import org.slf4j.LoggerFactory
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions

open class SimpleControllerHandler : ControllerHandler(){

    private val logger = LoggerFactory.getLogger(this::class.java.name)


    @OptIn(ExperimentalStdlibApi::class)
    override fun register() {
        logger.debug("SimpleControllerHandler")

        val (controller,firstRouteHandler , firstParamHandler,firstResultHandler,firstResponseHandler) = controllerHandlerChainProp
        val (router,route , vertx,scope) = handlerChainProp

        logger.debug("${controller::class.simpleName}")

        val klz = controller::class
        val routeBase = klz.findAnnotation<RouteBase>()!!

        val subRouter = Router.router(handlerChainProp.vertx)
        klz.memberFunctions.forEach {

            firstRouteHandler.handlerChainProp = HandlerChainProp(
                subRouter,subRouter.route() , vertx,scope
            )
            firstRouteHandler.routeFunctionHandlerChainProp = RouteFunctionHandlerChainProp(
                controller,it , firstParamHandler,firstResultHandler,firstResponseHandler
            )


            firstRouteHandler.register()
        }

        router.mountSubRouter(routeBase.path , subRouter)

        logger.debug("${routeBase.path} routes => ")
        logger.debug("${subRouter.routes}")

        next()
    }

//    override var next: ControllerHandler? = null


}
