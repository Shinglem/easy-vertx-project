package test

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.core.impl.ContextInternal
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Collections.synchronizedMap
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

val countMap: MutableMap<String, Int> = synchronizedMap(mutableMapOf("count" to 0))


class RawTest : CoroutineVerticle() {

    override suspend fun start() {
        val context = Vertx.currentContext()
        synchronized(countMap) {

            if (countMap["count"]!! == 0) {
                println("initing ...")
            }else {
                println("inited")
                return@synchronized
            }

            println("countMap : $countMap")
            println("thread id : ${Thread.currentThread().id}")
            val count = countMap["count"]!!
            val nowCount = count + 1
            countMap.put("count", nowCount)
            context.put("initTimes", "AAAA")
            context.put("random", Random.nextDouble())
            context.put("count", nowCount)

//            println((context as ContextInternal).contextData())

        }
        println((context as ContextInternal).contextData())
//        }
    }

}


fun main() {
    Vertx.vertx().deployVerticle(RawTest::class.java, DeploymentOptions().setInstances(3))
}
