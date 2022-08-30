package ui.components.tables

import domain.Parameter
import domain.Sample

class SamplesAdapter(list: List<Sample>, onEntityChanged: (Sample) -> Unit) :
    BaseAdapter<Sample>(list, onEntityChanged = onEntityChanged) {


    override fun getColumnCount(): Int = 4

    override fun getHeader(column: Int): String {
        return when (column) {
            COLUMN_ID -> "ID"
            COLUMN_COMMENT -> "Комментарий"
            COLUMN_DESCRIPTION -> "Описание"
            COLUMN_ORDER_ID -> "Партия"
            else -> throw IllegalStateException("column with position: $column was not found in $this")
        }
    }


    override fun getEntityField(entity: Sample, column: Int): String {
        return when (column) {
            COLUMN_ID -> entity.id
            COLUMN_COMMENT -> entity.comment ?: ""
            COLUMN_DESCRIPTION -> entity.description ?: ""
            COLUMN_ORDER_ID -> entity.orderID ?: ""
            else -> throw IllegalStateException("column with position: $column was not found in $this")
        }
    }

    override fun setEntityField(entity: Sample, column: Int, value: String): Sample {
        return when (column) {
            COLUMN_ID -> entity.copy(id = value)
            COLUMN_COMMENT -> entity.copy(comment = value)
            COLUMN_DESCRIPTION -> entity.copy(description = value)
            COLUMN_ORDER_ID -> entity.copy(orderID = value)
            else -> throw IllegalStateException("column with position: $column was not found in $this")
        }
    }

    companion object {
        private const val COLUMN_ID = 0
        private const val COLUMN_DESCRIPTION = 1
        private const val COLUMN_COMMENT = 2
        private const val COLUMN_ORDER_ID = 3

    }
}