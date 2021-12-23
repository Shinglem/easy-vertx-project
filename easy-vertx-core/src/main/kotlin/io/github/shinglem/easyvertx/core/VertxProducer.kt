package io.github.shinglem.easyvertx.core



import io.vertx.core.Vertx

interface VertxProducer {
    fun vertx(): Vertx
}

