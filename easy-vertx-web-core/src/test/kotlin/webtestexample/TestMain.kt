package webtestexample

import io.github.shinglem.easyvertx.core.def.VertxMain

object EasyVertxMain{

    @JvmStatic
    fun main() {
        VertxMain().start()
    }

}

fun main(args: Array<String>) {
    try {
        VertxMain().start()
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}


