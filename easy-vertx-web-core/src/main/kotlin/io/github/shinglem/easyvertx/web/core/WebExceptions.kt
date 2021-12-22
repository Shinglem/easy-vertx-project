package io.github.shinglem.easyvertx.web.core



class RouteRegistException(msg : String) : Exception(msg)


class ParamRegistException(msg : String) : Exception(msg)

class ParamNotSupportException : Exception{

    constructor(message: String?, cause: Throwable?) :super(message,cause){

    }

    constructor(msg: String):super(msg) {

    }
}

class FunctionNotSupportException(msg : String) : Exception(msg)

class ReturnTypeNotSupportException(msg : String) : Exception(msg)


class WebClassGetExcetion(msg : String) : Exception(msg)

class ControllerValidException(msg : String) : Exception(msg)
