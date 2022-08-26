package ui.components.tables

import domain.IEntity

abstract class BaseAdapter<ENTITY : IEntity>(
    private val list: List<ENTITY>,
    private val withHeader: Boolean = true,
    private val onEntityChanged: ((ENTITY) -> Unit)? = null
) :
    ITableAdapter {

    override fun withHeader(): Boolean = withHeader

    override fun getTotalRows(): Int {
        return list.size
    }

    override fun getCellValue(column: Int, row: Int): String {
        val entity = requireEntity(row)
        return getEntityField(entity, column)
    }

    abstract fun setEntityField(entity: ENTITY, column: Int, value: String) : ENTITY
    abstract fun getEntityField(entity: ENTITY, column: Int): String

    override fun setCellValue(column: Int, row: Int, newValue: String) {
        val entity = requireEntity(row)
        val changedEntity = setEntityField(entity, column, newValue)
        onEntityChanged?.invoke(changedEntity)
    }

    private fun requireEntity(row: Int): ENTITY {
        return list.getOrNull(row)
            ?: throw IllegalStateException("row $row out of bounds for list sized ${list.size} in $this")
    }
}