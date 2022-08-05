package test

import io.github.shinglem.easyvertx.core.def.VertxMain
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.junit.jupiter.api.Test

open class TestVerticle : CoroutineVerticle() {


    override suspend fun start() {

        println(config)
        println(Vertx.currentContext().config())
        println(Vertx.currentContext().isWorkerContext)
        println(Vertx.currentContext().instanceCount)




    }
}


open class TestMain {


    @Test
    open fun testMain() {
        VertxMain().start()
    }


}
