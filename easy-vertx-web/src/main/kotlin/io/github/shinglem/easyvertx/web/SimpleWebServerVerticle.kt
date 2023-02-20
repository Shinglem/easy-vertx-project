package io.github.shinglem.easyvertx.web

import io.github.shinglem.easyvertx.core.Global
import io.github.shinglem.easyvertx.core.json.path
import io.github.shinglem.easyvertx.core.json.path0
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.JsonObject
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

open class SimpleWebServerVerticle : WebServerVerticle() {

    open val webConfig by lazy {
        Global.config.path<JsonObject>("$.webServer") ?: JsonObject()
    }
    open override val httpServerOptions by lazy {
        webConfig.path<JsonObject>("httpServerOptions")?.mapTo(HttpServerOptions::class.java)
            ?: HttpServerOptions()
    }

}
