package io.github.shinglem.easyvertx.core.spring

import io.github.shinglem.easyvertx.core.def.exception.ConfigNotLoadException
import io.github.shinglem.easyvertx.core.def.ConfigLoader
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory
import kotlin.properties.Delegates

open class SpringVertxConfigLoader(
    private val configMap : Map<String , Any?>,
) : ConfigLoader {

    private final val logger = LoggerFactory.getLogger(this::class.java.name)

    private var configCopy: JsonObject by Delegates.notNull()
    private var vertxConfig: JsonObject by Delegates.notNull()
    init {
        loadConfig()
    }


    override fun mergeConfig(json: JsonObject) {
        vertxConfig.mergeIn(json) ?: throw ConfigNotLoadException("config not load")
        configCopy = vertxConfig.copy()
    }

    override fun loadConfig() {
        logger.debug("----- load spring vertx config -----")
        logger.debug(configMap.toString())
        vertxConfig = JsonObject(configMap)
        logger.debug("load config : ${vertxConfig.encodePrettily()}")

        configCopy = vertxConfig.copy()
    }

    override fun config(): JsonObject {
        return configCopy
    }
}

