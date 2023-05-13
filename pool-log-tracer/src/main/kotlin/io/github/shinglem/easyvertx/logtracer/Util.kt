package io.github.shinglem.easyvertx.logtracer

import io.vertx.sqlclient.Tuple

fun Tuple.logString(): String {
    val sb = StringBuilder()
    sb.append("[")
    val size = size()
    for (i in 0 until size) {
        sb.append(getValue(i)?.let {
            val str = it.toString()
            if (str.length > 20) {
                str.substring(0..19)+" ... "
            }else{
                str
            }
        })
        if (i + 1 < size) sb.append(",")
    }
    sb.append("]")
    return sb.toString()
}
