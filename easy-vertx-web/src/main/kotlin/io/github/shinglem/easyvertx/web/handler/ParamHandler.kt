package io.github.shinglem.easyvertx.web.handler

import io.github.shinglem.easyvertx.core.util.FunctionParameterHandler
import io.github.shinglem.easyvertx.web.core.impl.*
import io.github.shinglem.easyvertx.web.core.util.id.SnowFlake
import io.vertx.core.MultiMap
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.ext.web.RoutingContext
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.javaType


@OptIn(ExperimentalStdlibApi::class)
fun typeConvert(buf: Buffer, type: KType): Any? {
    val parser = DatabindCodec.createParser(buf)
    val ret = parser.use {
        DatabindCodec.mapper().let { mapper ->
            mapper.readValue<Any?>(parser, mapper.constructType(type.javaType))
        }
    }
    return ret
}

@OptIn(ExperimentalStdlibApi::class)
fun typeConvert(raw: Any?, type: KType): Any? {
    val ret = DatabindCodec.mapper().let { mapper ->
        mapper.convertValue<Any?>(raw, mapper.constructType(type.javaType))
    }
    return ret
}

fun resolveValue(value: Any?, parameter: KParameter, def: KClass<*>? = null): Any? {
    if (value == null) {
        return null
    }
    return when {
        (def != null) && value::class.starProjectedType.isSubtypeOf(parameter.type) -> {
            value
        }

        else -> {

            when(value){
                is Buffer -> {
                    val paramType = parameter.type
                    val param = typeConvert(value, paramType)
                    param
                }
                is MultiMap -> {
                    value.names().associateWith {
                        value[it]
                    }.let {
                        val paramType = parameter.type
                        val param = typeConvert(it, paramType)
                        param
                    }
                }

                else -> {
                    val paramType = parameter.type
                    val param = typeConvert(value, paramType)
                    param
                }
            }

        }
    }
}

inline fun <reified V> getValueFunc(parameter : KParameter, crossinline getFunc : (RoutingContext)->V?) : (RoutingContext)->Any?{
    return Func@{ rc: RoutingContext ->
        val value = getFunc(rc) ?: return@Func null
        val param = resolveValue(value , parameter  , V::class)
        return@Func param
    }
}


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

        val getValueFunc = getValueFunc(parameter) { rc: RoutingContext ->
            rc.queryParam(paramName)
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
        val getValueFunc = getValueFunc(parameter) { rc: RoutingContext ->
            rc.pathParam(paramName)
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
        val getValueFunc = getValueFunc(parameter) { rc: RoutingContext ->
            rc.request().getFormAttribute(paramName)
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
        val getValueFunc = getValueFunc(parameter) { rc: RoutingContext ->
            rc.body().asJsonObject().getValue(paramName)
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

        val getValueFunc = getValueFunc(parameter) { rc: RoutingContext ->
            rc.body().buffer()
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
        return Form::class
    }

    @Suppress("UNCHECKED_CAST")
    override fun resolve(metadata: MutableMap<String, Any?>, annotation: Annotation) {
        val parameter = metadata.get("parameter") as KParameter

        val getValueFunc = getValueFunc(parameter) { rc: RoutingContext ->
            rc.request().formAttributes()
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
        val getValueFunc = getValueFunc(parameter) { rc: RoutingContext ->
            rc.request().getParam(paramName)
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

        val getValueFunc = getValueFunc(parameter) { rc: RoutingContext ->
            rc.request().params()
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

        val getValueFunc = getValueFunc(parameter) { rc: RoutingContext ->
            rc.request().headers()
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
        val getValueFunc = getValueFunc(parameter) { rc: RoutingContext ->
            rc.request().getHeader(paramName)
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

        val getValueFunc = getValueFunc(parameter) { rc: RoutingContext ->
            rc
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

        val getValueFunc = getValueFunc(parameter) { rc: RoutingContext ->
            rc.request()
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

        val getValueFunc = getValueFunc(parameter) { rc: RoutingContext ->
            rc.response()
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

        val getValueFunc = getValueFunc(parameter) { rc: RoutingContext ->
            val paramValue = snowFlake.nextId()
            rc.put("request_id", paramValue)
            paramValue
        }

        val parameterFuncMap = metadata.get("parameterFuncMap") ?: kotlin.run {
            metadata.put("parameterFuncMap", mutableMapOf<KParameter, (RoutingContext) -> Any?>())
        }

        parameterFuncMap as MutableMap<KParameter, (RoutingContext) -> Any?>
        parameterFuncMap.put(parameter, getValueFunc)

    }

}
