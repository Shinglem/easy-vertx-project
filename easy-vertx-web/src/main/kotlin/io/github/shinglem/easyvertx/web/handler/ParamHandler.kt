package io.github.shinglem.easyvertx.web.handler

import io.github.shinglem.easyvertx.core.util.FunctionParameterHandler
import io.github.shinglem.easyvertx.web.core.impl.*
import io.github.shinglem.easyvertx.web.core.util.id.SnowFlake
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.ext.web.RoutingContext
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSubclassOf

open class QueryParamHandler : FunctionParameterHandler {

    companion object {
        @JvmStatic
        val INSTANCE = QueryParamHandler()
    }


    override fun annotationKClass(): KClass<out Annotation> {
        return QueryParam::class
    }

    @Suppress("UNCHECKED_CAST")
    override fun resolve(metadata: MutableMap<String, Any?>, annotation: Annotation) {
        val anno = annotation as QueryParam
        val parameter = metadata.get("parameter") as KParameter

        val paramName = anno.value.let {
            it.ifBlank {
                parameter.name ?: it
            }
        }

        val getValueFunc = { rc: RoutingContext ->
            val paramValue = rc.queryParam(paramName)
            val paramClz = (parameter.type.classifier!! as KClass<*>)
            val param = DatabindCodec.mapper().convertValue(paramValue, paramClz.javaObjectType)
            param
        }

        val parameterFuncMap = metadata.get("parameterFuncMap") ?: kotlin.run {
            metadata.put("parameterFuncMap", mutableMapOf<KParameter, (RoutingContext) -> Any?>())
        }

        parameterFuncMap as MutableMap<KParameter, (RoutingContext) -> Any?>
        parameterFuncMap.put(parameter, getValueFunc)

    }

}

open class PathParamHandler : FunctionParameterHandler {

    companion object {
        @JvmStatic
        val INSTANCE = PathParamHandler()
    }


    override fun annotationKClass(): KClass<out Annotation> {
        return PathParam::class
    }

    @Suppress("UNCHECKED_CAST")
    override fun resolve(metadata: MutableMap<String, Any?>, annotation: Annotation) {
        val anno = annotation as PathParam
        val parameter = metadata.get("parameter") as KParameter

        val paramName = anno.value.let {
            it.ifBlank {
                parameter.name ?: it
            }
        }

        val getValueFunc = Func@{ rc: RoutingContext ->
            val paramValue = rc.pathParam(paramName) ?: return@Func null
            val paramClz = (parameter.type.classifier!! as KClass<*>)
            val param = DatabindCodec.mapper().convertValue(paramValue, paramClz.javaObjectType)
            return@Func param
        }

        val parameterFuncMap = metadata.get("parameterFuncMap") ?: kotlin.run {
            metadata.put("parameterFuncMap", mutableMapOf<KParameter, (RoutingContext) -> Any?>())
        }

        parameterFuncMap as MutableMap<KParameter, (RoutingContext) -> Any?>
        parameterFuncMap.put(parameter, getValueFunc)

    }

}


open class FormParamHandler : FunctionParameterHandler {

    companion object {
        @JvmStatic
        val INSTANCE = FormParamHandler()
    }


    override fun annotationKClass(): KClass<out Annotation> {
        return FormParam::class
    }

    @Suppress("UNCHECKED_CAST")
    override fun resolve(metadata: MutableMap<String, Any?>, annotation: Annotation) {
        val anno = annotation as FormParam
        val parameter = metadata.get("parameter") as KParameter

        val paramName = anno.value.let {
            it.ifBlank {
                parameter.name ?: it
            }
        }

        val getValueFunc = Func@{ rc: RoutingContext ->
            val paramValue = rc.request().getFormAttribute(paramName) ?: return@Func null
            val paramClz = (parameter.type.classifier!! as KClass<*>)
            val param = DatabindCodec.mapper().convertValue(paramValue, paramClz.javaObjectType)
            return@Func param
        }

        val parameterFuncMap = metadata.get("parameterFuncMap") ?: kotlin.run {
            metadata.put("parameterFuncMap", mutableMapOf<KParameter, (RoutingContext) -> Any?>())
        }

        parameterFuncMap as MutableMap<KParameter, (RoutingContext) -> Any?>
        parameterFuncMap.put(parameter, getValueFunc)

    }

}


open class BodyParamHandler : FunctionParameterHandler {

    companion object {
        @JvmStatic
        val INSTANCE = BodyParamHandler()
    }


    override fun annotationKClass(): KClass<out Annotation> {
        return BodyParam::class
    }

