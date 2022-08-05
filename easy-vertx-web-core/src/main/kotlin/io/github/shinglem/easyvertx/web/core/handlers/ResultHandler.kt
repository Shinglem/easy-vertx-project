package io.github.shinglem.easyvertx.web.core.handlers

import io.github.shinglem.easyvertx.util.classInfo.AfterHandler
import io.github.shinglem.easyvertx.web.core.impl.DefaultReturn
import io.vertx.core.buffer.Buffer
import kotlin.reflect.KClass

open class DefaultResultHandler : AfterHandler {
    override fun resolve(context: MutableMap<String, Any?>, annotation: Annotation,  resultContext : Any , rawResult : Any?) {

        if(rawResult is Unit){
            context["result"] = Buffer.buffer()
            return
        }

        context["result"] = Buffer.buffer(rawResult.toString())
    }

    override fun annotationKClass(): KClass<out Annotation> {
        return DefaultReturn::class
    }

}
