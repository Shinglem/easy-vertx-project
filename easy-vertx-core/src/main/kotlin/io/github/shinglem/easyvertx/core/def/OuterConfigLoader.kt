package io.github.shinglem.easyvertx.core.def

import io.github.shinglem.easyvertx.core.ConfigLoader
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.json.JsonObject


open class OuterConfigLoader : ConfigLoader {
    private val USER_DIR = System.getProperty("user.dir")
    private val PROFILE = System.getProperty("profiles.active") ?: ""
    var store: ConfigStoreOptions
        private set

    init {
        val fileName = "application${if (PROFILE.isEmpty()) "" else "-$PROFILE"}.yml"
        val store = ConfigStoreOptions()
            .setType("file")
            .setFormat("yaml")
            .setConfig(
                JsonObject()
                    .put("path", "$USER_DIR/$fileName")
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
