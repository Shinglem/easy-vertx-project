package io.github.shinglem.easyvertx.core.def.exception

class JsonPathException : Exception{
    constructor()
    constructor(msg: String) :super(msg)
    constructor(msg : String , throwable: Throwable) : super(msg , throwable)
}

class JsonTypeException : Exception{
    constructor()
    constructor(msg: String) :super(msg)
    constructor(msg : String , throwable: Throwable) : super(msg , throwable)
}