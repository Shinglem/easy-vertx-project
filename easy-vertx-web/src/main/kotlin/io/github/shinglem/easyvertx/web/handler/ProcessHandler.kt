package io.github.shinglem.easyvertx.web.handler

import io.github.shinglem.easyvertx.web.DoFunctionHandler
import io.github.shinglem.easyvertx.web.core.impl.DefaultDoFunction
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.full.instanceParameter
open class Processor : DoFunctionHandler {


    companion object {
        @JvmStatic
        val INSTANCE = Processor()
    }



    override fun annotationKClass(): KClass<out Annotation> {
        return DefaultDoFunction::class
    }

    @Suppress("UNCHECKED_CAST")
    override fun resolve(
        metadata: MutableMap<String , Any?>, annotation: Annotation
    ) {
        val instance = metadata.get("instance") as Any
        val scope = metadata.get("scope") as CoroutineScope
        val function = metadata.get("function") as KFunction<*>
        val parameterFuncMap = metadata.get("parameterFuncMap") as MutableMap<KParameter, (RoutingContext) -> Any?>

        val processor = { rc: RoutingContext ->
            val params = parameterFuncMap.map {
                val param = it.key
                val paramValue = it.value(rc)
                param to paramValue
            }.toMap(mutableMapOf())

            params.put(function.instanceParameter!! , instance)

            val deferred = scope.async {
                try {
                    function.callSuspendBy(params)
                } catch (e: Throwable) {
                    throw e
                }
            }
            Future.fromCompletionStage(
                deferred.asCompletableFuture()
            )
        }

        metadata.put("resultExecutor" , processor)

    }
}
