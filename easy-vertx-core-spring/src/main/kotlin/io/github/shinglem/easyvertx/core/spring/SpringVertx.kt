//package io.github.shinglem.easyvertx.core.spring
//
//import io.github.shinglem.easyvertx.core.*
//import io.github.shinglem.easyvertx.core.json.path
//import io.github.shinglem.easyvertx.core.json.registerJsonMapper
//import io.vertx.core.DeploymentOptions
//import io.vertx.core.Vertx
//import io.vertx.core.VertxOptions
//import io.vertx.core.json.JsonArray
//import io.vertx.core.json.JsonObject
//import io.vertx.kotlin.coroutines.await
//import kotlinx.coroutines.runBlocking
//import org.slf4j.LoggerFactory
//import java.net.InetAddress
//import java.util.concurrent.CountDownLatch
//import kotlin.reflect.full.createInstance
//private lateinit var up : VertxProducer
//private fun setProducer(producer: VertxProducer) {
//    if(!::up.isInitialized){
//        up = producer
//    }
//}
//
//private lateinit var cf : ConfigLoader
//private fun setConfigLoader(configLoader: ConfigLoader) {
//    if(!::cf.isInitialized){
//        cf = configLoader
//    }
//}
//
//class VertxMain(
//    val configLoader: ConfigLoader = DefaultConfigLoader(),
//    val producer: VertxProducer = VertxSingleProducer(configLoader)
//) : Main {
//    private final val logger = LoggerFactory.getLogger(this::class.java.name)
//
//    init {
//
//        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory")
//        System.setProperty("user.timezone", "GMT +08")
//        System.setProperty("kotlinx.coroutines.debug", "")
//        System.setProperty("java.net.preferIPv4Stack", "true")
//
//        configLoader
//        setConfigLoader(configLoader)
//        setProducer(producer)
//    }
//
//    private val verticleConfig = configLoader.config().path<JsonArray>("vertx.verticles") ?: JsonArray()
//    private val vertx = producer.vertx()
//
//    override fun start() {
//        start {  }
//    }
//
//    override fun start(successHandle : ()->Unit) {
//        return runBlocking {
//            logger.debug("-----deploy verticles-----")
//            verticleConfig.forEach {
//                val config = it as JsonObject
//                val optionsJson = config.getJsonObject("deploymentOptions") ?: JsonObject()
//                val options = DeploymentOptions(optionsJson)
//
//                logger.debug(
//                    """
//                    class : ${config.getString("class")}
//                    options ：
//                     ${optionsJson.encodePrettily()}
//                """.trimIndent()
//                )
//
//                val serviceVerticleId = vertx.deployVerticle(config.getString("class"), options).await()
//
//                logger.info("${config.getString("class")} Start: id = [$serviceVerticleId ]")
//            }
//
//            successHandle()
//
//        }
//
//
//    }
//
//
//}
//
//
//class VertxSingleProducer(val configLoader: ConfigLoader) : VertxProducer {
//    private final val logger = LoggerFactory.getLogger(this::class.java.name)
//
//    private var vertx: Vertx? = null
//    private val vertxOptions = configLoader.config().path<JsonObject>("vertx.options") ?: JsonObject()
//    private val classGettorName: String? = configLoader.config().path("vertx.ext.ClassGettor")
//
//    init {
//        classGettor()
//        vertxInit()
//    }
//
//
//    private fun localIp(): String {
//        val ip = InetAddress.getLocalHost().hostAddress
//        return ip
//    }
//
//
//    private fun vertxInit() {
//
//        logger.debug("----- init -----")
//
//        logger.debug("----- register json mapper -----")
//        registerJsonMapper()
//        logger.debug("----- get local ip -----")
//        val ip = localIp()
//        logger.debug("----- ip => $ip -----")
//        vertx = Vertx.vertx(VertxOptions(vertxOptions))
//
//
//        Runtime.getRuntime().addShutdownHook(Thread {
//            logger.info("start stop vertx");
//
//            val countDownLatch = CountDownLatch(1)
//            vertx!!.close {
//                countDownLatch.countDown()
//            }
//            try {
//                countDownLatch.await()
//                logger.info("stop vertx success");
//            } catch (e: Throwable) {
//                logger.error("", e)
//            }
//        })
//
//
//    }
//
//
//    override fun vertx(): Vertx {
//        return vertx!!
//    }
//
//    private lateinit var classGettorObj: ClassGettor
//    override fun classGettor(): ClassGettor {
//        if (::classGettorObj.isInitialized) {
//            return classGettorObj
//        }
//        if (classGettorName != null) {
//            classGettorObj = Class.forName(classGettorName).kotlin.createInstance() as ClassGettor
//        } else {
//            classGettorObj = DefaultClassGettor()
//        }
//
//        return classGettorObj
//    }
//
//
//}
