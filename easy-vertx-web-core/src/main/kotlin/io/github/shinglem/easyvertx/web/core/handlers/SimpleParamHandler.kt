package io.github.shinglem.easyvertx.web.core.handlers

import io.github.shinglem.easyvertx.web.core.ParamHandler
import io.github.shinglem.easyvertx.web.core.ParamNotSupportException
import io.github.shinglem.easyvertx.web.core.ParamRegistException
import io.github.shinglem.easyvertx.web.core.impl.*
import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.starProjectedType

open class SimplePathParamHandler : ParamHandler() {

    private val logger = LoggerFactory.getLogger(this::class.java.name)


    @OptIn(ExperimentalStdlibApi::class)
    override fun register() {
        logger.debug("SimplePathParamHandler")

        val (param ,context , paramMap ) = paramHandlerChainProp

        val ann = param.findAnnotation<PathParam>()
        if (ann != null) {
            val name = ann.value.let {
                if (it == PathParam.ELEMENT_NAME) {
                    param.name!!
                } else {
                    it
                }
            }

            val value = context.pathParam(name)?.let {
                if (param.type.isMarkedNullable) {
                    it
                } else {
                    throw ParamNotSupportException("parameter ${param.name} can not be null")
                }
            }
            paramMap.put(param, value)
        }


        next()
    }


}

open class SimpleQueryParamHandler : ParamHandler() {

    private val logger = LoggerFactory.getLogger(this::class.java.name)


    @OptIn(ExperimentalStdlibApi::class)
    override fun register() {
        logger.debug("SimpleQueryParamHandler")
        val (param ,context , paramMap ) = paramHandlerChainProp
        val ann = param.findAnnotation<QueryParam>()
        if (ann != null) {
            if (!param.type.isSupertypeOf(List::class.starProjectedType)) {
                throw ParamRegistException("QueryParam should be List type")
            }


            val name = ann.value.let {
                if (it == QueryParam.ELEMENT_NAME) {
                    param.name!!
                } else {
                    it
                }
            }

            val value = context.queryParam(name)
            paramMap.put(param, value)
        }

        next()
    }



}

open class SimpleBodyParamHandler : ParamHandler() {

    private val logger = LoggerFactory.getLogger(this::class.java.name)


    @OptIn(ExperimentalStdlibApi::class)
    override fun register() {
        logger.debug("SimpleBodyParamHandler")
        val (param ,context , paramMap ) = paramHandlerChainProp
        val ann = param.findAnnotation<BodyParam>()
        if (ann != null) {

            val name = ann.value.let {
                if (it == BodyParam.ELEMENT_NAME) {
                    param.name!!
                } else {
                    it
                }
            }


            val json = try {
                context.bodyAsJson
            } catch (e: Throwable) {
                throw ParamNotSupportException("request is not json", e)
            }

            val value = paramMap.put(param, json.transToJsonDecodeValue(param.type, name).let {
                if (param.type.isMarkedNullable) {
                    it
                } else {
                    throw ParamNotSupportException("parameter ${param.name} can not be null")
                }
            })
            paramMap.put(param, value)
        }

        next()
    }

}

open class SimpleBodyHandler : ParamHandler() {

    private val logger = LoggerFactory.getLogger(this::class.java.name)


    @OptIn(ExperimentalStdlibApi::class)
    override fun register() {
        logger.debug("SimpleBodyHandler")
        val (param ,context , paramMap ) = paramHandlerChainProp
        val ann = param.findAnnotation<Body>()
        if (ann != null) {

            val bd = try {
                context.body
            } catch (e: Throwable) {
                throw ParamNotSupportException("request is not json", e)
            }

            val value = if (bd == null) {
                bd.let {
                    if (param.type.isMarkedNullable) {
                        it
                    } else {
                        throw ParamNotSupportException("parameter ${param.name} can not be null")
                    }
                }
            }else{
                Json.CODEC.fromBuffer(bd , (param.type.classifier as KClass<*>).java)
            }

            paramMap.put(param, value)
        }

        next()
    }


}


open class SimpleRcParamHandler : ParamHandler() {

    private val logger = LoggerFactory.getLogger(this::class.java.name)


    @OptIn(ExperimentalStdlibApi::class)
    override fun register() {
        logger.debug("SimpleRcParamHandler")

        val (param ,context , paramMap ) = paramHandlerChainProp
        val isRc = param.type.isSupertypeOf(RoutingContext::class.starProjectedType)
        if(isRc){
            paramMap.put(param, context)
        }

        next()
    }

}


open class SimpleHeaderParamHandler : ParamHandler() {

    private val logger = LoggerFactory.getLogger(this::class.java.name)


    @OptIn(ExperimentalStdlibApi::class)
    override fun register() {
        logger.debug("SimpleHeaderParamHandler")
        val (param ,context , paramMap ) = paramHandlerChainProp
        val ann = param.findAnnotation<Header>()
        if (ann != null) {
            if (!param.type.isSupertypeOf(String::class.starProjectedType)) {
                throw ParamRegistException("Header should be String type")
            }


            val name = ann.value.let {
                if (it == Header.ELEMENT_NAME) {
                    param.name!!
                } else {
                    it
                }
            }

            val value = context.request().getHeader(name)
            paramMap.put(param, value)
        }

        next()
    }

}
