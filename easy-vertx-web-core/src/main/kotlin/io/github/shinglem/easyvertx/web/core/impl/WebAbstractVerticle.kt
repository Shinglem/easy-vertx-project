package io.github.shinglem.easyvertx.web.core.impl

import io.vertx.core.Context
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

abstract class WebAbstractVerticle() : CoroutineVerticle() {


    private val logger = LoggerFactory.getLogger(this::class.java.name)


    abstract val httpServerOptions :HttpServerOptions


    protected lateinit var router: Router
    protected val controllers: MutableList<Any> = mutableListOf()

    override fun init(vertx: Vertx, context: Context) {
        super.init(vertx, context)
        router = Router.router(vertx)
    }

    fun Route.launchHandler(block: suspend (routingContext: RoutingContext) -> Unit): Route {
        this.handler {
            launch {
                try {

                    val internalId: String = it["internalId"]
                    it.put("internalId", internalId)
                    logger.debug(
                        "[$internalId] |  ${it.request().path()} :  ${it.request().method()}  :  {}",
                        it.bodyAsString
                    )
                    block(it)
                } catch (e: Throwable) {
                    logger.error("route ${it.request().path()} ------- ")
                    logger.error("route fail => ", e)
                    it.fail(500, e)
                }
            }
        }

        return this
    }


    open fun serverStart() {

    }

    abstract fun loadControllers()


    open fun reprocess() {

    }

    open fun controllersToRoute() {
        controllers.map {
            getRouterModel(it)
        }.forEach {
            createRoute(router , it, this)
        }
    }

    open fun registerController(vararg controller: Any) {
        controllers.addAll(controller)
    }


    open override suspend fun start() {
        logger.debug("---------web start---------" + this.deploymentID)


        serverStart()
        val server = vertx.createHttpServer(httpServerOptions)
        loadControllers()
        controllersToRoute()
        reprocess()


        server
            .requestHandler(router)
            .listen() {
                if (it.succeeded()) {
                    val s = it.result()
                    logger.info("web start success , listening ${s.actualPort()}")
                } else {
                    val cause = it.cause()
                    logger.error("web start fail =>", cause)
                }
            }

    }


}



