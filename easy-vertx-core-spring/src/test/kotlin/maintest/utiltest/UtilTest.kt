package maintest.utiltest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsFactory

class UtilTest {
}

fun main() {
    val f = JavaPropsFactory()
    val p = ObjectMapper(f)

    val v = p.readValue("verticles.0.class=com.xy.us.verticles.UsServerVerticle\nverticles.0.deploymentOptions.instances=1" ,  Map::class.java) as Map<String, *>

    println(v)

}