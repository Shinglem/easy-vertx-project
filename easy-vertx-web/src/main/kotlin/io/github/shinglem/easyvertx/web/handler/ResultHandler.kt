package io.github.shinglem.easyvertx.web.handler

import io.github.shinglem.easyvertx.web.ReturnHandler
import io.github.shinglem.easyvertx.web.core.impl.DefaultReturn
import io.github.shinglem.easyvertx.web.core.impl.Route
import io.github.shinglem.easyvertx.web.core.impl.Route.HandlerType.*
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.reflect.KClass


open class DefaultReturnHandler : ReturnHandler {

    companion object {
        @JvmStatic
        val INSTANCE = DefaultReturnHandler()
    }

    @Suppress("UNCHECKED_CAST")
    override fun resolve(metadata: MutableMap<String, Any?>, annotation: Annotation) {
        val routes = metadata.get("routes") as MutableList<io.vertx.ext.web.Route>
        val handlerType = metadata.get("handlerType") as Route.HandlerType
        val scope = metadata.get("scope") as CoroutineScope
        val resultExecutor = metadata.get("resultExecutor") as (RoutingContext) -> Future<Any?>
        val handler: suspend (RoutingContext) -> Unit = { rc: RoutingContext ->
            val rawResult = resultExecutor(rc).await()
            val result = when (rawResult) {
                is Unit, is Void -> ""
                else -> rawResult.toString()
            }

            if (!rc.response().ended()) {
                rc.end(result)
            }
        }

        routes.forEach {
            when (handlerType) {
                NORMAL -> {
                    it.handler {
                        scope.launch {
                            handler(it)
                        }
                    }
                }

                BLOCKING -> {
                    it.blockingHandler {

                        runBlocking {
                            handler(it)
                        }

                    }
                }

                FAILURE -> {
                    it.failureHandler {
                        scope.launch {
                            handler(it)
                        }
                    }
                }
            }

        }
    }

    override fun annotationKClass(): KClass<out Annotation> {
        return DefaultReturn::class
    }

}
