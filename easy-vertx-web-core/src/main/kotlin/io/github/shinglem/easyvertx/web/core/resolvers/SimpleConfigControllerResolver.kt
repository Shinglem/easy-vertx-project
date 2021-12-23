package io.github.shinglem.easyvertx.web.core.resolvers

import io.github.shinglem.easyvertx.core.ConfigLoader
import io.github.shinglem.easyvertx.core.def.DefaultConfigLoader
import io.github.shinglem.easyvertx.core.json.path
import io.github.shinglem.easyvertx.web.core.ControllerResolver
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import kotlin.reflect.full.createInstance

open class SimpleConfigControllerResolver : ControllerResolver {

    override var config: JsonObject = JsonObject()
//    private val configLoader: ConfigLoader = DefaultConfigLoader()
    open val controllerNames by lazy {
        val json = config.path("${this::class.simpleName}.controllers") ?: JsonArray()
        json.list as List<String>
    }

    override fun getControllers(): List<Any> {
        return controllerNames.map {
            Class.forName(it).kotlin
        }.map {
            it.objectInstance ?: it.createInstance()
        }
    }
}
