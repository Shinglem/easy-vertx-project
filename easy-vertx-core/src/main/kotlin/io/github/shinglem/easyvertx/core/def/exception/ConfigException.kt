package io.github.shinglem.easyvertx.core.def.exception

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

