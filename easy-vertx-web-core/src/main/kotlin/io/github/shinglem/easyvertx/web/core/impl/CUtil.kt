package io.github.shinglem.easyvertx.web.core.impl

import io.github.shinglem.easyvertx.web.core.Chain
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.Json
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType


private val logger = LoggerFactory.getLogger("CUtil")


fun <T : Chain<T>> listToChain(chains: List<T>): T {
    var first = chains.first()
    chains.reduce { acc, chain ->
        acc.next = chain
        chain
    }
    return first
}

fun Route.HttpMethod.toVertxHttpMethod(): HttpMethod {
    val name = this.name.replace("_", "-")
    return HttpMethod.valueOf(name)
}

fun JsonObject.transToJsonDecodeValue(type: KType, key: String): Any? {
    val o = this.let { json ->

        val obj = json.getValue(key).let {
            if (it == null) {
                null
            } else if (it is JsonObject) {
                Json.CODEC.fromValue(it, (type.classifier as KClass<*>).java)
            } else if (it is JsonArray) {
                Json.CODEC.fromValue(it, (type.classifier as KClass<*>).java)
            } else {
                it
            }
        }
        obj
    }
    return o
}

fun transToResultObject(type: KType, obj: Any?): String {

    if (obj == null) {
        return "null"
    }

    if (type == Unit::class.starProjectedType || type == Void::class.starProjectedType) {
        return ""
    }

    return when {
        type == Byte::class.starProjectedType -> obj.toString()
        type == Short::class.starProjectedType -> obj.toString()
        type == Int::class.starProjectedType -> obj.toString()
        type == Long::class.starProjectedType -> obj.toString()
        type == Boolean::class.starProjectedType -> obj.toString()
        type == Char::class.starProjectedType -> obj.toString()
        type == Float::class.starProjectedType -> obj.toString()
        type == Double::class.starProjectedType -> obj.toString()

        type == String::class.starProjectedType -> obj.toString()

        type == ByteArray::class.starProjectedType -> obj.toString()
        type == ShortArray::class.starProjectedType -> JsonArray((obj as Array<*>).toList()).encodePrettily()
        type == IntArray::class.starProjectedType -> JsonArray((obj as Array<*>).toList()).encodePrettily()
        type == LongArray::class.starProjectedType -> JsonArray((obj as Array<*>).toList()).encodePrettily()
        type == BooleanArray::class.starProjectedType -> JsonArray((obj as Array<*>).toList()).encodePrettily()
        type == CharArray::class.starProjectedType -> JsonArray((obj as Array<*>).toList()).encodePrettily()
        type == FloatArray::class.starProjectedType -> JsonArray((obj as Array<*>).toList()).encodePrettily()
        type == DoubleArray::class.starProjectedType -> JsonArray((obj as Array<*>).toList()).encodePrettily()

        type == Array::class.starProjectedType -> JsonArray((obj as Array<*>).toList()).encodePrettily()
        type.isSubtypeOf(Collection::class.starProjectedType) -> JsonArray((obj as Collection<*>).toList()).encodePrettily()
        type.isSubtypeOf(Map::class.starProjectedType) -> JsonObject.mapFrom(obj).encodePrettily()

        else -> JsonObject.mapFrom(obj).encodePrettily()
    }
}
