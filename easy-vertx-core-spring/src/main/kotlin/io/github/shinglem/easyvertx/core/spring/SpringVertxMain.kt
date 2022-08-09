package io.github.shinglem.easyvertx.core.spring

import io.github.shinglem.easyvertx.core.ConfigLoader
import io.github.shinglem.easyvertx.core.Global
import io.github.shinglem.easyvertx.core.Main
import io.github.shinglem.easyvertx.core.VertxProducer
import io.github.shinglem.easyvertx.core.def.DefaultVertxProducer
import io.github.shinglem.easyvertx.core.def.InnerConfigLoader
import io.github.shinglem.easyvertx.core.def.OuterConfigLoader
import io.github.shinglem.easyvertx.core.def.VertxMain
import io.github.shinglem.easyvertx.core.json.path
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.slf4j.LoggerFactory
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
