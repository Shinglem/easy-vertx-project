package io.github.shinglem.easyvertx.core


import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject

interface Main {
    fun start()
    fun start(successHandle: () -> Unit)
    fun vertx() : Vertx
    fun config(): JsonObject
}

interface ConfigLoader {

    fun mergeConfig(json: JsonObject)
    fun loadConfig(): JsonObject
    fun config(): JsonObject
}





