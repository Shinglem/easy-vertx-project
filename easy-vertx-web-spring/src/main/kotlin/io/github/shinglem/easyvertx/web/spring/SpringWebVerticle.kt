package io.github.shinglem.easyvertx.web.spring

import io.github.shinglem.easyvertx.core.Global
import io.github.shinglem.easyvertx.core.json.path0
import io.github.shinglem.easyvertx.web.WebServerVerticle
import io.github.shinglem.easyvertx.web.core.impl.RouteBase
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.JsonObject
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.beans.factory.getBeansWithAnnotation
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
open class SpringWebVerticle(
    @Autowired
    private val applicationContext : ApplicationContext,
) : WebServerVerticle() {

    open val webConfig by lazy {
        Global.config.path0<JsonObject>("$.webServer") ?: JsonObject()
    }
    open override val httpServerOptions by lazy {
        webConfig.path0<JsonObject>("httpServerOptions")?.mapTo(HttpServerOptions::class.java)
            ?: HttpServerOptions()
    }


    override fun findControllers() : List<Any> {
        val controllers = applicationContext.getBeansWithAnnotation<RouteBase>().values.toList()
        return controllers
    }


}
