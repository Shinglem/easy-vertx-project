package io.github.shinglem.easyvertx.core


import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject

interface Main {
    fun start()
    fun start(successHandle: () -> Unit)
    fun vertx(): Vertx
    fun config(): Future<JsonObject>
}

interface ConfigLoader {

    fun store(): ConfigStoreOptions

    fun order(): Int = 0
}


object Global {
    @JvmStatic
    lateinit var vertx: Vertx

    @JvmStatic
    lateinit var config: Future<JsonObject>

}

