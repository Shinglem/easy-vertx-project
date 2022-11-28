package io.github.shinglem.easyvertx.core.util

import io.vertx.ext.web.RoutingContext
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.starProjectedType


class Resolver constructor(
    classHandlers: Set<ClassHandler>,
    functionHandlers: Set<FunctionHandler>,
    parameterHandlers: Set<FunctionParameterHandler>,
    doFunctionHandlers: Set<DoFunctionHandler>,
    returnHandlers: Set<ReturnHandler>,

    defaultClassHandlers: Set<ClassHandler>,
    defaultFunctionHandlers: Set<FunctionHandler>,
    defaultFunctionParameterHandlers: Set<FunctionParameterHandler>,
    defaultDoFunctionHandlers: Set<DoFunctionHandler>,
    defaultReturnHandlers: Set<ReturnHandler>,

    val functionAnn : List<KClass<out Annotation>>
    ) {


    val classHandlers = classHandlers.sortedBy { it.order() }
    val functionHandlers = functionHandlers.sortedBy { it.order() }
    val parameterHandlers = parameterHandlers.sortedBy { it.order() }
    val doFunctionHandlers = doFunctionHandlers.sortedBy { it.order() }
    val returnHandlers = returnHandlers.sortedBy { it.order() }
    val defaultClassHandlers = defaultClassHandlers.sortedBy { it.order() }
    val defaultFunctionHandlers = defaultFunctionHandlers.sortedBy { it.order() }
    val defaultFunctionParameterHandlers = defaultFunctionParameterHandlers.sortedBy { it.order() }
    val defaultDoFunctionHandlers = defaultDoFunctionHandlers.sortedBy { it.order() }
    val defaultReturnHandlers = defaultReturnHandlers.sortedBy { it.order() }


    fun resolve(metaData: MutableMap<String, Any?>, clz: Any) {
        val classMetadata = mutableMapOf<String, Any?>().apply { this.putAll(metaData) }
        classMetadata.put("instance", clz)


        val classHandlerList = defaultClassHandlers.map { { it.resolve(classMetadata, Default()) } } +
                clz::class.annotations.mapNotNull { ann ->
                    classHandlers.firstOrNull { ann.annotationClass.starProjectedType == it.annotationKClass().starProjectedType }
                        ?.let {
                            it to { it.resolve(classMetadata, ann) }
                        }
                }
                    .sortedBy { it.first.order() }
                    .map { it.second }


        classHandlerList.forEach {
            it()
        }


        clz::class.memberFunctions
            .filter {
//                it.annotations.any { it is Route || it is Route.Routes || it is RawRouter }
                it.annotations.any { functionAnn.contains(it.annotationClass) }
            }
            .forEach {
                val functionMetadata = mutableMapOf<String, Any?>().apply { this.putAll(classMetadata) }
                functionMetadata.put("function", it)
                functionMetadata.put("parameterFuncMap", mutableMapOf<KParameter, (RoutingContext) -> Any?>())


                val functionHandlerList = defaultFunctionHandlers.map { { it.resolve(functionMetadata, Default()) } } +
                        it.annotations.mapNotNull { ann ->
                            functionHandlers.firstOrNull { ann.annotationClass.starProjectedType == it.annotationKClass().starProjectedType }
                                ?.let {
                                    it to { it.resolve(functionMetadata, ann) }
                                }

                        }.sortedBy { it.first.order() }
                            .map { it.second }

                functionHandlerList.forEach { it() }

                val isDone = (functionMetadata.get("isDone") as? Boolean) ?: false

                if (isDone){
                    return@forEach
                }


                it.parameters.forEach {
                    val parameterMetadata = mutableMapOf<String, Any?>().apply { this.putAll(functionMetadata) }
                    parameterMetadata.put("parameter", it)

                    val parameterHandlerList =
                        defaultFunctionParameterHandlers.map { { it.resolve(parameterMetadata, Default()) } } +
                                it.annotations.mapNotNull { ann ->

                                    parameterHandlers.firstOrNull { ann.annotationClass.starProjectedType == it.annotationKClass().starProjectedType }
                                        ?.let {
                                            it to { it.resolve(parameterMetadata, ann) }
                                        }

                                }
                                    .sortedBy { it.first.order() }
                                    .map { it.second }

                    parameterHandlerList.forEach { it() }
                }

                val doFunctionHandlerList =
                    defaultDoFunctionHandlers.map { { it.resolve(functionMetadata, Default()) } } +
                            it.annotations.mapNotNull { ann ->
                                doFunctionHandlers.firstOrNull { ann.annotationClass.starProjectedType == it.annotationKClass().starProjectedType }
                                    ?.let {
                                        it to { it.resolve(functionMetadata, ann) }
                                    }

                            }.sortedBy { it.first.order() }
                                .map { it.second }

                doFunctionHandlerList.forEach { it() }


                val returnHandlerList = defaultReturnHandlers.map { { it.resolve(functionMetadata, Default()) } } +
                        it.annotations.mapNotNull { ann ->
                            returnHandlers.firstOrNull { ann.annotationClass.starProjectedType == it.annotationKClass().starProjectedType }
                                ?.let {
                                    it to { it.resolve(functionMetadata, ann) }
                                }

                        }.sortedBy { it.first.order() }
                            .map { it.second }

                returnHandlerList.forEach { it() }
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



interface FunctionHandler : ResolveHandler {

}


interface DoFunctionHandler : FunctionHandler {

}


interface FunctionParameterHandler : ResolveHandler {

}


interface ReturnHandler : ResolveHandler {

}


@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class Default
