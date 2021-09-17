package io.github.shinglem.easyvertx.core.spring

import io.github.shinglem.easyvertx.core.def.json.path
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.JsonObject
import org.springframework.context.ApplicationContext

fun getHttpServerOptions(applicationContext: ApplicationContext, prefix: String) : HttpServerOptions {
    val map = getConfigMap(applicationContext , prefix)
    val json = JsonObject(map)
    val options = HttpServerOptions(json).apply {
        val port = json.path<Any>("SpringWebVerticle.httpServerOptions.port")
        if (port != null) {
            if (port is Number) {
                this.setPort(port.toInt())
            }else{
                this.setPort(port.toString().toInt())
            }
        }
    }
    return options
}
