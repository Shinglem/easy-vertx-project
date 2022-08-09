package io.github.shinglem.easyvertx.core.spring

import io.github.shinglem.easyvertx.core.Main
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

private val logger = KotlinLogging.logger {}

@Configuration
open class VertxBean {


    @Autowired
    private lateinit var vertxMain: Main

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @PostConstruct
    open fun initVertx() {
        logger.debug { "post construct init vertx" }
        ApplicationContextProvider.applicationContext = applicationContext
        vertxMain.start()
    }
}

@Configuration
open class VertxMainBean {
    @Bean
    open fun springVertxMain(applicationContext: ApplicationContext): Main {
        return SpringVertxMain(applicationContext)
    }
}
