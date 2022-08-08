package io.github.shinglem.easyvertx.core.json

import com.fasterxml.jackson.core.type.TypeReference
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.InvalidJsonException
import com.jayway.jsonpath.TypeRef
import com.jayway.jsonpath.spi.json.AbstractJsonProvider
import com.jayway.jsonpath.spi.mapper.MappingProvider
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.Json
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.json.jackson.DatabindCodec
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*


@Suppress("UNCHECKED_CAST")
open class VertxJsonMappingProvider() :
    MappingProvider {

    override fun <T> map(source: Any?, targetType: Class<T>, configuration: Configuration): T? {
        if (source == null) {
            return null
        }
        return Json.CODEC.fromValue<T>(source, targetType)
    }

    override fun <T> map(source: Any?, targetType: TypeRef<T>, configuration: Configuration): T? {
        if (source == null) {
            return null
        }
        val typeRef = object : TypeReference<T>() {}
        val codec = Json.CODEC as DatabindCodec
        return codec.fromValue(source, typeRef)
    }
}

@Suppress("UNCHECKED_CAST")
open class VertxJsonProvider() :
    AbstractJsonProvider() {

    @Throws(InvalidJsonException::class)
    override fun parse(json: String): Any {
        return try {
            Json.CODEC.fromString(json, Any::class.java)
        } catch (e: IOException) {
            throw InvalidJsonException(e, json)
        }
    }

    @Throws(InvalidJsonException::class)
    override fun parse(json: ByteArray): Any {
        return try {
            Json.CODEC.fromBuffer(Buffer.buffer(json), Any::class.java)
        } catch (e: IOException) {
            throw InvalidJsonException(e, String(json, StandardCharsets.UTF_8))
        }
    }

    @Throws(InvalidJsonException::class)
    override fun parse(jsonStream: InputStream, charset: String): Any {
        return try {
            DatabindCodec.fromParser(DatabindCodec.mapper().factory.createParser(jsonStream), Any::class.java)
        } catch (e: IOException) {
            throw InvalidJsonException(e)
        }
    }

    override fun toJson(obj: Any): String {
        return try {
            Json.CODEC.toString(obj)
        } catch (e: IOException) {
            throw InvalidJsonException(e)
        }
    }

    override fun createArray(): List<Any?> {
        return LinkedList()
    }

    override fun createMap(): Any {
        return LinkedHashMap<String, Any?>()
    }

    override fun isArray(obj: Any): Boolean {
        return obj is JsonArray || obj is List<*>
    }

    override fun getArrayIndex(array: Any, index: Int): Any? {
        if (!isArray(array)) {
            throw UnsupportedOperationException()
        }

        val result = when (array) {
            is MutableList<*> -> (array as MutableList<Any?>).get(index)
            is JsonArray -> array.getValue(index)
            else -> throw UnsupportedOperationException()
        }
        return result
    }

    override fun setArrayIndex(array: Any, index: Int, newValue: Any?) {
        if (!isArray(array)) {
            throw UnsupportedOperationException()
        }

        when (array) {
            is MutableList<*> -> (array as MutableList<Any?>).set(index, newValue)
            is JsonArray -> array.set(index, newValue)
            else -> throw UnsupportedOperationException()
        }
    }

    override fun getMapValue(obj: Any, key: String): Any? {

        if (!isMap(obj)) {
            throw UnsupportedOperationException()
        }

        val result = when (obj) {
            is MutableMap<* , *> -> (obj as MutableMap<String , Any?>).get(key)
            is JsonObject -> obj.getValue(key)
            else -> throw UnsupportedOperationException()
        }
        return result
    }

    override fun setProperty(obj: Any, key: Any, value: Any) {

        if (!isMap(obj) && !isArray(obj)) {
            throw UnsupportedOperationException()
        }

        if (isMap(obj)) {
            when (obj) {
                is JsonObject -> obj.put(key.toString(), value)
                is MutableMap<*, *> -> (obj as MutableMap<Any, Any?>).put(key.toString(), value)
                else -> throw UnsupportedOperationException()
            }
        } else {

            when (obj) {
                is JsonArray -> {
                    val index: Int = if (key != null) {
                        if (key is Int) key else key.toString().toInt()
                    } else {
                        obj.size()
                    }

                    if (index == obj.size()) {
                        obj.add(value)
                    } else {
                        obj.set(index, value)
                    }
                }

                is MutableList<*> -> {
                    obj as MutableList<Any?>
                    val index: Int = if (key != null) {
                        if (key is Int) key else key.toString().toInt()
                    } else {
                        obj.size
                    }

                    if (index == obj.size) {
                        obj.add(value)
                    } else {
                        obj.set(index, value)
                    }
                }

                else -> throw UnsupportedOperationException()
            }

        }
    }

    override fun removeProperty(obj: Any, key: Any) {

        if (!isMap(obj) && !isArray(obj)) {
            throw UnsupportedOperationException()
        }

        if (isMap(obj)) {
            when (obj) {
                is JsonObject -> obj.remove(key.toString())
                is MutableMap<*, *> -> (obj as MutableMap<Any, Any?>).remove(key.toString())
                else -> throw UnsupportedOperationException()
            }
        } else {

            when (obj) {
                is JsonArray -> {
                    val index: Int = if (key != null) {
                        if (key is Int) key else key.toString().toInt()
                    } else {
                        obj.size()
                    }

                    obj.remove(index)
                }

                is MutableList<*> -> {
                    obj as MutableList<Any?>
                    val index: Int = if (key != null) {
                        if (key is Int) key else key.toString().toInt()
                    } else {
                        obj.size
                    }

                    obj.remove(index)
                }

                else -> throw UnsupportedOperationException()
            }

        }
    }


    override fun isMap(obj: Any?): Boolean {
        return obj is JsonObject || obj is MutableMap<*, *>
    }

    override fun getPropertyKeys(obj: Any): Collection<String> {
        if (!isMap(obj)) {
            throw UnsupportedOperationException()
        }
        val keys = when (obj) {
            is JsonObject -> obj.fieldNames()
            is MutableMap<*, *> -> (obj as MutableMap<String, Any?>).keys
            else -> throw UnsupportedOperationException()
        }
        return keys
    }

    override fun length(array: Any): Int {
        if (!isArray(array)) {
            throw UnsupportedOperationException()
        }

        return when (array) {
            is MutableList<*> -> (array as MutableList<Any?>).size
            is JsonArray -> array.size()
            else -> throw com.jayway.jsonpath.JsonPathException("length operation can not applied to " + if (array != null) array.javaClass.name else "null")
        }

    }

    override fun toIterable(array: Any): Iterable<Any?> {
        if (!isArray(array)) {
            throw UnsupportedOperationException()
        }
        return when (array) {
            is MutableList<*> -> (array as MutableList<Any?>)
            is JsonArray -> array.list
            else -> throw UnsupportedOperationException()
        }
    }

}
