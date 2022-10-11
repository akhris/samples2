package domain.valueobjects

/**
 * SliceResult(val value: Any, val foreignValues: List<Any>)
 *  if given column by [columnName] is not reference to another table - return just value of rows
 *  if given column is a reference - return it's id in value field and all foreing values in foreingValues field
 */
data class SliceResult(val value: Any, val foreingValues: List<Any> = listOf())