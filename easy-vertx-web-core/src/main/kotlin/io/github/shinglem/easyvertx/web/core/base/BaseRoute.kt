package io.github.shinglem.easyvertx.web.core.base

import io.github.shinglem.easyvertx.web.core.InitRouter
import io.github.shinglem.easyvertx.web.core.impl.RootLevel
import io.github.shinglem.easyvertx.web.core.util.id.IdInterface
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.LoggerHandler
import org.slf4j.LoggerFactory


open class BaseRoute {
    private val logger = LoggerFactory.getLogger(this::class.java.name)

    private val id = IdInterface.create()

    @InitRouter
    fun defaultRoute(router: Router) {
        router.route().order(RootLevel).handler(BodyHandler.create())
        router.route().order(RootLevel).handler(LoggerHandler.create())
        router.route().order(RootLevel).handler { rc ->
            logger.info("[rootRouter]")
            val response = rc.response()
            rc.put("internalId", id.nextId().toString())
            response.isChunked = true
            rc.next()
        }
    }


}

open class RestfulRoute(val restPath: String = "/api/*") {
    private val logger = LoggerFactory.getLogger(this::class.java.name)

    @InitRouter
    fun restRoute(router: Router) {
        router.route(restPath).order(RootLevel).handler { rc ->
            logger.info("[rootRouter]")
            val response = rc.response()
            response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            rc.next()
        }
        router.route().failureHandler {
            logger.info("[failure Handler]");
            val errorCode = it.statusCode()

            when {

                httpErrorCodeMap.containsKey(errorCode.toString()) -> it.next()

                else -> {
                    logger.info("[$errorCode fail]");
                    it.response().statusCode = errorCode
                    if (it.request().method() != HttpMethod.HEAD) {
                        // If it's a 404 let's send a body too
                        it.response()
                            .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                            .end(
                                JsonObject.mapFrom(
                                    ResponseEntity(
                                        errorCode,
                                        "Internal Server Error",
                                        JsonObject().put("error", it.failure())
                                    )
                                ).encodePrettily()
                            )
                    } else {
                        it.response().end()
                    }
                }
            }


        }

        httpErrorCodeMap.forEach { key, value ->

            router.errorHandler(key.toInt()) {
                logger.info("[${key} handler]");
                it.response().statusCode = key.toInt()
                if (it.request().method() != HttpMethod.HEAD) {
                    it.response()
                        .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                        .end(
                            JsonObject.mapFrom(
                                ResponseEntity(
                                    key.toInt(),
                                    value,
                                    JsonObject().put("error", it.failure())
                                )
                            ).encodePrettily()
                        )
                } else {
                    it.response().end()
                }
            }
        }
    }
}

data class ResponseEntity(
    var code: Int? = null,
    var message: String? = null,
    var data: Any? = null
) {}


private val httpErrorCodeMap = mapOf(
    "400" to "Bad Request",
    "401" to "Unauthorized",
    "402" to "Payment Required",
    "403" to "Forbidden",
    "404" to "Not Found",
    "405" to "Method Not Allowed",
    "406" to "Not Acceptable",
    "407" to "Proxy Authentication Required",
    "408" to "Request Time-out",
    "409" to "Conflict",
    "410" to "Gone",
    "411" to "Length Required",
    "412" to "Precondition Failed",
    "413" to "Request Entity Too Large",
    "414" to "Request-URI Too Large",
    "415" to "Unsupported Media Type",
    "416" to "Requested range not satisfiable",
    "417" to "Expectation Failed",
    "500" to "Internal Server Error",
    "501" to "Not Implemented",
    "502" to "Bad Gateway",
    "503" to "Service Unavailable",
    "504" to "Gateway Time-out",
    "505" to "HTTP Version not supported",
)

