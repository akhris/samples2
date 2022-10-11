package domain

/**
 * Class for storing query result.
 */
sealed class EntitiesList<T> {
    /**
     * Grouped query result
     */
    data class Grouped<T>(val items: List<GroupedItem<T>>) : EntitiesList<T>()

    /**
     * Not grouped query result
     */
    data class NotGrouped<T>(val items: List<T>) : EntitiesList<T>()

    fun isNotEmpty(): Boolean {
        return when(this){
            is Grouped -> items.isNotEmpty()
            is NotGrouped -> items.isNotEmpty()
        }
    }

    companion object {
        fun <ENTITY> empty(): EntitiesList<ENTITY> = NotGrouped(listOf())
    }
}

data class GroupedItem<T>(
    val groupID: GroupID,
    val items: List<T>
)

data class GroupID(
    val categoryName: String,
    val key: Any?,
    val keyName: String? = null
)