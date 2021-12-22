package io.github.shinglem.easyvertx.web.core.handlers

import io.github.shinglem.easyvertx.web.core.ResultHandler
import io.github.shinglem.easyvertx.web.core.impl.transToResultObject
import org.slf4j.LoggerFactory

open class SimpleRawTypeHandler : ResultHandler() {

    private val logger = LoggerFactory.getLogger(this::class.java.name)


    @OptIn(ExperimentalStdlibApi::class)
    override fun register() {
        logger.debug("SimpleRawTypeHandler")

        val (rawRes,type,cont) = resultHandlerChainProp
        val res = transToResultObject(type , rawRes)
        cont.value = res

        next()
    }

    override var next: ResultHandler? = null
//    override var previous: ResultHandler = this

}


