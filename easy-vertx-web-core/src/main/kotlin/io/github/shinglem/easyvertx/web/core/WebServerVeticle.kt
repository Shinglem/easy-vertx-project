package io.github.shinglem.easyvertx.web.core

import com.google.common.reflect.ClassPath
import io.github.shinglem.easyvertx.util.classInfo.ClassResolver
import io.github.shinglem.easyvertx.util.classInfo.ParamHandler
import io.github.shinglem.easyvertx.web.core.handlers.*
import io.vertx.core.Context
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.LoggerFactory
import kotlin.reflect.full.createInstance

abstract class WebServerVerticle : CoroutineVerticle() {
    private val logger = LoggerFactory.getLogger(this::class.java.name)


    abstract val httpServerOptions: HttpServerOptions
    protected lateinit var rootRouter: Router


    override fun init(vertx: Vertx, context: Context) {
        super.init(vertx, context)
        rootRouter = Router.router(vertx)

    }

    open fun findControllers(pkg : String): List<Any> {
        val clzp = ClassPath.from(this::class.java.classLoader)
        val clzs = clzp.getTopLevelClassesRecursive(pkg)
            .map {
                it.load()
            }
            .map { it.kotlin }
            .mapNotNull {
                try {
                    it.constructors
                    it
                } catch (e: Throwable) {
                    null
                }
            }
            .map {
                it.objectInstance ?: it.createInstance()
            }

        return clzs
    }

    open fun registerController(ctl: Any) {
        ClassResolver(
            listOf(RouteBaseHandler() , RouteHandler() , RoutesHandler(), QueryParamHandler() ),
            Processor(),
            mutableMapOf(
                "router" to rootRouter ,
                "scopeVerticle" to this ,
            )
        ).resolve(ctl)
    }

    open override suspend fun start() {
        logger.debug("---------web start---------" + this.deploymentID)


        val server = vertx.createHttpServer(httpServerOptions)

        findControllers("webtestexample").forEach {
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
