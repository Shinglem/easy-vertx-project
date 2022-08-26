package io.github.shinglem.easyvertx.web.handler


import io.github.shinglem.easyvertx.web.PreFunctionHandler
import io.github.shinglem.easyvertx.web.core.impl.RawRouter
import io.github.shinglem.easyvertx.web.core.impl.Route
import io.vertx.ext.web.Router
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.full.isSubclassOf


open class RawRouterHandler : PreFunctionHandler {

    companion object {
        @JvmStatic
        val INSTANCE = RawRouterHandler()
    }

    override fun annotationKClass(): KClass<out Annotation> {
        return RawRouter::class
    }

    override fun resolve(metadata: MutableMap<String, Any?>, annotation: Annotation) {
        val instance = metadata.get("instance") as Any
        val router = metadata.get("router") as Router
        val scope = metadata.get("scope") as CoroutineScope
        val function = metadata.get("function") as KFunction<*>
        val parameterMap = function.parameters.associate {
            if (it.kind == KParameter.Kind.INSTANCE) {
                it to instance
            } else if (it.kind == KParameter.Kind.VALUE) {

                val paramClz = (it.type.classifier!! as KClass<*>)
                when {
                    paramClz.isSubclassOf(Router::class) -> {
                        it to router
                    }

                    paramClz.isSubclassOf(CoroutineScope::class) -> {
                        it to scope
                    }

                    else -> error("parameter type ${it.type} is not support , just Router and CoroutineScope")
                }
            } else {
                error("support only member function")
            }
        }

        if (function.isSuspend) {
            val job = scope.launch {
                function.callSuspendBy(parameterMap)
            }
            job.asCompletableFuture().get()
        } else {
            function.callBy(parameterMap)
        }


    }

}
