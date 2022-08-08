@file:JvmName("JsonUtil")

package io.github.shinglem.easyvertx.core.json

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.spi.json.JsonProvider
import com.jayway.jsonpath.spi.mapper.MappingProvider
import io.vertx.core.json.JsonObject
import java.util.EnumSet
import kotlin.reflect.KClass


fun initJsonPath() {
    Configuration.setDefaults(object : Configuration.Defaults {

        private val jsonProvider = VertxJsonProvider();
        private val mappingProvider = VertxJsonMappingProvider();
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

fun <T : Any> JsonObject.path(path: String, clz: KClass<T>): T? {
    return try {
        JsonPath.parse(this)
            .read<T>(path)
    } catch (e: Throwable) {
        null
    }
}

fun <T : Any> JsonObject.path(path: String, clz: Class<T>): T? {
    return this.path(path, clz.kotlin)
}

inline fun <reified T : Any> JsonObject.path(path: String): T? {
    return this.path(path, T::class)
}


