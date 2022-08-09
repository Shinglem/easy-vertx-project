package maintest.utiltest

import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsFactory
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper
import io.vertx.core.DeploymentOptions
import io.vertx.core.json.JsonObject
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.util.Properties

open class UtilTest {
    @Test
    fun optTest() {
        val json = JsonObject().put("instances" , "2")
        val opt = json.mapTo(DeploymentOptions::class.java)
        println(opt.toJson().encodePrettily())
        println(DeploymentOptions().toJson().encodePrettily())
    }


}

fun main() {
//    val f = JavaPropsFactory.builder().build()
    val p = JavaPropsMapper()
    val pro = Properties()
        .apply {
            this.put("a.b", 1)
            this.setProperty("a.c", "C:\\b")
        }
//    val stream = ByteArrayOutputStream()
//    pro.store(stream , null)
//
//    val ret = stream.toString()
//    println(ret)
    val v = p.readPropertiesAs(pro, Map::class.java)

    println(v)

}
