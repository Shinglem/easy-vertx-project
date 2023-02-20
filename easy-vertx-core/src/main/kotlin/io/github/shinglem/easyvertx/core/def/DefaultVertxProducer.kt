package io.github.shinglem.easyvertx.core.def

import io.github.shinglem.easyvertx.core.ConfigLoader
import io.github.shinglem.easyvertx.core.Global
import io.github.shinglem.easyvertx.core.VertxProducer
import io.github.shinglem.easyvertx.core.json.initJsonPath
import io.github.shinglem.easyvertx.core.json.path
import io.github.shinglem.easyvertx.core.json.path0
import io.github.shinglem.easyvertx.core.json.registerJsonMapper
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.json.JsonObject
import io.vertx.core.spi.cluster.ClusterManager
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import java.net.InetAddress
import java.util.concurrent.CountDownLatch

private val logger = KotlinLogging.logger {}

open class DefaultVertxProducer(
    private val configLoaders: List<ConfigLoader> = mutableListOf(InnerProfileConfigLoader(), OuterProfileConfigLoader()),
    private val configRetrieverOptions: ConfigRetrieverOptions = ConfigRetrieverOptions().setScanPeriod(-1),
    private val isCluster: Boolean = false,
    private val clusterManager: ClusterManager? = null,
    private val vertxOptionsApply:(VertxOptions)->VertxOptions = {it},
    private val preDo: MutableList<() -> Unit> = mutableListOf(::registerJsonMapper, ::initJsonPath),
    private val afterDo: MutableList<() -> Unit> = mutableListOf(),
) : VertxProducer {

    private val retriever: ConfigRetriever

    private val vertx: Vertx

    private val vertxOptions by lazy {
        config().map {
            it.path<JsonObject>("$.vertx.vertxOptions") ?: JsonObject()
        }
    }

    init {
        logger.debug { "----- set config loader -----" }
        val configVertx = Vertx.vertx()
        Runtime.getRuntime().addShutdownHook(Thread {
            logger.info("stop config vertx")

            val countDownLatch = CountDownLatch(1)
            configVertx.close {
                countDownLatch.countDown()
            }
            try {
                countDownLatch.await()
                logger.info("stop config vertx success");
            } catch (e: Throwable) {
                logger.error("", e)
            }
        })

        val sortedList = configLoaders.sortedByDescending { it.order() }

        sortedList.forEach {
            configRetrieverOptions.addStore(it.store())
        }
        val retriever = ConfigRetriever.create(configVertx, configRetrieverOptions)
        this.retriever = retriever
        logger.debug { "try load config ..." }
        runBlocking {
            Global.config = retriever.config.await()
        }
        logger.debug { "load config success ... ${Global.config.encodePrettily()}" }

        this.vertx = vertxInit()

    }

    override fun config(): Future<JsonObject> {
        return retriever.config
            .onFailure {
                logger.error("load config error : ", it)
            }
    }

    override fun retriever(): ConfigRetriever {
        return retriever
    }


    private fun localIp(): String {
        val ip = InetAddress.getLocalHost().hostAddress
        return ip
    }


    private fun vertxInit(): Vertx {
        logger.debug { "----- init -----" }

        val vertx: Vertx
        logger.debug { "----- init preDo -----" }
        preDo.forEach(Function0<Unit>::invoke)
        logger.info { "----- get local ip -----" }
        val ip = localIp()
        logger.info { "----- ip => $ip -----" }



        runBlocking {
            if (isCluster && clusterManager != null) {

                logger.debug { "----- start cluster vertx -----" }
                val options = vertxOptionsApply(VertxOptions(vertxOptions.await()).setClusterManager(clusterManager))
                vertx =
                    Vertx.clusteredVertx(options).await()

            } else {

                logger.debug { "----- start single vertx -----" }
                val opt = vertxOptionsApply(VertxOptions(vertxOptions.await()))
                vertx = Vertx.vertx(opt)

            }
        }

        afterDo.forEach(Function0<Unit>::invoke)
        Runtime.getRuntime().addShutdownHook(Thread {
            logger.info("stop vertx");

            val countDownLatch = CountDownLatch(1)
            vertx.close {
                countDownLatch.countDown()
            }
            try {
                countDownLatch.await()
                logger.info("stop vertx success");
            } catch (e: Throwable) {
                logger.error("", e)
            }
        })
        Global.vertx = vertx
        return vertx

    }


    override fun vertx(): Vertx {
        return vertx
    }
}
