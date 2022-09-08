package domain

import org.jetbrains.exposed.sql.Column
import ui.components.tables.ColumnId

sealed class Specification : ISpecification {
    object QueryAll : Specification()
    data class Search(val searchString: String = "") : Specification()
    data class Paginated(val pageNumber: Long, val itemsPerPage: Long) : Specification()
    data class Filtered(val filters: List<FilterSpec> = listOf()) : Specification()
    data class Grouped(val groupingSpec: GroupingSpec) : Specification()
    data class Sorted(
        val columnId: ColumnId,
        val isAscending: Boolean = true
    ) : Specification()
}

sealed class FilterSpec {
    abstract val columnId: ColumnId

    data class Values(
        val filteredValues: List<SliceValue<Any>>,
        override val columnId: ColumnId
    ) : FilterSpec()

    data class Range<T>(
        val fromValue: T?,
        val toValue: T?,
        override val columnId: ColumnId
    ) : FilterSpec()
}

/**
 * Class for storing Slice values (data from a single table column)
 */
data class SliceValue<VALUETYPE>(val name: Any, val value: VALUETYPE?, val column: Column<VALUETYPE?>)



data class GroupingSpec(
    val columnId: ColumnId
)