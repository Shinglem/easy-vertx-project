package io.github.shinglem.easyvertx.web

import com.google.common.reflect.ClassPath
import io.github.shinglem.easyvertx.web.core.impl.RouteBase
import io.github.shinglem.easyvertx.web.util.resolveBuild
import io.vertx.core.Context
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.LoggerHandler
import io.vertx.kotlin.coroutines.CoroutineVerticle
import mu.KotlinLogging
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.hasAnnotation

private val logger = KotlinLogging.logger {}

abstract class WebServerVerticle : CoroutineVerticle() {

    abstract val httpServerOptions: HttpServerOptions
    open protected lateinit var rootRouter: Router

    open protected val resolver = resolveBuild()

    override fun init(vertx: Vertx, context: Context) {
        super.init(vertx, context)
        rootRouter = Router.router(vertx)
    }

    open fun findControllers(): List<Any> {
        val clzp = ClassPath.from(this::class.java.classLoader)
        val clzs = clzp.getTopLevelClasses()
            .mapNotNull {
                try {
                    it.load()
                } catch (e: Throwable) {
                    when (e) {
                        is NoClassDefFoundError -> {
                            return@mapNotNull null
                        }

                        else -> throw e
                    }
                }
            }
            .map { it.kotlin }
            .filter {
                try {
                    it.hasAnnotation<RouteBase>() && it.constructors.isNotEmpty()
                } catch (e: Throwable) {
                    when (e) {
                        is NoClassDefFoundError, is UnsupportedOperationException -> {
                            return@filter false
                        }

                        else -> throw e
                    }
                }
            }
            .map {
                it.objectInstance ?: it.createInstance()
            }

        return clzs
    }

    open fun registerController(ctl: Any) {
        resolver
            .resolve(
                mutableMapOf(
                    "router" to rootRouter,
                    "scope" to this,
                ), ctl
            )
    }

    open fun registerDefaultHandler() {
        rootRouter.route().order(Int.MIN_VALUE).handler {
            it.response().setChunked(true)
        }
        rootRouter.route().order(Int.MIN_VALUE).handler(LoggerHandler.create())
        rootRouter.route().order(Int.MIN_VALUE).handler(BodyHandler.create(true))
    }

    open override suspend fun start() {
        logger.debug("---------web start---------" + this.deploymentID)


        val server = vertx.createHttpServer(httpServerOptions)

        registerDefaultHandler()

        findControllers().forEach {
            registerController(it)
        }


        server.requestHandler(rootRouter).listen() {
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