    @Suppress("UNCHECKED_CAST")
    override fun resolve(metadata: MutableMap<String, Any?>, annotation: Annotation) {
        val anno = annotation as BodyParam
        val parameter = metadata.get("parameter") as KParameter

        val paramName = anno.value.let {
            it.ifBlank {
                parameter.name ?: it
            }
        }

        val getValueFunc = Func@{ rc: RoutingContext ->
            val paramValue = rc.body().asJsonObject().getValue(paramName) ?: return@Func null
            val paramClz = (parameter.type.classifier!! as KClass<*>)
            val param = DatabindCodec.mapper().convertValue(paramValue, paramClz.javaObjectType)
            return@Func param
        }

        val parameterFuncMap = metadata.get("parameterFuncMap") ?: kotlin.run {
            metadata.put("parameterFuncMap", mutableMapOf<KParameter, (RoutingContext) -> Any?>())
        }

        parameterFuncMap as MutableMap<KParameter, (RoutingContext) -> Any?>
        parameterFuncMap.put(parameter, getValueFunc)

    }

}

open class BodyHandler : FunctionParameterHandler {

    companion object {
        @JvmStatic
        val INSTANCE = BodyHandler()
    }


    override fun annotationKClass(): KClass<out Annotation> {
        return Body::class
    }

    @Suppress("UNCHECKED_CAST")
    override fun resolve(metadata: MutableMap<String, Any?>, annotation: Annotation) {
        val parameter = metadata.get("parameter") as KParameter


        val getValueFunc = Func@{ rc: RoutingContext ->
            val paramValue = rc.body() ?: return@Func null
            val paramClz = (parameter.type.classifier!! as KClass<*>)

            val param = when {
                paramClz.isSubclassOf(List::class) -> {
                    paramValue.asJsonArray().list
                }

                paramClz.isSubclassOf(Array::class) -> {
                    paramValue.asJsonArray().list.toTypedArray()
                }

                paramClz.isSubclassOf(JsonArray::class) -> {
                    paramValue.asJsonArray()
                }

                paramClz.isSubclassOf(JsonObject::class) -> {
                    paramValue.asJsonObject()
                }

                else -> {
                    paramValue.asPojo(paramClz.javaObjectType)
                }
            }

            return@Func param
        }

        val parameterFuncMap = metadata.get("parameterFuncMap") ?: kotlin.run {
            metadata.put("parameterFuncMap", mutableMapOf<KParameter, (RoutingContext) -> Any?>())
        }

        parameterFuncMap as MutableMap<KParameter, (RoutingContext) -> Any?>
        parameterFuncMap.put(parameter, getValueFunc)

    }

}


open class FormHandler : FunctionParameterHandler {

    companion object {
        @JvmStatic
        val INSTANCE = FormHandler()
    }


    override fun annotationKClass(): KClass<out Annotation> {
        return Body::class
    }

    @Suppress("UNCHECKED_CAST")
    override fun resolve(metadata: MutableMap<String, Any?>, annotation: Annotation) {
        val parameter = metadata.get("parameter") as KParameter


        val getValueFunc = Func@{ rc: RoutingContext ->
            val paramValue = rc.request().formAttributes() ?: return@Func null
            val paramClz = (parameter.type.classifier!! as KClass<*>)

            val param = when {
                paramClz.isSubclassOf(MutableMap::class) -> {
                    paramValue
                }

                else -> {
                    error("form parameter need type MultiMap")
                }
            }

            return@Func param
        }

        val parameterFuncMap = metadata.get("parameterFuncMap") ?: kotlin.run {
            metadata.put("parameterFuncMap", mutableMapOf<KParameter, (RoutingContext) -> Any?>())
        }

        parameterFuncMap as MutableMap<KParameter, (RoutingContext) -> Any?>
        parameterFuncMap.put(parameter, getValueFunc)

    }

}


open class ParamHandler : FunctionParameterHandler {

    companion object {
        @JvmStatic
        val INSTANCE = ParamHandler()
    }


    override fun annotationKClass(): KClass<out Annotation> {
        return Param::class
    }

