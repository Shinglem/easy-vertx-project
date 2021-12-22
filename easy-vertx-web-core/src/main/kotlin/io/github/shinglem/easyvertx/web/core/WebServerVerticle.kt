package io.github.shinglem.easyvertx.web.core

import io.github.shinglem.easyvertx.web.core.handlers.*
import io.github.shinglem.easyvertx.web.core.impl.listToChain
import io.vertx.core.Context
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.LoggerFactory

abstract class WebServerVerticle : CoroutineVerticle() {
    private val logger = LoggerFactory.getLogger(this::class.java.name)
    abstract val httpServerOptions: HttpServerOptions
    protected lateinit var router: Router
        private set


    override fun init(vertx: Vertx, context: Context) {
        super.init(vertx, context)
        router = Router.router(vertx)
    }


    open fun registerControllers() {
        val rt = this.router
        val vt = vertx
        val sc = this

        val firstRouteHandler = listToChain(routeHandlers)
        val firstParamHandler = listToChain(paramHandlers)
        val firstResultHandler = listToChain(resultHandlers)
        val firstResponseHandler = listToChain(responseHandler)

        val firstControllerHandler = listToChain(controllerHandlers)

        firstControllerHandler.apply {
            this.handlerChainProp = HandlerChainProp(
                router = rt,
                route = rt.route(),
                vertx = vt,
                scope = sc,

                )
        }


        val controllers = controllerResolvers.flatMap {
            it.getControllers()
        }
        controllers.forEach {
            firstControllerHandler.controllerHandlerChainProp = ControllerHandlerChainProp(
                controller = it,
                firstRouteHandler = firstRouteHandler,
                firstParamHandler = firstParamHandler,
                firstResultHandler = firstResultHandler,
                firstResponseHandler = firstResponseHandler,
            )

            firstControllerHandler.register()
        }
    }

    open override suspend fun start() {
        logger.debug("---------web start---------" + this.deploymentID)


        val server = vertx.createHttpServer(httpServerOptions)

        registerControllers()

        coroutineContext



        server.requestHandler(router).listen() {
            if (it.succeeded()) {
                val s = it.result()
                logger.info("web start success , listening ${s.actualPort()}")
            } else {
                val cause = it.cause()
                logger.error("web start fail =>", cause)
            }
        }

    }

    abstract val controllerResolvers: List<ControllerResolver>
    abstract val controllerHandlers: List<ControllerHandler>
    abstract val routeHandlers: List<RouteFunctionHandler>
    abstract val paramHandlers: List<ParamHandler>
    abstract val resultHandlers: List<ResultHandler>
    abstract val responseHandler: List<ResponseHandler>
}
