package io.github.shinglem.easyvertx.core.def

import io.github.shinglem.easyvertx.core.def.json.path
import io.github.shinglem.easyvertx.core.def.json.registerJsonMapper
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.json.JsonObject
import io.vertx.core.spi.cluster.ClusterManager
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.net.InetAddress
import java.util.concurrent.CountDownLatch

open class DefaultVertxProducer(
    private val configLoader: ConfigLoader,
    private val isCluster :Boolean = false,
    private val clusterManager: ClusterManager? =null
) : VertxProducer {
    private final val logger = LoggerFactory.getLogger(this::class.java.name)

    private var vertx: Vertx? = null
    private val vertxOptions = configLoader.config().path<JsonObject>("vertxOptions") ?: JsonObject()


    init {
        vertxInit()
    }


    private fun localIp(): String {
        val ip = InetAddress.getLocalHost().hostAddress
        return ip
    }


    private fun vertxInit() {

        logger.debug("----- init -----")

        logger.debug("----- register json mapper -----")
        registerJsonMapper()
        logger.debug("----- get local ip -----")
        val ip = localIp()
        logger.debug("----- ip => $ip -----")
        if (isCluster && clusterManager!=null) {
            runBlocking {
                logger.debug("----- start cluster vertx -----")
                vertx = Vertx.clusteredVertx(VertxOptions(vertxOptions).setClusterManager(clusterManager)).await()
            }
        } else {
            logger.debug("----- start single vertx -----")
            vertx = Vertx.vertx(VertxOptions(vertxOptions))
        }

        Runtime.getRuntime().addShutdownHook(Thread {
            logger.info("stop vertx");

            val countDownLatch = CountDownLatch(1)
            vertx!!.close {
                countDownLatch.countDown()
            }
            try {
                countDownLatch.await()
                logger.info("stop vertx success");
            } catch (e: Throwable) {
                logger.error("", e)
            }
        })


    }


    override fun vertx(): Vertx {
        return vertx!!
    }
}