package io.github.shinglem.easyvertx.web.spring.base

import io.github.shinglem.easyvertx.web.core.dep.base.BaseRoute
import io.github.shinglem.easyvertx.web.core.dep.base.RestfulRoute
import io.github.shinglem.easyvertx.web.spring.SpringRoute

@SpringRoute
open class SpringBaseRoute : BaseRoute() {


}
@SpringRoute
open class SpringRestfulRoute(restPath: String = "/api/*") : RestfulRoute(restPath){

}
