package maintest.vertxtest

import io.vertx.core.AbstractVerticle
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class MySpringVerticle : AbstractVerticle() {
    private final val logger = LoggerFactory.getLogger(this::class.java.name)
    @PostConstruct
    fun init() {
        logger.info("Perform initialization in here")
    }

    override fun start() {
        println(this::class.simpleName)
        logger.info("start .....")
    }
}