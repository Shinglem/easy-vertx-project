package io.github.shinglem.easyvertx.util.classInfo


import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.valueParameters


open class ClassResolver(
    val handlers: List<ResolveHandler>,
    val processor : ProcessHandler,
    val context: Map<String, Any?>
) {

    val rootRegisterHandlerMap = handlers.filterIsInstance<RootRegisterHandler>().associateBy { it.annotationKClass() }
    val paramHandlerMap = handlers.filterIsInstance<ParamHandler>().associateBy { it.annotationKClass() }
    val afterHandlerMap = handlers.filterIsInstance<AfterHandler>().associateBy { it.annotationKClass() }
    val kClassHandlerMap = handlers.filterIsInstance<KClassHandler>().associateBy { it.annotationKClass() }
    val extraRouteHandlerMap = handlers.filterIsInstance<ExtraRouteHandler>().associateBy { it.annotationKClass() }

    fun resolve(any: Any) {
        val info = getClassInfo(any) ?: return
        assemble(info)
    }

    fun getClassInfo(any: Any): AssembleKClassInfo? {

        val asInfo = AssembleKClassInfo(any)


        val funcs = asInfo.instance::class.memberFunctions.mapNotNull {
            val funcInfo = AssembleKFunctionInfo(it)
            val preHandlerPairs = it.annotations.mapNotNull { ann ->

                rootRegisterHandlerMap[ann.annotationClass].let {
                    if (it == null) {
                        null
                    } else {
                        ann to it
                    }
                }

            }.toMap()

            if (preHandlerPairs.isEmpty()){
                return@mapNotNull null
            }
            funcInfo.rootRegisterHandlerPairs.putAll(preHandlerPairs)

            val extraRouteHandlerPairs = it.annotations.mapNotNull { ann ->

                extraRouteHandlerMap[ann.annotationClass].let {
                    if (it == null) {
                        null
                    } else {
                        ann to it
                    }
                }

            }.toMap()
            funcInfo.extraRouteHandlerPairs.putAll(extraRouteHandlerPairs)

            val afterHandlerPairs = it.annotations.mapNotNull { ann ->
                afterHandlerMap[ann.annotationClass].let {
                    if (it == null) {
                        null
                    } else {
                        ann to it
                    }
                }

            }.toMap()
            funcInfo.afterHandlerPairs.putAll(afterHandlerPairs)

            val params = it.valueParameters.map { param ->
                val paramInfo = AssembleKParamsInfo(param)
                val paramPair = param.annotations.mapNotNull { ann ->
                    paramHandlerMap[ann.annotationClass].let {
                        if (it == null) {
                            null
                        } else {
                            ann to it
                        }
                    }
                }.toMap()
                paramInfo.paramHandlerPairs.putAll(paramPair)
                paramInfo
            }
            funcInfo.params.addAll(params)
            funcInfo
        }

        if (funcs.isEmpty()) {
            return null
        }

        val kClassPairs = asInfo.instance::class.annotations.mapNotNull { ann ->
            kClassHandlerMap[ann.annotationClass].let {
                if (it == null) {
                    null
                } else {
                    ann to it
                }
            }
        }.toMap()
        asInfo.kClassHandlerPairs.putAll(kClassPairs)

        asInfo.funcs.addAll(funcs)

        return asInfo
    }

    fun assemble(info: AssembleKClassInfo) {
        val assembleContext = mutableMapOf<String, Any?>()
        assembleContext.putAll(context)
        assembleContext["assembleKClassInfo"] = info

        assembleContext["instance"] = info.instance

        info.kClassHandlerPairs.forEach {
            it.value.resolve(assembleContext , it.key)
        }

        info.funcs.forEach {
            val funcContext = mutableMapOf<String, Any?>()
            funcContext.putAll(assembleContext)
            funcContext["function"] = it.func

            it.rootRegisterHandlerPairs.forEach {
                it.value.resolve(funcContext , it.key)
            }

            it.extraRouteHandlerPairs.forEach {
                it.value.resolve(funcContext , it.key)
            }

            processor.resolve(funcContext , it.params , it.afterHandlerPairs)

        }


    }

}


data class AssembleKClassInfo(
    val instance: Any,
    val kClassHandlerPairs: MutableMap<Annotation, KClassHandler> = mutableMapOf(),
    val funcs: MutableList<AssembleKFunctionInfo> = mutableListOf(),
)

data class AssembleKFunctionInfo(
    val func: KFunction<*>,
    val rootRegisterHandlerPairs: MutableMap<Annotation, RootRegisterHandler> = mutableMapOf(),
    val extraRouteHandlerPairs: MutableMap<Annotation, ExtraRouteHandler> = mutableMapOf(),
    val params: MutableList<AssembleKParamsInfo> = mutableListOf(),
    val afterHandlerPairs: MutableMap<Annotation, AfterHandler> = mutableMapOf(),
)

data class AssembleKParamsInfo(
    val param: KParameter,
    val paramHandlerPairs: MutableMap<Annotation, ParamHandler> = mutableMapOf()
)

interface ResolveHandler {
    fun annotationKClass(): KClass<out Annotation>


}

interface FunctionHandler : ResolveHandler

interface RootRegisterHandler : FunctionHandler {
    fun resolve(context: MutableMap<String, Any?> , annotation: Annotation)
}

interface PreProcessHandler : FunctionHandler {
    fun resolve(context: MutableMap<String, Any?> , annotation: Annotation)
}

interface ExtraRouteHandler : FunctionHandler{
    fun order() :Int
    fun resolve(context: MutableMap<String, Any?> , annotation: Annotation)
}

interface ParamHandler : FunctionHandler {
    fun resolve(context: MutableMap<String, Any?>, annotation: Annotation , paramContext : Any , param : KParameter)
}

interface AfterHandler : FunctionHandler {
    fun resolve(context: MutableMap<String, Any?>, annotation: Annotation,  resultContext : Any , rawResult : Any?)
}

interface KClassHandler : ResolveHandler {
    fun resolve(context: MutableMap<String, Any?> , annotation: Annotation)
}

interface ProcessHandler {
    fun resolve(context: MutableMap<String, Any?>, params: MutableList<AssembleKParamsInfo> , after : MutableMap<Annotation, AfterHandler>)
}


