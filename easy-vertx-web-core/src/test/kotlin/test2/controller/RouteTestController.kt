package test2.controller

import io.github.shinglem.easyvertx.web.core.impl.Route
import io.github.shinglem.easyvertx.web.core.impl.RouteBase
import io.vertx.ext.web.RoutingContext

@RouteBase(path = "/api/method")
class RouteTestController {

    @Route(path = "/get" , methods = [Route.HttpMethod.GET])
    fun get( rc: RoutingContext){
        println("hello")
    }

}
