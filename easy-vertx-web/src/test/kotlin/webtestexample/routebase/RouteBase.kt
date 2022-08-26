package webtestexample.routebase

import io.github.shinglem.easyvertx.web.core.impl.Route
import io.github.shinglem.easyvertx.web.core.impl.RouteBase
import io.vertx.core.Vertx
import io.vertx.ext.web.Router

@RouteBase
class EmptyRouteBase {

    @Route
    fun emptyRoute() {

    }

    @Route(path = "/emptyRouteBase")
    fun emptyRouteBase(): String {
        return "emptyRouteBase"
    }

}

@RouteBase(path = "/notEmpty")
class NotEmptyRouteBase {

    @Route
    fun notEmpty(): String {
        return "notEmpty"
    }

    @Route(path = "/notEmptyTest")
    fun notEmptyTest(): String {
        return "notEmptyTest"
    }

}

@RouteBase(path = "/routes")
class Routes {

    @Route(path = "/1")
    @Route(path = "/2")
    fun notEmptyTest(): String = "routes"

}

