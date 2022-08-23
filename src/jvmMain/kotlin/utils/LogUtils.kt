package utils

import utils.LogUtils.isLogEnabled

object LogUtils {
    var isLogEnabled = true
}

fun Any.log(text: Any) {
    if (isLogEnabled)
        println("${this::class.simpleName}@${this.hashCode()}: $text")
}

fun log(text: Any, prefix: String? = null) {
    val textBuilder = StringBuilder()
    prefix?.let {
        textBuilder.append("$it: ")
    }
    textBuilder.append(text)
    println(textBuilder.toString())
}