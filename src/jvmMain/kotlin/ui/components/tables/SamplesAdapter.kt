package ui.components.tables

import domain.Sample

class SamplesAdapter(list: List<Sample>, onEntityChanged: (Sample) -> Unit) :
    BaseAdapter<Sample>(list, onEntityChanged = onEntityChanged) {


    override fun getColumnCount(): Int = 4

    override fun getHeader(column: Int): String {
        return when (column) {
            COLUMN_ID -> "Идентификатор"
            COLUMN_COMMENT -> "Комментарий"
            COLUMN_DESCRIPTION -> "Описание"
            COLUMN_ORDER_ID -> "Партия"
            else -> throw IllegalStateException("column with position: $column was not found in $this")
        }
    }


    override fun getEntityField(entity: Sample, column: Int): Cell {
        return Cell.TextCell(
            text = when (column) {
                COLUMN_ID -> entity.identifier ?: ""
                COLUMN_COMMENT -> entity.comment ?: ""
                COLUMN_DESCRIPTION -> entity.description ?: ""
                COLUMN_ORDER_ID -> entity.orderID ?: ""
                else -> throw IllegalStateException("column with position: $column was not found in $this")
            }
        )
    }

    override fun setEntityField(entity: Sample, column: Int, value: Cell): Sample {
        return when (column) {
            COLUMN_ID -> {
                (value as? Cell.TextCell)?.let {
                    entity.copy(identifier = it.text)
                } ?: entity
            }

            COLUMN_COMMENT -> {
                (value as? Cell.TextCell)?.let {
                    entity.copy(comment = it.text)
                } ?: entity
            }

            COLUMN_DESCRIPTION -> {
                (value as? Cell.TextCell)?.let {
                    entity.copy(description = it.text)
                } ?: entity
            }

            COLUMN_ORDER_ID -> {
                (value as? Cell.TextCell)?.let {
                    entity.copy(orderID = it.text)
                } ?: entity
            }

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