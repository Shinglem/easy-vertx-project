package io.github.shinglem.easyvertx.web.spring.base

import io.github.shinglem.easyvertx.web.core.InitRouter
import io.github.shinglem.easyvertx.web.core.base.BaseRoute
import io.github.shinglem.easyvertx.web.core.base.RestfulRoute
import io.github.shinglem.easyvertx.web.core.impl.RootLevel
import io.github.shinglem.easyvertx.web.core.util.id.IdInterface
import io.github.shinglem.easyvertx.web.spring.SpringRoute
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.LoggerHandler
import org.slf4j.LoggerFactory

@SpringRoute
open class SpringBaseRoute : BaseRoute() {


}
@SpringRoute
open class SpringRestfulRoute(restPath: String = "/api/*") : RestfulRoute(restPath){

}
