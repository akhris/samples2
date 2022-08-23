package domain

sealed class Specification : ISpecification {
    object QueryAll : Specification()
    data class Search(val searchString: String = "") : Specification()
    data class Paginated(val pageNumber: Long, val itemsPerPage: Long) : Specification()
//    data class Filtered(val filters: List<FilterSpec> = listOf()) : Specification()
//    data class Grouped(val groupingSpec: GroupingSpec) : Specification()
//    data class Sorted(val spec: SortingSpec) : Specification()
    data class CombinedSpecification(val specs: List<Specification>) : Specification()
}
