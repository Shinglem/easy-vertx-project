package maintest

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper
import io.github.shinglem.easyvertx.core.def.json.path
import io.github.shinglem.easyvertx.core.spring.getConfigMap
import io.github.shinglem.easyvertx.core.spring.getHttpServerOptions
import io.github.shinglem.easyvertx.web.spring.base.SpringBaseRoute
import io.github.shinglem.easyvertx.web.spring.base.SpringRestfulRoute
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.get
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.AbstractEnvironment
import org.springframework.core.env.MapPropertySource

@SpringBootApplication
open class Main {
}

fun main(args: Array<String>) {
    runApplication<Main>(*args)
}

@Configuration
open class WebConfig {
    @Bean
    open fun springBaseRoute(): SpringBaseRoute {
        return SpringBaseRoute()
    }

    @Bean
    open fun springRestfulRoute(): SpringRestfulRoute {
        return SpringRestfulRoute()
    }

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Bean
    open fun httpServerOptions(): HttpServerOptions {
        val options = getHttpServerOptions(applicationContext, "SpringWebVerticle")
        return options
    }
}
