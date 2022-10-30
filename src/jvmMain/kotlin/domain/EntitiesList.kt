package domain

/**
 * Class for storing query result.
 */

typealias EntitiesList<T> = List<GroupedItem<T>>

fun <T> EntitiesList<T>.flatten(): List<T> = flatMap { it.items }

data class GroupedItem<T>(
    val groupID: GroupID? = null,
    val items: List<T>
)

data class GroupID(
    val categoryName: String,
    val key: Any?,
    val keyName: String? = null
)