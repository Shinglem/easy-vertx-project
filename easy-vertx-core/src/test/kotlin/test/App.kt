package test

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.spi.json.JsonProvider
import com.jayway.jsonpath.spi.mapper.MappingProvider
import io.github.shinglem.easyvertx.core.json.VertxJsonMappingProvider
import io.github.shinglem.easyvertx.core.json.VertxJsonProvider
import io.github.shinglem.easyvertx.core.json.path
import io.vertx.core.json.JsonObject

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
    Configuration.setDefaults(object : Configuration.Defaults {

        private val jsonProvider = VertxJsonProvider();
        private val mappingProvider = VertxJsonMappingProvider();
        override fun jsonProvider(): JsonProvider {
            return jsonProvider
        }

        override fun options(): MutableSet<Option> {
            return mutableSetOf(Option.SUPPRESS_EXCEPTIONS)
        }

        override fun mappingProvider(): MappingProvider {
            return mappingProvider
        }

    })
    val a = json.path<JsonObject>("$.vertx.vertxOptions")
    println(a)
}


