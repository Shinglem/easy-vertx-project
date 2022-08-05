package io.github.shinglem.easyvertx.web.core.handlers

import io.github.shinglem.easyvertx.util.classInfo.ParamHandler
import io.github.shinglem.easyvertx.web.core.impl.QueryParam
import io.vertx.ext.web.RoutingContext
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

open class QueryParamHandler : ParamHandler {
    override fun annotationKClass(): KClass<out Annotation> {
        return QueryParam::class
    }

    override fun resolve(context: MutableMap<String, Any?>, annotation: Annotation , paramContext : Any , param : KParameter) {
        val rc = paramContext as RoutingContext
        val anno = annotation as QueryParam
        val name = anno.value.let {
            if (it.isBlank()){
                param.name
            }else{
                it
            }
        }
        val value = rc.queryParam(name)

        context["paramMap"] = mutableMapOf(param to value)

    }

}
