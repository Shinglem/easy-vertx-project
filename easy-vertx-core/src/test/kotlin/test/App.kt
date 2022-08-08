package test

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.spi.json.JsonProvider
import com.jayway.jsonpath.spi.mapper.MappingProvider
import io.github.shinglem.easyvertx.core.json.VertxJsonMappingProvider
import io.github.shinglem.easyvertx.core.json.VertxJsonProvider
import io.vertx.core.json.JsonObject

fun main(args: Array<String>) {
//    DefaultVertxMain().start()
    val jsonStr = """
        {
            "store": {
                "book": [
                    {
                        "category": "reference",
                        "author": "Nigel Rees",
                        "title": "Sayings of the Century",
                        "price": 8.95
                    },
                    {
                        "category": "fiction",
                        "author": "Evelyn Waugh",
                        "title": "Sword of Honour",
                        "price": 12.99
                    },
                    {
                        "category": "fiction",
                        "author": "Herman Melville",
                        "title": "Moby Dick",
                        "isbn": "0-553-21311-3",
                        "price": 8.99
                    },
                    {
                        "category": "fiction",
                        "author": "J. R. R. Tolkien",
                        "title": "The Lord of the Rings",
                        "isbn": "0-395-19395-8",
                        "price": 22.99
                    }
                ],
                "bicycle": {
                    "color": "red",
                    "price": 19.95
                }
            },
            "expensive": 10
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
    val a = JsonPath
        .parse(jsonStr)
        .read<List<String>>("$.store.book[*].author")
    println(a)
}


