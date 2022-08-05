package maintest.vertxtest


import io.github.shinglem.easyvertx.web.core.dep.Id
import io.github.shinglem.easyvertx.web.core.impl.Body
import io.github.shinglem.easyvertx.web.core.impl.Route
import io.github.shinglem.easyvertx.web.core.impl.RouteBase
import io.github.shinglem.easyvertx.web.spring.SpringRoute
import io.vertx.ext.web.RoutingContext

data class UserLoginInfo (
    val userAccount : String? ,
    val password : String? ,


    ){
}
@SpringRoute
@RouteBase(path = "/api/user")
object UserController {

    @Route(path = "/hello")
    fun hello( rc: RoutingContext){

    }

    @Route(path = "/error")
    fun error( rc: RoutingContext){
//        rc.fail(400 , Exception("aaa"))
//        rc.response().setStatusCode(400)
        throw Exception("aaa")
    }

    @Route(path = "/login")
    suspend fun login(@Id internalId : String, @Body userInfo: UserLoginInfo): UserLoginInfo {

        println("userLoginInfo : $userInfo")
//        try {
//
//
//            val userLoginInfo = userInfo.mapTo(UserLoginInfo::class.java)
//
//            logger.debug("userLoginInfo : $userLoginInfo")
//
//
//            val user = authenticationProvider.authenticate(
//                JsonObject()
//                    .put("username", userLoginInfo.userAccount)
//                    .put("password", userLoginInfo.password)
//            ).await()
//
//            logger.debug("User: " + user.principal())
//
//
//            val token = jwtProvider.generateToken(
//                JsonObject()
//                    .put("userAccount", userLoginInfo.userAccount)
//                    .put("userId" , user.get("userId")), jwtOptions
//            )
//            return responseBuild(RESULT_OK, "登录成功", JsonObject().put("token", token))
//
//
//        } catch (e: Exception) {
//
//            logger.error("" , e)
//
//            return responseBuild(RESULT_NOK, "登录失败")
//        }
        return userInfo
    }

}
