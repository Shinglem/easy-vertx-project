package test

import io.vertx.core.json.JsonObject

fun main(args: Array<String>) {
//    DefaultVertxMain().start()
    val inner = mapOf("aaa" to  "bbb")
    val outer = mapOf("q" to inner)
    val json = JsonObject(outer)
    val a = json.getJsonObject("q")
    println(a.encodePrettily())
}


