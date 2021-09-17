package io.github.shinglem.easyvertx.core.def


import com.xy.common.util.exception.GetClassException
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import java.util.*
import kotlin.reflect.KClass

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





