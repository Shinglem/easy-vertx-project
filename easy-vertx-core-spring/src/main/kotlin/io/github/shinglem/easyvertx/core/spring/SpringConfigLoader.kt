package io.github.shinglem.easyvertx.core.spring

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper
import io.github.shinglem.easyvertx.core.ConfigLoader
import io.vertx.config.ConfigStoreOptions
import io.vertx.config.spi.ConfigStore
import io.vertx.config.spi.ConfigStoreFactory
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import mu.KotlinLogging
import org.springframework.context.ApplicationContext
import org.springframework.core.env.AbstractEnvironment
import org.springframework.core.env.MapPropertySource
import java.util.*


private val logger = KotlinLogging.logger {}

open class SpringVertxConfigLoader(
    private val ctx: ApplicationContext,
) : ConfigLoader {

    private var store: ConfigStoreOptions

    init {
        val store = ConfigStoreOptions()
            .setType("spring_config")
            .setConfig(
                JsonObject()
                    .put("applicationContext", ctx)
            )
            .setOptional(true)
        this.store = store
    }

    override fun store(): ConfigStoreOptions {
        return store
    }

    override fun order(): Int = -Int.MAX_VALUE


}

open class SpringConfigStore(vertx: Vertx, config: JsonObject) : ConfigStore {
    private val ctx: ApplicationContext

    init {
        ctx = config.getValue("applicationContext") as ApplicationContext
    }

    override fun close(): Future<Void> {
        return Future.succeededFuture()
    }

    override fun get(): Future<Buffer> {
        try {
            val config = getConfigJson(ctx)
            return Future.succeededFuture(config.toBuffer())
        } catch (e: Throwable) {
            logger.error(e) { "get config from spring error" }
            return Future.failedFuture(e)
        }
    }


    private fun getConfigJson(ctx: ApplicationContext): JsonObject {
        val c = ctx.environment as AbstractEnvironment
        val properties = Properties()
        val tempMap = mutableMapOf<String, Any?>()
        c.propertySources
            .filterIsInstance<MapPropertySource>()
            .forEach { source ->
                source.propertyNames
                    .forEach {
                        val value = source.getProperty(it)
                        if (value != null) {
                            properties.setProperty(it, value.toString())
                        }
                    }
            }
        val mapper = JavaPropsMapper()
        val map = mapper.readPropertiesAs(properties, Map::class.java) as Map<String, Any?>
        val json = JsonObject(map)
        return json
    }
}

open class SpringConfigStoreFactory : ConfigStoreFactory {
    override fun name(): String {
        return "spring_config"
    }

    override fun create(vertx: Vertx, configuration: JsonObject): ConfigStore {
        return SpringConfigStore(vertx, configuration)
    }
}
