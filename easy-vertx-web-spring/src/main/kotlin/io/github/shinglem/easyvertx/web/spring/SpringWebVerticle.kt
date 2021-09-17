package io.github.shinglem.easyvertx.web.spring

import io.github.shinglem.easyvertx.web.core.impl.WebAbstractVerticle
import io.vertx.core.http.HttpServerOptions
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.beans.factory.getBeansWithAnnotation
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
open class SpringWebVerticle(
    @Autowired
    private val applicationContext : ApplicationContext,
    @Autowired
    private val options : HttpServerOptions
) : WebAbstractVerticle() {

    private final val logger = LoggerFactory.getLogger(this::class.java.name)

    override val httpServerOptions: HttpServerOptions = options

    override fun loadControllers() {
        val controllers = applicationContext.getBeansWithAnnotation<SpringRoute>().values.toTypedArray()
        registerController(*controllers)
    }


}
