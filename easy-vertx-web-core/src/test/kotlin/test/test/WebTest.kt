package test.test


import io.github.shinglem.easyvertx.core.def.DefaultVertxMain
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class WebTest {

    companion object{

        val vertxMain = DefaultVertxMain()

        @BeforeAll
        @JvmStatic
        fun startMain() {
            println("start end")
            val downLatch = CountDownLatch(1)
            thread {
                vertxMain.start {
                    println("start end")
                    downLatch.countDown()
                }
            }
            downLatch.await()
        }

        @AfterAll
        @JvmStatic
        fun stopMain(){
            vertxMain.vertx().close()
        }
    }

    @Test
    fun webTest() {
        vertxMain.start()
    }

}
