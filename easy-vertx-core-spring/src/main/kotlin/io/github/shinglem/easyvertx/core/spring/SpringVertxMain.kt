package io.github.shinglem.easyvertx.core.spring

import io.github.shinglem.easyvertx.core.ConfigLoader
import io.github.shinglem.easyvertx.core.VertxProducer
import io.github.shinglem.easyvertx.core.def.DefaultVertxProducer
import io.github.shinglem.easyvertx.core.def.VertxMain
import io.vertx.core.json.JsonObject
import mu.KotlinLogging
import org.springframework.context.ApplicationContext


private val logger = KotlinLogging.logger {}

open class SpringVertxMain(
    val applicationContext: ApplicationContext,
    configLoaders: MutableList<ConfigLoader> = mutableListOf(SpringVertxConfigLoader(applicationContext)),
    producer: VertxProducer = DefaultVertxProducer(configLoaders),
) : VertxMain(configLoaders, producer) {

    private val prefix = SpringVerticleFactory.PREFIX
    override open protected val verticleClassName: (JsonObject) -> String = {
        val clz = it.getString("class")
        val name = "$prefix:$clz"
        name
    }

}
