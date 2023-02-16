@file:JvmName("JsonUtil")

package io.github.shinglem.easyvertx.core.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.JsonPathException
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.spi.json.AbstractJsonProvider
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider
import com.jayway.jsonpath.spi.json.JsonProvider
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider
import com.jayway.jsonpath.spi.mapper.MappingProvider
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.kotlin.core.json.get
import java.io.InputStream
import java.util.*


fun initJsonPath() {
    Configuration.setDefaults(object : Configuration.Defaults {

        private val jsonProvider = JacksonJsonNodeJsonProvider(DatabindCodec.mapper())
        private val mappingProvider = JacksonMappingProvider(DatabindCodec.mapper())
        override fun jsonProvider(): JsonProvider {
            return jsonProvider
        }

        override fun options(): MutableSet<Option> {
            return EnumSet.noneOf(Option::class.java)
        }

        override fun mappingProvider(): MappingProvider {
            return mappingProvider
        }

    })
}

open class VertxJsonProvider() : AbstractJsonProvider() {
    override fun parse(json: String?): Any? {
        return try {
            DatabindCodec.mapper().readTree(json).let {
                when (it) {
                    is ValueNode -> when {
                        it.isNumber -> it.numberValue()
                        it.isBinary -> it.binaryValue()
                        it.isNull -> null
                        it.isBoolean -> it.booleanValue()
                        it.isTextual -> it.textValue()
                        else -> error("json is value but not base value : $json")
                    }

                    is ArrayNode -> JsonArray(DatabindCodec.fromParser(it.traverse(), List::class.java))
                    else -> JsonObject(DatabindCodec.fromParser(it.traverse(), Map::class.java) as Map<String, Any?>)
                }
            }
        } catch (e: Throwable) {
            throw RuntimeException("parse json fail ", e)
        }
    }

    override fun parse(jsonStream: InputStream, charset: String): Any? {
        return parse(String(jsonStream.readBytes(), charset(charset)))
    }

    override fun toJson(obj: Any?): String? {
        return when (obj) {
            null -> null
            is JsonObject -> obj.encodePrettily()
            is JsonArray -> obj.encodePrettily()
            is Number, is Boolean, is String -> obj.toString()
            else -> error("type is invalid ${obj::class.java}")

        }
    }

    override fun createArray(): Any {
        return JsonArray()
    }

    override fun createMap(): Any {
        return JsonObject()
    }

    override fun isArray(obj: Any?): Boolean {
        return obj is JsonArray || obj is List<Any?>
    }

    override fun isMap(obj: Any?): Boolean {
        return obj is JsonObject
    }


    override fun getArrayIndex(obj: Any, idx: Int): Any? {
        return toJsonArray(obj).get(idx)
    }

    override fun setArrayIndex(array: Any?, index: Int, newValue: Any?) {
        if (!isArray(array)) {
            throw UnsupportedOperationException()
        } else {
            val arrayNode = toJsonArray(array!!)
            if (index == arrayNode.size()) {
                arrayNode.add(newValue)
            } else {
                arrayNode.set(index, newValue)
            }
        }
    }

    override fun getMapValue(obj: Any, key: String): Any? {
        val jsonObject = toJsonObject(obj)
        return jsonObject[key]
    }

    override fun setProperty(obj: Any, key: Any?, value: Any?) {
        // jlolling: Bug: #211 avoid create cloned nodes
        if (isMap(obj)) {
            setValueInObjectNode(obj as JsonObject, key!!, value)
        } else {
            val array = toJsonArray(obj)
            val index: Int = if (key != null) {
                if (key is Int) key else key.toString().toInt()
            } else {
                array.size()
            }
            if (index == array.size()) {
                array.add(value)
            } else {
                array.set(index, value)
            }
        }
    }

    override fun removeProperty(obj: Any?, key: Any?) {
        if (isMap(obj)) {
            toJsonObject(obj!!).remove(key.toString())
        } else {
            val array = toJsonArray(obj!!)
            val index = if (key is Int) key else key.toString().toInt()
            array.remove(index)
        }
    }

    override fun length(obj: Any?): Int {
        if (isArray(obj)) {
            return toJsonArray(obj!!).size()
        } else if (isMap(obj)) {
            return toJsonObject(obj!!).size()
        } else {
            if (obj is TextNode) {
                return obj.size()
            }
        }
        throw JsonPathException("length operation can not applied to " + if (obj != null) obj.javaClass.name else "null")
    }

    override fun getPropertyKeys(obj: Any?): Collection<String>? {
        val keys = toJsonObject(obj!!).fieldNames()
        return keys
    }

    override fun toIterable(obj: Any?): Iterable<*>? {
        val values = toJsonArray(obj!!).list
        return values
    }


    private fun toJsonObject(obj: Any): JsonObject {
        if (obj is Map<*, *>) {
            return JsonObject(obj as Map<String, Any?>)
        }
        return obj as JsonObject
    }

    private fun toJsonArray(obj: Any): JsonArray {
        if (obj is List<Any?>) {
            return JsonArray(obj)
        }
        return obj as JsonArray
    }

    private fun setValueInObjectNode(objectNode: JsonObject, key: Any, value: Any?) {
        objectNode.put(key.toString(), value)
    }

}

val jsonPathConfig = Configuration.ConfigurationBuilder().jsonProvider(VertxJsonProvider())
    .mappingProvider(JacksonMappingProvider(DatabindCodec.mapper())).build()

inline fun <reified T> JsonObject.path(path: String): T? {
    return JsonPath
        .using(jsonPathConfig)
        .parse(this)
        .read<T>(path)
}

inline fun <reified T> JsonArray.path(path: String): T? {
    return JsonPath
        .using(jsonPathConfig)
        .parse(this)
        .read<T>(path)
}

fun <T> path(json: JsonObject, path: String, clz: Class<T>): T? {
    return JsonPath
        .using(jsonPathConfig)
        .parse(json)
        .read<T>(path)

}

fun <T> path(json: JsonArray, path: String, clz: Class<T>): T? {
    return JsonPath
        .using(jsonPathConfig)
        .parse(json)
        .read<T>(path)

}

inline fun <reified T> JsonObject.pathOrNull(path: String): T? {
    return try {
        JsonPath
            .using(
                jsonPathConfig
            )
            .parse(this)
            .read<T>(path)

    } catch (e: Throwable) {
        null
    }
}

inline fun <reified T> JsonArray.pathOrNull(path: String): T? {
    return try {
        JsonPath
            .using(
                jsonPathConfig
            )
            .parse(this)
            .read<T>(path)

    } catch (e: Throwable) {
        null
    }
}

