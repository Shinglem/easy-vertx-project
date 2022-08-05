package webtestexample.filter

import io.github.shinglem.easyvertx.util.classInfo.ExtraRouteHandler
import io.vertx.ext.web.Router
import kotlin.reflect.KClass


annotation class TestFilter

class TestExtraRouteHandler : ExtraRouteHandler {
    override fun order() = -Int.MAX_VALUE

    override fun resolve(context: MutableMap<String, Any?>, annotation: Annotation) {
        val router =  context["router"] as Router
        val routes = context["routes"] as MutableList<io.vertx.ext.web.Route>
        routes.forEach { route ->
            val r = router.route()
            if (route.isRegexPath) {
                r.pathRegex(route.path)
            }else{
                r.path(route.path)
            }

            route.methods()
                .forEach {
                    r.method(it)
                }
            route.order(order())
            route.handler { rc ->

                rc.next()

            }

        }
    }

    override fun annotationKClass(): KClass<out Annotation> = TestFilter::class
}
