package io.github.shinglem.easyvertx.core.spring

import io.github.shinglem.easyvertx.core.ConfigLoader
import io.github.shinglem.easyvertx.core.Main
import io.github.shinglem.easyvertx.core.VertxProducer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct


@Configuration
//@EnableConfigurationProperties(ConfigMap::class)
open class VertxBean {

    private final val logger = LoggerFactory.getLogger(this::class.java.name)

//    @Autowired
//    private lateinit var configMap: ConfigMap

    @Autowired
    private lateinit var springVertxConfigLoader: ConfigLoader


    @Autowired
    private lateinit var producer: VertxProducer

    @Autowired
    private lateinit var configLoader: ConfigLoader

    @Autowired
    private lateinit var vertxMain: Main

    @Bean
    open fun vertxProducer(): VertxProducer {
        val producer = SpringVertxProducer(springVertxConfigLoader)
        return producer
    }

    @Bean
    open fun springConfigLoader(applicationContext: ApplicationContext): ConfigLoader {
        val configMap = getConfigMap(applicationContext , "vertx")
        return SpringVertxConfigLoader(configMap)
    }

    @Bean
    open fun springVertxMain(applicationContext: ApplicationContext): Main {
        return SpringVertxMain(configLoader, producer, applicationContext)
    }

    @PostConstruct
    open fun initVertx() {
        logger.debug("post construct")
//        logger.debug(configMap.getConfigMap().toString())
        logger.debug(springVertxConfigLoader.config().encodePrettily())
        vertxMain.start()
    }
}


//@ConfigurationProperties(prefix = "vertx")
//open class ConfigMap {
//    private final val logger = LoggerFactory.getLogger(this::class.java.name)
//    private var maps: Map<String, String> by Delegates.notNull()
//    private val mapConfig: Map<String, Any?> by lazy {
//        val propstr = maps.map {
//            it.key + "=" + it.value
//        }.joinToString("\n") ?: ""
//        val mapper = JavaPropsMapper()
//        val map = mapper.readValue(propstr, Map::class.java) as Map<String, *>
//        map
//    }
//
//    open fun setConfig(maps: Map<String, String>) {
//        this.maps = maps
//    }
//
//    open fun getConfigMap(): Map<String, Any?> {
//
//        return mapConfig
//    }
//}
