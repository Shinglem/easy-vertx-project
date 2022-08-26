package webtest.routebase

import io.vertx.core.buffer.Buffer
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import webtest.TestBase

class RouteBaseTest : TestBase() {

    @Test
    fun emptyRoute() {
        runBlocking {
            testRequest()
                .send()
                .await()
                .body()
                .also {
                    println("----- test emptyRoute -----")
                    println("===response===")
                    println((it as Any?).toString())
                    println("==============")
                    assertEquals(
                        null,
                        it
                    ) {
                        "----- test emptyRoute fail-----"
                    }
                }
        }
    }

    @Test
    fun emptyRouteBase() {
        runBlocking {
            testRequest("/emptyRouteBase")
                .send()
                .await()
                .body()
                .also {
                    println("----- test emptyRouteBase -----")
                    println("===response===")
                    println((it as Any?).toString())
                    println("==============")
                    assertEquals(
                        Buffer.buffer("emptyRouteBase"),
                        it
                    ) {
                        "----- test emptyRouteBase fail-----"
                    }
                }
        }
    }

    @Test
    fun notEmpty() {
        runBlocking {
            testRequest("/notEmpty")
                .send()
                .await()
                .body()
                .also {
                    println("----- test notEmpty -----")
                    println("===response===")
                    println((it as Any?).toString())
                    println("==============")
                    assertEquals(
                        Buffer.buffer("notEmpty"),
                        it
                    ) {
                        "----- test notEmpty fail-----"
                    }
                }
        }
    }

    @Test
    fun notEmptyTest() {
        runBlocking {
            testRequest("/notEmpty/notEmptyTest")
                .send()
                .await()
                .body()
                .also {
                    println("----- test notEmptyTest -----")
                    println("===response===")
                    println((it as Any?).toString())
                    println("==============")
                    assertEquals(
                        Buffer.buffer("notEmptyTest"),
                        it
                    ) {
                        "----- test notEmptyTest fail-----"
                    }
                }
        }
    }

    @Test
    fun routesTest() {
        runBlocking {
            val r1 = kotlin.runCatching {
                testRequest("/routes/1")
                    .send()
                    .await()
                    .body()
                    .also {
                        println("----- test routes/1 -----")
                        println("===response===")
                        println((it as Any?).toString())
                        println("==============")
                        assertEquals(
                            Buffer.buffer("routes"),
                            it
                        ) {
                            "----- test routes/1 fail-----"
                        }
                    }
            }

            val r2 = kotlin.runCatching {
                testRequest("/routes/2")
                    .send()
                    .await()
                    .body()
                    .also {
                        println("----- test routes/2 -----")
                        println("===response===")
                        println((it as Any?).toString())
                        println("==============")
                        assertEquals(
                            Buffer.buffer("routes"),
                            it
                        ) {
                            "----- test routes/2 fail-----"
                        }
                    }
            }
            if (r1.isFailure || r2.isFailure) {
                error("failed ")
            }

        }
    }

}
