package io.github.shinglem.easyvertx.core.def

import io.github.shinglem.easyvertx.core.def.json.path
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

open class DefaultVertxMain(val configLoader: ConfigLoader = DefaultConfigLoader(),
                            val producer: VertxProducer = DefaultVertxProducer(configLoader),
)  : Main {
    private final val logger = LoggerFactory.getLogger(this::class.java.name)

    init {

        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory")
        System.setProperty("user.timezone", "GMT +08")
        System.setProperty("kotlinx.coroutines.debug", "")
        System.setProperty("java.net.preferIPv4Stack", "true")

    }

    private val verticleConfig = configLoader.config().path<JsonArray>("vertx.config.verticles") ?: JsonArray()
    val vertx = producer.vertx()

    override fun start() {
        start {  }
    }

    override fun start(successHandle : ()->Unit) {
        return runBlocking {
            logger.debug("-----deploy verticles-----")
            verticleConfig.forEach {
                val config = it as JsonObject
                val optionsJson = config.getJsonObject("deploymentOptions") ?: JsonObject()
                val options = DeploymentOptions(optionsJson)

                logger.debug(
                    """
                    class : ${config.getString("class")}
                    options ：
                     ${optionsJson.encodePrettily()}
                """.trimIndent()
                )

                val serviceVerticleId = vertx.deployVerticle(config.getString("class"), options).await()

                logger.info("${config.getString("class")} Start: id = [$serviceVerticleId ]")
            }

            successHandle()

        }


    }

    override fun vertx(): Vertx {
        return vertx
    }


}