    @Suppress("UNCHECKED_CAST")
    override fun resolve(metadata: MutableMap<String, Any?>, annotation: Annotation) {
        val anno = annotation as Param
        val parameter = metadata.get("parameter") as KParameter

        val paramName = anno.value.let {
            it.ifBlank {
                parameter.name ?: it
            }
        }

        val getValueFunc = Func@{ rc: RoutingContext ->
            val paramValue = rc.request().getParam(paramName) ?: return@Func null
            val paramClz = (parameter.type.classifier!! as KClass<*>)
            val param = DatabindCodec.mapper().convertValue(paramValue, paramClz.javaObjectType)
            return@Func param
        }

        val parameterFuncMap = metadata.get("parameterFuncMap") ?: kotlin.run {
            metadata.put("parameterFuncMap", mutableMapOf<KParameter, (RoutingContext) -> Any?>())
        }

        parameterFuncMap as MutableMap<KParameter, (RoutingContext) -> Any?>
        parameterFuncMap.put(parameter, getValueFunc)

    }

}


open class ParamsHandler : FunctionParameterHandler {

    companion object {
        @JvmStatic
        val INSTANCE = ParamsHandler()
    }


    override fun annotationKClass(): KClass<out Annotation> {
        return Params::class
    }

    @Suppress("UNCHECKED_CAST")
    override fun resolve(metadata: MutableMap<String, Any?>, annotation: Annotation) {
        val parameter = metadata.get("parameter") as KParameter


        val getValueFunc = Func@{ rc: RoutingContext ->
            val paramValue = rc.request().params() ?: return@Func null
            val paramClz = (parameter.type.classifier!! as KClass<*>)

            val param = when {
                paramClz.isSubclassOf(MutableMap::class) -> {
                    paramValue
                }

                else -> {
                    error("form parameter need type MultiMap")
                }
            }

            return@Func param
        }

        val parameterFuncMap = metadata.get("parameterFuncMap") ?: kotlin.run {
            metadata.put("parameterFuncMap", mutableMapOf<KParameter, (RoutingContext) -> Any?>())
        }

        parameterFuncMap as MutableMap<KParameter, (RoutingContext) -> Any?>
        parameterFuncMap.put(parameter, getValueFunc)

    }

}


open class HeadersHandler : FunctionParameterHandler {

    companion object {
        @JvmStatic
        val INSTANCE = HeadersHandler()
    }


    override fun annotationKClass(): KClass<out Annotation> {
        return Headers::class
    }

    @Suppress("UNCHECKED_CAST")
    override fun resolve(metadata: MutableMap<String, Any?>, annotation: Annotation) {
        val parameter = metadata.get("parameter") as KParameter


        val getValueFunc = Func@{ rc: RoutingContext ->
            val paramValue = rc.request().headers() ?: return@Func null
            val paramClz = (parameter.type.classifier!! as KClass<*>)

            val param = when {
                paramClz.isSubclassOf(MutableMap::class) -> {
                    paramValue
                }

                else -> {
                    error("form parameter need type MultiMap")
                }
            }

            return@Func param
        }

        val parameterFuncMap = metadata.get("parameterFuncMap") ?: kotlin.run {
            metadata.put("parameterFuncMap", mutableMapOf<KParameter, (RoutingContext) -> Any?>())
        }

        parameterFuncMap as MutableMap<KParameter, (RoutingContext) -> Any?>
        parameterFuncMap.put(parameter, getValueFunc)

    }

}

open class HeaderHandler : FunctionParameterHandler {

    companion object {
        @JvmStatic
        val INSTANCE = ParamHandler()
    }


    override fun annotationKClass(): KClass<out Annotation> {
        return Header::class
    }

    @Suppress("UNCHECKED_CAST")
    override fun resolve(metadata: MutableMap<String, Any?>, annotation: Annotation) {
        val anno = annotation as Header
        val parameter = metadata.get("parameter") as KParameter

        val paramName = anno.value.let {
            it.ifBlank {
                parameter.name ?: it
            }
        }

        val getValueFunc = Func@{ rc: RoutingContext ->
            val paramValue = rc.request().getHeader(paramName) ?: return@Func null
            val paramClz = (parameter.type.classifier!! as KClass<*>)
            val param = DatabindCodec.mapper().convertValue(paramValue, paramClz.javaObjectType)
            return@Func param
        }

        val parameterFuncMap = metadata.get("parameterFuncMap") ?: kotlin.run {
            metadata.put("parameterFuncMap", mutableMapOf<KParameter, (RoutingContext) -> Any?>())
        }

        parameterFuncMap as MutableMap<KParameter, (RoutingContext) -> Any?>
        parameterFuncMap.put(parameter, getValueFunc)

    }

}


open class RouteContextHandler : FunctionParameterHandler {

    companion object {
        @JvmStatic
        val INSTANCE = RouteContextHandler()
    }


    override fun annotationKClass(): KClass<out Annotation> {
        return Context::class
    }

