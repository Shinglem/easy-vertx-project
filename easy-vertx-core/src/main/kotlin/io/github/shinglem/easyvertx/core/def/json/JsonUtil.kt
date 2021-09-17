package io.github.shinglem.easyvertx.core.def.json

import io.github.shinglem.easyvertx.core.def.exception.JsonPathException
import io.github.shinglem.easyvertx.core.def.exception.JsonTypeException
import io.vertx.core.json.JsonObject
import kotlin.reflect.KClass

fun <T : Any> JsonObject.path(path: String, clz : KClass<T>): T?{
    val pathList = path.split(".")
    val value = try {
        getJsonValue(this, pathList)
    } catch (e: Throwable) {
        throw JsonPathException("json parsing error : path -> $path" , e)

    }

    try {

        return value as T?
    }catch (e: Throwable) {
        throw JsonTypeException("json type cast error : path ->  $path , value ${value?.javaClass} -> type ${clz.java}" , e)

    }

}

fun <T : Any> JsonObject.path(path: String, clz : Class<T>): T?{
    return this.path(path , clz.kotlin)
}
inline fun <reified T:Any> JsonObject.path(path: String): T? {
   return this.path(path , T::class)

}

private tailrec fun getJsonValue(json: JsonObject, keys: List<String>): Any? {
    val keyList = keys
    val value = json.getValue(keys[0]) ?: return null
    val afterGet = keyList.drop(1)
    if (afterGet.isNotEmpty()) {
        return getJsonValue((value as JsonObject),afterGet)
    } else {
        return value
    }


}