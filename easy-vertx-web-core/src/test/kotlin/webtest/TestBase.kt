package webtest

import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.client.HttpRequest
import io.vertx.ext.web.client.WebClient
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import webtestexample.EasyVertxMain
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread


open class TestBase {



    companion object {
        lateinit var client: WebClient
        val baseUrl: String = "127.0.0.1"
        val basePort = 39000

        @JvmStatic
        @BeforeAll
        open fun init() {
            println("+++++++++ init start +++++++++")
            val latch = CountDownLatch(1)
            thread {
                try {
                    EasyVertxMain.main()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }finally {
                    latch.countDown()
                }

            }
            latch.await()

            client = WebClient.create(Vertx.vertx())
            println("+++++++++ init finish +++++++++")

        }
    }

    fun testRequest(path:String = "", method : HttpMethod = HttpMethod.GET  ): HttpRequest<Buffer> {
        return client.request(method , basePort , baseUrl  , path)
    }
}
