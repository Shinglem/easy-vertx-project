package io.github.shinglem.easyvertx.web.core

import io.github.shinglem.easyvertx.core.json.path
import io.github.shinglem.easyvertx.web.core.*
import io.github.shinglem.easyvertx.web.core.handlers.*
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory

open class SimpleWebServerVerticle : WebServerVerticle() {
    private val logger = LoggerFactory.getLogger(this::class.java.name)

    //    private val configLoader: ConfigLoader = DefaultConfigLoader()
    open val webConfig by lazy {
        config ?: JsonObject()
    }
    open val port by lazy { webConfig.path("httpServerOptions.port") ?: 8080 }
    open override val httpServerOptions by lazy {
        webConfig.path<JsonObject>("httpServerOptions")?.let { HttpServerOptions(it).setPort(port) }
            ?: HttpServerOptions()
    }

}
