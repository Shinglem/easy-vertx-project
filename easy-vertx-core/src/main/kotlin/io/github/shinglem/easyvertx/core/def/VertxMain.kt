package io.github.shinglem.easyvertx.core.def

import io.github.shinglem.easyvertx.core.ConfigLoader
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
import org.slf4j.LoggerFactory

open class VertxMain(configLoaders: MutableList<ConfigLoader> = mutableListOf(InnerConfigLoader() , OuterConfigLoader()),
                     val producer: VertxProducer = DefaultVertxProducer(configLoaders),
)  : Main {
    private final val logger = LoggerFactory.getLogger(this::class.java.name)


    private val verticleConfig = suspend { producer.config().await().path<JsonArray>("$.vertx.config.verticles") ?: JsonArray() }
    val vertx = producer.vertx()

    override fun start() {
        start {  }
    }

    override fun start(successHandle : ()->Unit) {
        runBlocking {
            try {
                logger.debug("-----deploy verticles-----")
                logger.debug("-----get verticleConfig-----")
                val vConfig = verticleConfig()
                logger.debug("-----get verticleConfig done ${vConfig.encodePrettily()}-----")
                vConfig.forEach {
                    logger.debug("current : $it")
                    val config = it as JsonObject
                    val optionsJson = config.getJsonObject("deploymentOptions") ?: JsonObject()
                    val options = DeploymentOptions(optionsJson)

                    logger.debug(
                """|
                   |class : ${config.getString("class")}
                   |options ：
                   |${optionsJson.encodePrettily().replace("\n" , "\n|")}
                """.trimMargin()
                    )
                    val serviceVerticleId = try {
                        vertx.deployVerticle(config.getString("class"), options).await()
                    } catch (e: Throwable) {
                        logger.error("start verticle error : class: ${config.getString("class")}" , e)
                        throw e
                    }

                    logger.info("${config.getString("class")} Start: id = [$serviceVerticleId ]")
                }

                successHandle()
            } catch (e: Throwable) {
                logger.error("" , e)
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

