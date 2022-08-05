package io.github.shinglem.easyvertx.core



import io.vertx.config.ConfigRetriever
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject

interface VertxProducer {
    fun vertx(): Vertx
    fun config(): Future<JsonObject>
    fun retriever(): ConfigRetriever
}

