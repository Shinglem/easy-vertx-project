package io.github.shinglem.easyvertx.core.spring

import io.github.shinglem.easyvertx.core.*
import io.github.shinglem.easyvertx.core.def.ConfigLoader
import io.github.shinglem.easyvertx.core.def.Main
import io.github.shinglem.easyvertx.core.def.VertxProducer
import io.github.shinglem.easyvertx.core.def.json.path
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext

open class SpringVertxMain(val configLoader: ConfigLoader,
                           val producer: VertxProducer,
                           val applicationContext: ApplicationContext
) : Main {
    private final val logger = LoggerFactory.getLogger(this::class.java.name)
    private val verticleConfig = configLoader.config().path<JsonArray>("verticles") ?: JsonArray()
    val vertx = producer.vertx()

    init {

        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory")
        System.setProperty("user.timezone", "GMT +08")
        System.setProperty("kotlinx.coroutines.debug", "")
        System.setProperty("java.net.preferIPv4Stack", "true")

        vertx.registerVerticleFactory(SpringVerticleFactory())
        ApplicationContextProvider.applicationContext = applicationContext
    }


    override fun start() {
        start { }
    }
    private val prefix = SpringVerticleFactory.PREFIX
    override fun start(successHandle: () -> Unit) {
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
                val name = prefix+":"+config.getString("class")
                val serviceVerticleId = vertx.deployVerticle(name, options).await()

                logger.info("${config.getString("class")} Start: id = [$serviceVerticleId ]")
            }

            successHandle()

        }


    }

    override fun vertx(): Vertx {
        return vertx
    }
}
