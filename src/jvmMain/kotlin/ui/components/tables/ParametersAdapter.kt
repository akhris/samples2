package ui.components.tables

import domain.Parameter

class ParametersAdapter(list: List<Parameter>, onEntityChanged: (Parameter) -> Unit) :
    BaseAdapter<Parameter>(list, onEntityChanged = onEntityChanged) {


    override fun getColumnCount(): Int = 2

    override fun getHeader(column: Int): String {
        return when (column) {
            COLUMN_NAME -> "Имя"
            COLUMN_DESCRIPTION -> "Описание"
            else -> throw IllegalStateException("column with position: $column was not found in $this")
        }
    }


    override fun getEntityField(entity: Parameter, column: Int): String {
        return when (column) {
            COLUMN_NAME -> entity.name
            COLUMN_DESCRIPTION -> entity.description
            else -> throw IllegalStateException("column with position: $column was not found in $this")
        }
    }

    override fun setEntityField(entity: Parameter, column: Int, value: String): Parameter {
        return when (column) {
            COLUMN_NAME -> entity.copy(name = value)
            COLUMN_DESCRIPTION -> entity.copy(description = value)
            else -> throw IllegalStateException("column with position: $column was not found in $this")
        }
    }

    companion object {
        private const val COLUMN_NAME = 0
        private const val COLUMN_DESCRIPTION = 1

    }
}