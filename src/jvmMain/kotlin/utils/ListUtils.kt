package utils

fun <T> List<T>.replace(newValue: T, block: (T) -> Boolean): List<T> {
    return map {
        if (block(it)) newValue else it
    }
}

fun <T> List<T>.replaceOrAdd(newValue: T, block: (T) -> Boolean): List<T> {
    var wasReplaced: Boolean = false

    val replaced = map {
        if (block(it)) {
            wasReplaced = true
            newValue
        } else it
    }

    return if (!wasReplaced) {
        replaced.plus(newValue)
    } else {
        replaced
    }
}