    @Suppress("UNCHECKED_CAST")
    override fun resolve(metadata: MutableMap<String, Any?>, annotation: Annotation) {
        val parameter = metadata.get("parameter") as KParameter


        val getValueFunc = Func@{ rc: RoutingContext ->
            val paramValue = rc
            val paramClz = (parameter.type.classifier!! as KClass<*>)

            val param = when {
                paramClz.isSubclassOf(RoutingContext::class) -> {
                    paramValue
                }

                else -> {
                    error("form parameter need type RoutingContext")
                }
            }

            return@Func param
        }

        val parameterFuncMap = metadata.get("parameterFuncMap") ?: kotlin.run {
            metadata.put("parameterFuncMap", mutableMapOf<KParameter, (RoutingContext) -> Any?>())
        }

        parameterFuncMap as MutableMap<KParameter, (RoutingContext) -> Any?>
        parameterFuncMap.put(parameter, getValueFunc)

    }

}

open class HttpRequestHandler : FunctionParameterHandler {

    companion object {
        @JvmStatic
        val INSTANCE = HttpRequestHandler()
    }


    override fun annotationKClass(): KClass<out Annotation> {
        return HttpRequest::class
    }

    @Suppress("UNCHECKED_CAST")
    override fun resolve(metadata: MutableMap<String, Any?>, annotation: Annotation) {
        val parameter = metadata.get("parameter") as KParameter


        val getValueFunc = Func@{ rc: RoutingContext ->
            val paramValue = rc.request()
            val paramClz = (parameter.type.classifier!! as KClass<*>)

            val param = when {
                paramClz.isSubclassOf(RoutingContext::class) -> {
                    paramValue
                }

                else -> {
                    error("form parameter need type HttpServerRequest")
                }
            }

            return@Func param
        }

        val parameterFuncMap = metadata.get("parameterFuncMap") ?: kotlin.run {
            metadata.put("parameterFuncMap", mutableMapOf<KParameter, (RoutingContext) -> Any?>())
        }

        parameterFuncMap as MutableMap<KParameter, (RoutingContext) -> Any?>
        parameterFuncMap.put(parameter, getValueFunc)

    }

}

open class HttpResponseHandler : FunctionParameterHandler {

    companion object {
        @JvmStatic
        val INSTANCE = HttpResponseHandler()
    }


    override fun annotationKClass(): KClass<out Annotation> {
        return HttpResponse::class
    }

    @Suppress("UNCHECKED_CAST")
    override fun resolve(metadata: MutableMap<String, Any?>, annotation: Annotation) {
        val parameter = metadata.get("parameter") as KParameter


        val getValueFunc = Func@{ rc: RoutingContext ->
            val paramValue = rc.response()
            val paramClz = (parameter.type.classifier!! as KClass<*>)

            val param = when {
                paramClz.isSubclassOf(RoutingContext::class) -> {
                    paramValue
                }

                else -> {
                    error("form parameter need type HttpServerRequest")
                }
            }

            return@Func param
        }

        val parameterFuncMap = metadata.get("parameterFuncMap") ?: kotlin.run {
            metadata.put("parameterFuncMap", mutableMapOf<KParameter, (RoutingContext) -> Any?>())
        }

        parameterFuncMap as MutableMap<KParameter, (RoutingContext) -> Any?>
        parameterFuncMap.put(parameter, getValueFunc)

    }

}


open class IdHandler : FunctionParameterHandler {

    companion object {
        @JvmStatic
        val INSTANCE = IdHandler()
    }

    open val snowFlake = SnowFlake()

    override fun annotationKClass(): KClass<out Annotation> {
        return Id::class
    }

    @Suppress("UNCHECKED_CAST")
    override fun resolve(metadata: MutableMap<String, Any?>, annotation: Annotation) {
        val parameter = metadata.get("parameter") as KParameter


        val getValueFunc = Func@{ rc: RoutingContext ->
            val paramValue = snowFlake.nextId()
            rc.put("request_id" , paramValue)
            val paramClz = (parameter.type.classifier!! as KClass<*>)

            val param = when {
                paramClz.isSubclassOf(Long::class) -> {
                    paramValue
                }

                else -> {
                    error("id parameter need type Long")
                }
            }

            return@Func param
        }

        val parameterFuncMap = metadata.get("parameterFuncMap") ?: kotlin.run {
            metadata.put("parameterFuncMap", mutableMapOf<KParameter, (RoutingContext) -> Any?>())
        }

        parameterFuncMap as MutableMap<KParameter, (RoutingContext) -> Any?>
        parameterFuncMap.put(parameter, getValueFunc)

    }

}
