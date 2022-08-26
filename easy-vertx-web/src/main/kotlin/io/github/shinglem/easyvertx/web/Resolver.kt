package io.github.shinglem.easyvertx.web

import io.github.shinglem.easyvertx.web.core.impl.DefaultDoFunction
import io.github.shinglem.easyvertx.web.core.impl.DefaultReturn
import io.github.shinglem.easyvertx.web.handler.*
import io.vertx.ext.web.RoutingContext
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberFunctions



@JvmOverloads
fun resolveBuild(
    preClassHandlers: List<PreClassHandler> = mutableListOf(
        RouteBaseHandler.INSTANCE
    ),
    doClassHandlers: List<DoClassHandler> = mutableListOf(),
    afterClassHandlers: List<AfterClassHandler> = mutableListOf(),

    preFunctionHandlers: List<PreFunctionHandler> = mutableListOf(
        RoutesHandler.INSTANCE, RouteHandler.INSTANCE
    ),
    doFunctionHandlers: List<DoFunctionHandler> = mutableListOf(),
    afterFunctionHandlers: List<AfterFunctionHandler> = mutableListOf(),

    preParamHandlers: List<PreParamHandler> = mutableListOf(
        QueryParamHandler.INSTANCE,
        PathParamHandler.INSTANCE,
        FormParamHandler.INSTANCE,
        BodyParamHandler.INSTANCE,
        BodyHandler.INSTANCE,
        FormHandler.INSTANCE,
        ParamHandler.INSTANCE,
        ParamsHandler.INSTANCE,
        HeadersHandler.INSTANCE,
        HeaderHandler.INSTANCE,
        RouteContextHandler.INSTANCE,
        HttpRequestHandler.INSTANCE,
        HttpResponseHandler.INSTANCE,
        IdHandler.INSTANCE
    ),
    doParamHandlers: List<DoParamHandler> = mutableListOf(),
    afterParamHandlers: List<AfterParamHandler> = mutableListOf(),

    returnHandlers: List<ReturnHandler> = mutableListOf(),
    defaultReturnHandler: ReturnHandler = DefaultReturnHandler.INSTANCE,
    defaultDoFunctionHandler: DoFunctionHandler = Processor.INSTANCE,
) = Resolver(
    preClassHandlers,
    doClassHandlers,
    afterClassHandlers,
    preFunctionHandlers,
    doFunctionHandlers,
    afterFunctionHandlers,
    preParamHandlers,
    doParamHandlers,
    afterParamHandlers,
    returnHandlers,
    defaultReturnHandler,
    defaultDoFunctionHandler
)

