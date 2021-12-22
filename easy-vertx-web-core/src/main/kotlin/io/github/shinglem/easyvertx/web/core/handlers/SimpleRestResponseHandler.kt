package io.github.shinglem.easyvertx.web.core.handlers

import io.github.shinglem.easyvertx.web.core.ResponseHandler
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory

open class SimpleRestResponseHandler : ResponseHandler(){

    private val logger = LoggerFactory.getLogger(this::class.java.name)


    @OptIn(ExperimentalStdlibApi::class)
    override fun register() {
        logger.debug("SimpleRestResponseHandler")

        val rawResp = responseHandlerChainProp.responseInfo.resp
        val code = if (responseHandlerChainProp.responseInfo.error == null) {
            0
        }else{
            if (responseHandlerChainProp.responseInfo.code == null) {
                -1
            } else {
                responseHandlerChainProp.responseInfo.code!!
            }
        }

        val data = if (responseHandlerChainProp.responseInfo.error == null){
            rawResp
        }else{
            responseHandlerChainProp.responseInfo.error!!.message ?: "error"
        }

        val restResp = JsonObject()
            .put("code", code)
            .put("data", data)

        responseHandlerChainProp.responseBufferContainer.respBuffer = Buffer.buffer(restResp.encodePrettily())

        next()
    }

    override var next: ResponseHandler? = null
//    override var previous: ResponseHandler = this

}
