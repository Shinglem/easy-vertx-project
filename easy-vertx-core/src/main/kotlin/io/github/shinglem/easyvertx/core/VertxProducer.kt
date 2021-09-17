package io.github.shinglem.easyvertx.core



import io.vertx.core.Vertx

//private lateinit var up : VertxProducer
// fun setProducer(producer: VertxProducer) {
//    if(!::up.isInitialized){
//        up = producer
//    }
//}
//
//private lateinit var cf : ConfigLoader
//fun setConfigLoader(configLoader: ConfigLoader) {
//    if(!::cf.isInitialized){
//        cf = configLoader
//    }
//}


interface VertxProducer {
    fun vertx(): Vertx
}

//object VERTX : VertxProducer by up
//
//object VertxConfig : ConfigLoader by cf

