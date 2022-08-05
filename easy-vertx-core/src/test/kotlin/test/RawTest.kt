package test

import io.github.shinglem.easyvertx.core.def.VertxMain
import io.vertx.core.Vertx
import io.vertx.core.impl.ContextInternal
import io.vertx.kotlin.coroutines.CoroutineVerticle
import java.util.Collections.synchronizedMap
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

}
