package utils

fun <T> List<T>.replace(newValue: T, block: (T) -> Boolean): List<T> {
    return map {
        if (block(it)) newValue else it
    }
}

fun <T> MutableList<T>.replace(newValue: T, block: (T) -> Boolean) {
    val oldItem = this.find { block(it) } ?: return
    val oldItemPos = indexOf(oldItem)
    this[oldItemPos] = newValue
}

fun <T> MutableList<T>.moveDown(index: Int) {
    if (index in indices)
        add(index + 1, removeAt(index))
}

fun <T> MutableList<T>.moveUp(index: Int) {
    if (index in indices)
        add(index - 1, removeAt(index))
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

fun <T> List<T>.toFormattedList(delimiter: String = ", "): String {
    val builder = StringBuilder()
    forEachIndexed { index, s ->
        builder.append(s)
        if (index < size - 1) {
            builder.append(delimiter)
        }
    }
    return builder.toString()
}