package io.github.shinglem.easyvertx.web.core

import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.CoroutineScope
import org.slf4j.LoggerFactory
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType

interface ControllerResolver {

    fun getControllers(): List<Any>
}

interface Chain<T> where T : Chain<T> {
    abstract var next: T?

    //    abstract var previous : T
    fun next()
}

data class HandlerChainProp(
    val router: Router,
    val route: Route,
    val vertx: Vertx,
    val scope: CoroutineScope,
)

abstract class HandlerChain<T> : Chain<T> where T : HandlerChain<T> {
    private val logger = LoggerFactory.getLogger(this::class.java.name)

    lateinit var handlerChainProp: HandlerChainProp


    override var next: T? = null

    //    override lateinit var previous: T
    abstract fun register()

    open fun propertiesCopy() {
        if (next != null) {
            next!!.handlerChainProp = handlerChainProp

        }
    }

    override fun next() {
        if (next != null) {
            propertiesCopy()
            next!!.register()
        } else {
            logger.debug("${this::class.simpleName} is the end HandlerChain")
        }
    }
}
data class ControllerHandlerChainProp(
    val controller: Any,
    val firstRouteHandler: RouteFunctionHandler,
    val firstParamHandler: ParamHandler,
    val firstResultHandler: ResultHandler,
    val firstResponseHandler: ResponseHandler,
)
abstract class ControllerHandler : HandlerChain<ControllerHandler>() {

    private val logger = LoggerFactory.getLogger(this::class.java.name)


    lateinit var controllerHandlerChainProp: ControllerHandlerChainProp


//    override var next: ControllerHandler? = null
//    override lateinit var previous: ControllerHandler

    override fun propertiesCopy() {
        super.propertiesCopy()
        if (next != null) {
            next!!.controllerHandlerChainProp = controllerHandlerChainProp
        }
    }

//    override fun next() {
//        if (next != null) {
//            propertiesCopy()
////            next!!.previous = this
//            next!!.register()
//        } else {
//            logger.debug("${this::class.simpleName} is the end ControllerHandler")
//        }
//    }
}
data class RouteFunctionHandlerChainProp(
    val controller: Any,
    val func:  KFunction<*>,
    val firstParamHandler: ParamHandler,
    val firstResultHandler: ResultHandler,
    val firstResponseHandler: ResponseHandler,
)
abstract class RouteFunctionHandler : HandlerChain<RouteFunctionHandler>() {

    private val logger = LoggerFactory.getLogger(this::class.java.name)


    lateinit var routeFunctionHandlerChainProp: RouteFunctionHandlerChainProp

    //    override var next: RouteFunctionHandler? = null
//    override lateinit var previous: RouteFunctionHandler
    override fun propertiesCopy() {
        super.propertiesCopy()
        if (next != null) {
            next!!.routeFunctionHandlerChainProp = routeFunctionHandlerChainProp
        }
    }

//    override fun next() {
//        if (next != null) {
//            propertiesCopy()
////            next!!.previous = this
//            next!!.register()
//        } else {
//            logger.debug("${this::class.simpleName} is the end RouteFunctionHandler")
//        }
//    }
}
data class ParamHandlerChainProp(
    val param: KParameter,
    val context: RoutingContext,
    val paramMap: MutableMap<KParameter, Any?>,
)
abstract class ParamHandler : HandlerChain<ParamHandler>() {

    private val logger = LoggerFactory.getLogger(this::class.java.name)

    lateinit var paramHandlerChainProp: ParamHandlerChainProp


    //    override var next: ParamHandler? = null
//    override lateinit var previous: ParamHandler
    override fun propertiesCopy() {
        super.propertiesCopy()
        if (next != null) {
            next!!.paramHandlerChainProp = paramHandlerChainProp
        }
    }

//    override fun next() {
//        if (next != null) {
//            propertiesCopy()
//
////            next!!.previous = this
//            next!!.register()
//        } else {
//            logger.debug("${this::class.simpleName} is the end ParamHandler")
//        }
//    }
}

data class ResultContainer(
    var value: String = ""
)

data class ResultHandlerChainProp(
    val result: Any?,
    val resultType: KType,
    val resultContainer: ResultContainer,
)

abstract class ResultHandler : HandlerChain<ResultHandler>() {

    private val logger = LoggerFactory.getLogger(this::class.java.name)


    lateinit var resultHandlerChainProp: ResultHandlerChainProp



    //    override var next: ResultHandler? = null
//    override lateinit var previous: ResultHandler
    override fun propertiesCopy() {
        super.propertiesCopy()
        if (next != null) {
           next!!.resultHandlerChainProp = resultHandlerChainProp

        }
    }

//    override fun next() {
//        if (next != null) {
//            propertiesCopy()
//            next!!.register()
//        } else {
//            logger.debug("${this::class.simpleName} is the end ResultHandler")
//        }
//    }
}

data class ResponseInfo(
    var resp: String = "",
    var error: Throwable? = null,
    var code: Int? = null,
) {

}
data class ResponseHandlerChainProp(
    val responseInfo: ResponseInfo,
    val responseBufferContainer: ResponseBufferContainer,

)

data class ResponseBufferContainer(
    var respBuffer: Buffer = Buffer.buffer(),
) {

}

abstract class ResponseHandler : HandlerChain<ResponseHandler>() {

    private val logger = LoggerFactory.getLogger(this::class.java.name)


    lateinit var responseHandlerChainProp: ResponseHandlerChainProp

//    lateinit var param: KParameter
//    lateinit var context: RoutingContext
//    lateinit var paramMap: MutableMap<KParameter, Any?>

//    override var next: ResponseHandler? = null
//    override lateinit var previous: ResponseHandler

    override fun propertiesCopy() {
        super.propertiesCopy()
        if (next != null) {
            next!!.responseHandlerChainProp = responseHandlerChainProp
        }
    }

//    override fun next() {
//        if (next != null) {
//            propertiesCopy()
//            next!!.register()
//        } else {
//            logger.debug("${this::class.simpleName} is the end ResponseHandler")
//        }
//    }

}
