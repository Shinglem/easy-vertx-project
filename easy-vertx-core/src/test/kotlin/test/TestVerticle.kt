package test

import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.CoroutineVerticle

open class TestVerticle : CoroutineVerticle() {


    override suspend fun start() {

        println(config)
        println(Vertx.currentContext().config())
        println(Vertx.currentContext().isWorkerContext)
        println(Vertx.currentContext().instanceCount)




    }
}
