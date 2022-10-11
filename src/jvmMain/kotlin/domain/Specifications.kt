package domain

import org.jetbrains.exposed.sql.Column
import ui.components.tables.ColumnId

sealed class Specification : ISpecification {
    object QueryAll : Specification()
    data class Search(val searchString: String = "") : Specification()
    data class Paginated(val pageNumber: Long, val itemsPerPage: Long, val totalItems: Long?) : Specification()
    data class Filtered(val filters: List<FilterSpec> = listOf(), val isFilteredOut: Boolean = false) : Specification()
    data class Grouped(val groupingSpec: GroupingSpec) : Specification()
    data class Sorted(
        val columnId: ColumnId,
        val isAscending: Boolean = true
    ) : Specification()
}

sealed class FilterSpec {
    abstract val columnName: String

    data class Values(
        val filteredValues: List<Any>,
        override val columnName: String
    ) : FilterSpec()

    data class Range<T>(
        val fromValue: T?,
        val toValue: T?,
        override val columnName: String
    ) : FilterSpec()
}

/**
 * Class for storing Slice values (data from a single table column)
 */
data class SliceValue<VALUETYPE>(val name: Any, val value: VALUETYPE?, val column: Column<VALUETYPE?>)


data class GroupingSpec(
    val columnId: ColumnId
)