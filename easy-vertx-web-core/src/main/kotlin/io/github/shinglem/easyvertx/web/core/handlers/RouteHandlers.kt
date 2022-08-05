package io.github.shinglem.easyvertx.web.core.handlers

import io.github.shinglem.easyvertx.util.classInfo.KClassHandler
import io.github.shinglem.easyvertx.util.classInfo.RootRegisterHandler
import io.github.shinglem.easyvertx.web.core.impl.Route
import io.github.shinglem.easyvertx.web.core.impl.Route.Routes
import io.github.shinglem.easyvertx.web.core.impl.RouteBase
import io.vertx.ext.web.Router
import kotlin.reflect.KClass

open class RouteBaseHandler : KClassHandler {


    override fun annotationKClass(): KClass<out Annotation> {
        return RouteBase::class
    }

    override fun resolve(context: MutableMap<String, Any?>, annotation: Annotation) {
        val anno = annotation as RouteBase
        context["RouteBasePath"] = anno.path
        context["RouteBaseProduce"] = anno.produces
        context["RouteBaseConsumes"] = anno.consumes
    }

}

open class RouteHandler : RootRegisterHandler {
    override fun annotationKClass(): KClass<out Annotation> {
        return Route::class
    }

    override fun resolve(context: MutableMap<String, Any?>, annotation: Annotation) {
        val anno = annotation as Route
        val router = context["router"] as Router
        val routeBasePath = context["RouteBasePath"] as String? ?: ""


        val route = router.route()

        val routes = context["routes"] as MutableList<io.vertx.ext.web.Route>? ?: kotlin.run {
            context["routes"] = mutableListOf<io.vertx.ext.web.Route>()
            context["routes"] as MutableList<io.vertx.ext.web.Route>
        }

        routes.add(route)


        var path = ""

        if (routeBasePath.endsWith("/")) {
            path += routeBasePath.dropLast(1)
        } else {
            path += routeBasePath
        }

        if (anno.path == "/" || anno.path.isBlank()) {

        } else if (anno.path.startsWith("/")) {
            path += "/" + anno.path.removePrefix("/")
        }

        if (path.isBlank()) {
            path = "/"
        }

        if (anno.regex.isNotBlank()) {
            route.pathRegex(path)
        } else {
            route.path(path)
        }


    }

}

open class RoutesHandler : RouteHandler() {
    override fun annotationKClass(): KClass<out Annotation> {
        return Routes::class
    }

    override fun resolve(context: MutableMap<String, Any?>, annotation: Annotation) {
        val anno = annotation as Routes

        context["routes"] = mutableListOf<io.vertx.ext.web.Route>()

        anno.value.forEach {
            super.resolve(context, it)
        }

    }

}
