package io.github.shinglem.easyvertx.core.def

import io.github.shinglem.easyvertx.core.ConfigLoader
import io.github.shinglem.easyvertx.core.Global
import io.github.shinglem.easyvertx.core.Main
import io.github.shinglem.easyvertx.core.VertxProducer
import io.github.shinglem.easyvertx.core.json.path
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

open class VertxMain(
    configLoaders: MutableList<ConfigLoader> = mutableListOf(InnerConfigLoader(), OuterConfigLoader()),
    val producer: VertxProducer = DefaultVertxProducer(configLoaders),
) : Main {


    protected val verticleConfig = Global.config.path<JsonArray>("$.vertx.config.verticles") ?: JsonArray()
    val vertx = producer.vertx()
    protected open val verticleClassName: (JsonObject)->String = { it.getString("class") }
    override fun start() {
        start { }
    }

    override fun start(successHandle: () -> Unit) {
        runBlocking {
            try {
                logger.debug { "-----deploy verticles-----" }
                logger.debug { "-----get verticleConfig-----" }
                val vConfig = verticleConfig
                logger.debug { "-----get verticleConfig done -----" }
                logger.debug { "verticle config : \n ${vConfig.encodePrettily()}" }
                vConfig.forEach {
                    logger.debug { "current : $it" }
                    val config = it as JsonObject
                    val optionsJson = config.getJsonObject("deploymentOptions") ?: JsonObject()
                    val options = optionsJson.mapTo(DeploymentOptions::class.java)
                    val name = verticleClassName(config)
                    logger.debug {
                        """|
                           |class : $name
                           |options ：
                           |${optionsJson.encodePrettily().replace("\n", "\n|")}
                           |""".trimMargin()
                    }
                    val serviceVerticleId = try {
                        vertx.deployVerticle(name, options).await()
                    } catch (e: Throwable) {
                        logger.error("start verticle error : class: $name", e)
                        throw e
                    }

                    logger.info("${config.getString("class")} Start: id = [$serviceVerticleId ]")
                }

                successHandle()
            } catch (e: Throwable) {
                logger.error("", e)
            }
        }


    }

    override fun vertx(): Vertx {
        return vertx
    }

    override fun config(): Future<JsonObject> {
        return producer.config()
    }


}