class Resolver constructor(
    preClassHandlers: List<PreClassHandler>,
    doClassHandlers: List<DoClassHandler>,
    afterClassHandlers: List<AfterClassHandler>,

    preFunctionHandlers: List<PreFunctionHandler>,
    doFunctionHandlers: List<DoFunctionHandler>,
    afterFunctionHandlers: List<AfterFunctionHandler>,

    preParamHandlers: List<PreParamHandler>,
    doParamHandlers: List<DoParamHandler>,
    afterParamHandlers: List<AfterParamHandler>,

    returnHandlers: List<ReturnHandler>,
    val defaultReturnHandler: ReturnHandler,
    val defaultDoFunctionHandler: DoFunctionHandler,

    ) {

    val preClassHandlerMap = preClassHandlers.sortedBy { it.order() }.associateBy { it.annotationKClass() }
    val doClassHandlerMap = doClassHandlers.sortedBy { it.order() }.associateBy { it.annotationKClass() }
    val afterClassHandlerMap = afterClassHandlers.sortedBy { it.order() }.associateBy { it.annotationKClass() }

    val preFunctionHandlerMap = preFunctionHandlers.sortedBy { it.order() }.associateBy { it.annotationKClass() }
    val doFunctionHandlerMap = doFunctionHandlers.sortedBy { it.order() }.associateBy { it.annotationKClass() }
    val afterFunctionHandlerMap = afterFunctionHandlers.sortedBy { it.order() }.associateBy { it.annotationKClass() }

    val preParamHandlerMap = preParamHandlers.sortedBy { it.order() }.associateBy { it.annotationKClass() }
    val doParamHandlerMap = doParamHandlers.sortedBy { it.order() }.associateBy { it.annotationKClass() }
    val afterParamHandlerMap = afterParamHandlers.sortedBy { it.order() }.associateBy { it.annotationKClass() }

    val returnHandlerMap = returnHandlers.sortedBy { it.order() }.associateBy { it.annotationKClass() }


    fun resolve(metaData: MutableMap<String, Any?>, clz: Any) {
        val classMetadata = mutableMapOf<String, Any?>().apply { this.putAll(metaData) }
        classMetadata.put("instance", clz)
        val classHandlerMap = mutableMapOf<String, MutableList<() -> Unit>>().apply {
            this.put("pre", mutableListOf())
            this.put("do", mutableListOf())
            this.put("after", mutableListOf())
        }
        clz::class.annotations.forEach { ann ->

            val a = preClassHandlerMap.get(ann.annotationClass)


            preClassHandlerMap.get(ann.annotationClass)?.let {
                val handlerFunc = { it.resolve(classMetadata, ann) }
                classHandlerMap.get("pre")!!.add(handlerFunc)
            }
            doClassHandlerMap.get(ann.annotationClass)?.let {
                val handlerFunc = { it.resolve(classMetadata, ann) }
                classHandlerMap.get("do")!!.add(handlerFunc)
            }
            afterClassHandlerMap.get(ann.annotationClass)?.let {
                val handlerFunc = { it.resolve(classMetadata, ann) }
                classHandlerMap.get("after")!!.add(handlerFunc)
            }

        }
        classHandlerMap.get("pre")!!.forEach {
            it()
        }
        classHandlerMap.get("do")!!.forEach {
            it()
        }
        classHandlerMap.get("after")!!.forEach {
            it()
        }

        clz::class.memberFunctions.forEach {
            val functionMetadata = mutableMapOf<String, Any?>().apply { this.putAll(classMetadata) }
            functionMetadata.put("function", it)
            functionMetadata.put("parameterFuncMap", mutableMapOf<KParameter, (RoutingContext) -> Any?>())
            val functionHandlerMap = mutableMapOf<String, MutableList<() -> Unit>>().apply {
                this.put("pre", mutableListOf())
                this.put("do", mutableListOf())
                this.put("after", mutableListOf())
            }


            val returnHandlerList = mutableListOf<() -> Unit>()
            val defaultReturnHandle = { defaultReturnHandler.resolve(functionMetadata, DefaultReturn()) }
            val defaultDoFunctionHandle = { defaultDoFunctionHandler.resolve(functionMetadata, DefaultDoFunction()) }


            it.annotations.forEach { ann ->
                preFunctionHandlerMap.get(ann.annotationClass)?.let {
                    val handlerFunc = { it.resolve(functionMetadata, ann) }
                    functionHandlerMap.get("pre")!!.add(handlerFunc)
                }
                doFunctionHandlerMap.get(ann.annotationClass)?.let {
                    val handlerFunc = { it.resolve(functionMetadata, ann) }
                    functionHandlerMap.get("do")!!.add(handlerFunc)
                }
                afterFunctionHandlerMap.get(ann.annotationClass)?.let {
                    val handlerFunc = { it.resolve(functionMetadata, ann) }
                    functionHandlerMap.get("after")!!.add(handlerFunc)
                }

                this.returnHandlerMap.get(ann.annotationClass)?.let {
                    val handlerFunc = { it.resolve(functionMetadata, ann) }
                    returnHandlerList.add(handlerFunc)
                }
            }

            functionHandlerMap.get("pre")!!.apply {
                if (this.isEmpty()) {
                    return@forEach
                } else {
                    functionHandlerMap.get("pre")!!.forEach {
                        it()
                    }
                }
            }

            val parameterHandlerMap = mutableMapOf<String, MutableList<() -> Unit>>().apply {
                this.put("pre", mutableListOf())
                this.put("do", mutableListOf())
                this.put("after", mutableListOf())
            }

            it.parameters.forEach {
                val parameterMetadata = mutableMapOf<String, Any?>().apply { this.putAll(functionMetadata) }
                parameterMetadata.put("parameter", it)
                it.annotations.forEach { ann ->
                    preParamHandlerMap.get(ann.annotationClass)?.let {
                        val handlerFunc = { it.resolve(parameterMetadata, ann) }
                        parameterHandlerMap.get("pre")!!.add(handlerFunc)
                    }
                    doParamHandlerMap.get(ann.annotationClass)?.let {
                        val handlerFunc = { it.resolve(parameterMetadata, ann) }
                        parameterHandlerMap.get("do")!!.add(handlerFunc)

                    }
                    afterParamHandlerMap.get(ann.annotationClass)?.let {
                        val handlerFunc = { it.resolve(parameterMetadata, ann) }
                        parameterHandlerMap.get("after")!!.add(handlerFunc)

                    }
                }


                parameterHandlerMap.get("pre")!!.forEach {
                    it()
                }
                parameterHandlerMap.get("do")!!.let {
                    if (it.isEmpty()) {
                        defaultDoFunctionHandle()
                    } else {
                        it.forEach {
                            it()
                        }
                    }
                }
                parameterHandlerMap.get("after")!!.forEach {
                    it()
                }

            }

            functionHandlerMap.get("do")!!.forEach {
                it()
            }
            functionHandlerMap.get("after")!!.forEach {
                it()
            }

            if (returnHandlerList.isEmpty()) {
                defaultReturnHandle()
            } else {
                returnHandlerList.first().invoke()
            }
        }


    }

}

interface ResolveHandler {
    fun annotationKClass(): KClass<out Annotation>
    fun resolve(metadata: MutableMap<String, Any?>, annotation: Annotation)
    fun order() = 0
}

interface ClassHandler : ResolveHandler {

}

interface PreClassHandler : ClassHandler {

}

interface DoClassHandler : ClassHandler {

}

interface AfterClassHandler : ClassHandler {

}

interface FunctionHandler : ResolveHandler {

}

interface PreFunctionHandler : FunctionHandler {

}

interface DoFunctionHandler : FunctionHandler {

}

interface AfterFunctionHandler : FunctionHandler {

}

interface ParamFunctionHandler : ResolveHandler {

}

interface PreParamHandler : ParamFunctionHandler {

}

interface DoParamHandler : ParamFunctionHandler {

}

interface AfterParamHandler : ParamFunctionHandler {

}

interface ReturnHandler : ResolveHandler {

}

