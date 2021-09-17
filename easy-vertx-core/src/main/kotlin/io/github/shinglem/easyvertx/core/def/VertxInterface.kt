package io.github.shinglem.easyvertx.core.def


import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject

interface Main {
    fun start()
    fun start(successHandle: () -> Unit)
    fun vertx() : Vertx
}

interface ConfigLoader {

    fun mergeConfig(json: JsonObject)
    fun loadConfig()
    fun config(): JsonObject
}





