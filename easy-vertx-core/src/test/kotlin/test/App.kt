package test

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider
import com.jayway.jsonpath.spi.json.JsonProvider
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider
import com.jayway.jsonpath.spi.mapper.MappingProvider
import io.github.shinglem.easyvertx.core.json.registerJsonMapper
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.json.jackson.DatabindCodec
import java.util.*

fun main(args: Array<String>) {
//    DefaultVertxMain().start()
    val jsonStr = """
{
  "vertx" : {
    "config" : {
      "verticles" : [ {
        "class" : "test.TestVerticle",
        "deploymentOptions" : {
          "instances" : 2,
          "config" : {
            "aaa" : "bbb"
          }
        }
      } ]
    }
  }
}
    """.trimIndent()

    val json = JsonObject(jsonStr)
    println(json.encodePrettily())
    registerJsonMapper()
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
//    val a = json.path<JsonObject>("$.vertx.config")

    val a = json.path<JsonArray>("$.vertx.config.verticles")
    println(a)
}

inline fun <reified T> JsonObject.path(path: String): T? {
    return try {
        val node = DatabindCodec.mapper().valueToTree<JsonNode>(this)
        val ret = JsonPath.parse(node).read<JsonNode>(path)

        DatabindCodec.mapper().let {
            it.readValue(it.treeAsTokens(ret), T::class.java)
        }

    } catch (e: Throwable) {
        null
    }
}

