package com.xy.common.util.exception

class ConfigNotLoadException : Exception{
    constructor()
    constructor(msg: String) :super(msg)
    constructor(msg : String , throwable: Throwable) : super(msg , throwable)
}


class GetClassException : Exception{
    constructor()
    constructor(msg: String) :super(msg)
    constructor(msg : String , throwable: Throwable) : super(msg , throwable)
}

