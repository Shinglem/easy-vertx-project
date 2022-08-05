package io.github.shinglem.easyvertx.web.core.handlers

import io.github.shinglem.easyvertx.util.classInfo.AfterHandler
import io.github.shinglem.easyvertx.util.classInfo.AssembleKParamsInfo
import io.github.shinglem.easyvertx.util.classInfo.ProcessHandler
import io.github.shinglem.easyvertx.web.core.WebServerVerticle
import io.github.shinglem.easyvertx.web.core.impl.DefaultReturn
import io.github.shinglem.easyvertx.web.core.impl.RouteBase
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.Route
import kotlinx.coroutines.launch
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.full.instanceParameter

open class Processor : ProcessHandler {

    var defaultResultHandler = DefaultResultHandler()

    override fun resolve(
        context: MutableMap<String, Any?>,
        params: MutableList<AssembleKParamsInfo>,
        after: MutableMap<Annotation, AfterHandler>
    ) {
        val routes = context["routes"] as MutableList<io.vertx.ext.web.Route>
        routes.forEach { route ->


            route.handler { rc ->
                try {
                    val paramMap = context["paramMap"] as MutableMap<KParameter, Any?>? ?: mutableMapOf()
                    val ins = context["instance"]
                    val func = context["function"] as KFunction<*>
                    val scope = context["scopeVerticle"] as WebServerVerticle
                    scope.launch {
                        params.forEach {
                            val param = it.param
                            it.paramHandlerPairs.forEach {
                                it.value.resolve(context, it.key, rc, param)
                            }
                        }


                        paramMap[func.instanceParameter!!] = ins

                        val result = func.callSuspendBy(
                            paramMap
                        )
                        if (after.isEmpty()) {
                            val handler = defaultResultHandler
                            handler.resolve(context, DefaultReturn(), rc, result)
                        }
                        after.forEach {
                            it.value.resolve(context, it.key, rc, result)
                        }
                        rc.end(context["result"] as Buffer)


                    }
                } catch (e: Throwable) {
                    rc.fail(e)
                }

            }

        }
    }
}
