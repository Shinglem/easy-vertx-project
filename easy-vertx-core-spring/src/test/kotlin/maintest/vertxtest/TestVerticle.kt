package maintest.vertxtest

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component



@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
open class MySpringVerticle : AbstractVerticle() {
    private final val logger = LoggerFactory.getLogger(this::class.java.name)
    @PostConstruct
    fun init() {
        logger.info("Perform initialization in here")
    }

    @Value("\${aaa.bbb}")
    private lateinit var aaa : String

    override fun start() {
        println(this::class.simpleName)
        logger.info("start .....")
        Vertx.currentContext().instanceCount
//        Global.config.also {
//            println(it.encodePrettily())
//        }
        println(aaa)
    }
}
