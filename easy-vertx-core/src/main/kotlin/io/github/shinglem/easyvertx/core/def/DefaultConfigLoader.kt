package io.github.shinglem.easyvertx.core.def

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import io.github.shinglem.easyvertx.core.ConfigLoader
import io.github.shinglem.easyvertx.core.exception.ConfigNotLoadException

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import kotlin.properties.Delegates

open class DefaultConfigLoader : ConfigLoader {

    private val USER_DIR = System.getProperty("user.dir")
    private val PROFILE = System.getProperty("spring.profiles.active") ?: (System.getProperty("profiles.active") ?: "")


    private final val logger = LoggerFactory.getLogger(this::class.java.name)
    private var configCopy: JsonObject by Delegates.notNull()
    private var vertxConfig: JsonObject by Delegates.notNull()

    private val mapper = ObjectMapper(YAMLFactory())

    init {
        loadConfig()
    }


    override fun config(): JsonObject {
        return configCopy
    }


    override fun loadConfig() {
        runBlocking {
            logger.debug("----- load config -----")
            val tempVertx = Vertx.vertx()

            val fileName = "application${if (PROFILE.isNullOrEmpty()) "" else "-$PROFILE"}.yml"
            logger.debug("----- load inner config $fileName -----")
            val configInner = try {
                val file = tempVertx.fileSystem().readFile(fileName).await()
                val map = mapper.readValue(file.toString(), Map::class.java) as Map<String, *>
                val json = JsonObject(map)

                json
            } catch (e: Throwable) {
                logger.warn("error in find inner config")
                logger.debug("", e)
                JsonObject()
            }
            logger.debug("----- load outer config $USER_DIR/$fileName -----")

            val configOuter = try {
                val file = tempVertx.fileSystem().readFile("$USER_DIR/$fileName").await()
                val map = mapper.readValue(file.toString(), Map::class.java) as Map<String, *>
                val json = JsonObject(map)

                json
            } catch (e: Throwable) {
                logger.warn("error in find outer config")
                logger.debug("", e)
                JsonObject()
            }

            vertxConfig = configInner.mergeIn(configOuter)
            logger.debug("load config : ${vertxConfig.encodePrettily()}")

            configCopy = vertxConfig.copy()
        }
    }

    override fun mergeConfig(json: JsonObject) {
        vertxConfig.mergeIn(json) ?: throw ConfigNotLoadException("config not load")
        configCopy = vertxConfig.copy()
    }

}
