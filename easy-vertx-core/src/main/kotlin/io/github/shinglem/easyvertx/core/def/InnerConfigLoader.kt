package io.github.shinglem.easyvertx.core.def

import io.github.shinglem.easyvertx.core.ConfigLoader
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.json.JsonObject


open class InnerConfigLoader: ConfigLoader {

    private val PROFILE = System.getProperty("profiles.active") ?: ""
    private var store: ConfigStoreOptions

    init {
        val store = ConfigStoreOptions()
            .setType("file")
            .setFormat("yaml")
            .setConfig(
                JsonObject()
                    .put("path", "application${if (PROFILE.isEmpty()) "" else "-$PROFILE"}.yml")
            )
            .setOptional(true)
        this.store = store
    }

    override fun store(): ConfigStoreOptions {
        return store
    }

    override fun order(): Int {
        return -Int.MAX_VALUE
    }


}
