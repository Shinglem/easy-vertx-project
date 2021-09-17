package test.verticles


import io.github.shinglem.easyvertx.core.ConfigLoader
import io.github.shinglem.easyvertx.core.def.DefaultConfigLoader
import io.github.shinglem.easyvertx.core.json.path
import io.github.shinglem.easyvertx.web.core.base.BaseRoute
import io.github.shinglem.easyvertx.web.core.base.RestfulRoute
import io.github.shinglem.easyvertx.web.core.impl.WebAbstractVerticle
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.JsonObject
import test.controller.UserController

class UsServerVerticle : WebAbstractVerticle() {

    private val configLoader: ConfigLoader = DefaultConfigLoader()

    open val webConfig by lazy {
        configLoader.config().path("${this::class.simpleName}") ?: JsonObject()
    }
    open val port = webConfig.path("httpServerOptions.port")?:8080
    open override val  httpServerOptions by lazy {
        webConfig.path<JsonObject>("httpServerOptions")?.let { HttpServerOptions(it).setPort(port) }
            ?: HttpServerOptions()
    }

    override fun serverStart() {



    }

    override fun loadControllers() {
        registerController( UserController)
        registerController( BaseRoute() , RestfulRoute())
    }

}
