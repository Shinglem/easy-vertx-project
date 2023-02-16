package test

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.spi.json.JsonProvider
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider
import com.jayway.jsonpath.spi.mapper.MappingProvider
import io.github.shinglem.easyvertx.core.json.VertxJsonProvider
import io.github.shinglem.easyvertx.core.json.path
import io.github.shinglem.easyvertx.core.json.registerJsonMapper
import io.vertx.core.json.JsonObject
import io.vertx.core.json.jackson.DatabindCodec
import org.junit.jupiter.api.Test
import java.util.*

class JsonTest {
    @Test
    fun jsonPathTest() {
        registerJsonMapper()
//        Configuration.setDefaults(object : Configuration.Defaults {
//
//            private val jsonProvider = VertxJsonProvider()
//            private val mappingProvider = JacksonMappingProvider(DatabindCodec.mapper())
//            override fun jsonProvider(): JsonProvider {
//                return jsonProvider
//            }
//
//            override fun options(): MutableSet<Option> {
//                return EnumSet.noneOf(Option::class.java)
//            }
//
//            override fun mappingProvider(): MappingProvider {
//                return mappingProvider
//            }
//
//        })
//
//        val json = JsonPath.parse(""" {"test" : "a"} """).read("$.test" , String::class.java)
//        println(json)

        val jsonObj = JsonObject(""" {"test" : "a"} """).path<String>("$.test")
        println(jsonObj)
    }
}
