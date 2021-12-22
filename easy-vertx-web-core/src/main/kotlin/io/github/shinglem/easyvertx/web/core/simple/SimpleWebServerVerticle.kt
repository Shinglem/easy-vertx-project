package io.github.shinglem.easyvertx.web.core.simple

import io.github.shinglem.easyvertx.core.ConfigLoader
import io.github.shinglem.easyvertx.core.def.DefaultConfigLoader
import io.github.shinglem.easyvertx.core.json.path
import io.github.shinglem.easyvertx.web.core.*
import io.github.shinglem.easyvertx.web.core.handlers.*
import io.github.shinglem.easyvertx.web.core.resolvers.SimpleConfigControllerResolver
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory
import kotlin.reflect.full.createInstance

open class SimpleWebServerVerticle  : WebServerVerticle() {
    private val logger = LoggerFactory.getLogger(this::class.java.name)

    private val configLoader: ConfigLoader = DefaultConfigLoader()
    open val webConfig by lazy {
        configLoader.config().path("${this::class.simpleName}") ?: JsonObject()
    }
    open val port = webConfig.path("httpServerOptions.port")?:8080
    open override val  httpServerOptions by lazy {
        webConfig.path<JsonObject>("httpServerOptions")?.let { HttpServerOptions(it).setPort(port) }
            ?: HttpServerOptions()
    }
    open override val  controllerResolvers by lazy {
        webConfig.path<JsonArray>("controllerResolvers")
            ?.let { it.list as List<String> }
            ?.map {
                Class.forName(it).kotlin.createInstance() as ControllerResolver
            }
            ?: kotlin.run {
                logger.debug("no controllerResolvers define , use ConfigControllerResolver")
                listOf(SimpleConfigControllerResolver())
            }
    }
    override val controllerHandlers: List<ControllerHandler> by lazy {
        webConfig.path<JsonArray>("controllerHandlers")
            ?.let { it.list as List<String> }
            ?.map {
                Class.forName(it).kotlin.createInstance() as ControllerHandler
            }
            ?: kotlin.run {
                logger.debug("no controllerHandlers define , use SimpleControllerHandler")
                listOf(SimpleControllerHandler())
            }
    }
    override val routeHandlers: List<RouteFunctionHandler> by lazy {
        webConfig.path<JsonArray>("routeHandlers")
            ?.let { it.list as List<String> }
            ?.map {
                Class.forName(it).kotlin.createInstance() as RouteFunctionHandler
            }
            ?: kotlin.run {
                logger.debug("no routeHandlers define , use SimpleRouteHandler")
                listOf(SimpleRouteHandler() , SimpleRouteFileterHandler())
            }
    }
    override val paramHandlers: List<ParamHandler> by lazy {
        webConfig.path<JsonArray>("paramHandlers")
            ?.let { it.list as List<String> }
            ?.map {
                Class.forName(it).kotlin.createInstance() as ParamHandler
            }
            ?: kotlin.run {
                logger.debug("no paramHandlers define , use SimpleBodyParamHandler , SimplePathParamHandler , SimpleQueryParamHandler , SimpleRcParamHandler , SimpleHeaderParamHandler  ")
                listOf(SimpleBodyParamHandler() , SimplePathParamHandler() , SimpleQueryParamHandler() , SimpleRcParamHandler() , SimpleHeaderParamHandler())
            }
    }
    override val resultHandlers: List<ResultHandler> by lazy {
        webConfig.path<JsonArray>("resultHandlers")
            ?.let { it.list as List<String> }
            ?.map {
                Class.forName(it).kotlin.createInstance() as ResultHandler
            }
            ?: kotlin.run {
                logger.debug("no resultHandlers define , use SimpleRouteHandler")
                listOf(SimpleRawTypeHandler())
            }
    }

    override val responseHandler: List<ResponseHandler> by lazy {
        webConfig.path<JsonArray>("responseHandler")
            ?.let { it.list as List<String> }
            ?.map {
                Class.forName(it).kotlin.createInstance() as ResponseHandler
            }
            ?: kotlin.run {
                logger.debug("no responseHandler define , use SimpleRestResponseHandler")
                listOf(SimpleRestResponseHandler())
            }
    }
}
