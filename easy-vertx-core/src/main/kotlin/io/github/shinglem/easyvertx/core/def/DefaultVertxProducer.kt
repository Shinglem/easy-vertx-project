package io.github.shinglem.easyvertx.core.def

import io.github.shinglem.easyvertx.core.ConfigLoader
import io.github.shinglem.easyvertx.core.Global
import io.github.shinglem.easyvertx.core.VertxProducer
import io.github.shinglem.easyvertx.core.json.path
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
import org.slf4j.LoggerFactory
import java.net.InetAddress
import java.util.concurrent.CountDownLatch



open class DefaultVertxProducer(
    private val configLoaders: List<ConfigLoader>,
    private val configScanPeriod: Long = 0,
    private val isCluster: Boolean = false,
    private val clusterManager: ClusterManager? = null,
    private val preDo : MutableList<()->Unit> = mutableListOf(::registerJsonMapper),
    private val afterDo : MutableList<()->Unit> = mutableListOf(),
) : VertxProducer {
    private final val logger = LoggerFactory.getLogger(this::class.java.name)

    private val retriever: ConfigRetriever

    init {
        logger.debug("----- set config loader -----")
        val configVertx = Vertx.vertx()
        Runtime.getRuntime().addShutdownHook(Thread {
            logger.info("stop config vertx");

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
        val options = ConfigRetrieverOptions()
        if (configScanPeriod > 0) {
            options.scanPeriod = configScanPeriod
        }

        sortedList.forEach {
            options.addStore(it.store())
        }
        val retriever = ConfigRetriever.create(configVertx, options)
        this.retriever = retriever
        Global.configGetter = {retriever.config}

    }

    override fun config(): Future<JsonObject> {
        return retriever.config
            .onFailure {
                logger.error("load config error : " , it)
            }
    }

    override fun retriever(): ConfigRetriever {
        return retriever
    }


    private val vertx: Vertx by lazy {
        vertxInit()
    }

    private val vertxOptions = suspend {
       val opt = config().await().path<JsonObject>("vertx.config.vertxOptions") ?: JsonObject()
       opt
    }


    private fun localIp(): String {
        val ip = InetAddress.getLocalHost().hostAddress
        return ip
    }


    private fun vertxInit(): Vertx {
        logger.debug("----- init -----")

        val vertx: Vertx
        logger.debug("----- register json mapper -----")
        preDo.forEach(Function0<Unit>::invoke)
        logger.debug("----- get local ip -----")
        val ip = localIp()
        logger.debug("----- ip => $ip -----")



        runBlocking {
            logger.debug("----- load config init -----")
            logger.debug("config => ${config().await().encodePrettily()}")
            if (isCluster && clusterManager != null) {

                logger.debug("----- start cluster vertx -----")
                vertx = Vertx.clusteredVertx(VertxOptions(vertxOptions()).setClusterManager(clusterManager)).await()

            } else {

                logger.debug("----- start single vertx -----")
                vertx = Vertx.vertx(VertxOptions(vertxOptions()))

            }
        }

        afterDo.forEach (Function0<Unit>::invoke)
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
