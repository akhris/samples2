package ui.components.tables.mappers

import domain.Parameter
import ui.components.tables.Cell
import ui.components.tables.ColumnId
import ui.components.tables.IDataTableMapper

class ParametersDataMapper : IDataTableMapper<Parameter> {
    override val columns: List<ColumnId> = listOf(
        Column.Name.id,
        Column.Description.id
    )

    override fun getId(item: Parameter): String = item.id

    override fun updateItem(item: Parameter, columnId: ColumnId, cell: Cell): Parameter {
        return when (Column.requireColumn(columnId)) {
            Column.Description -> (cell as? Cell.EditTextCell)?.let { item.copy(description = it.value) }
            Column.Name -> (cell as? Cell.EditTextCell)?.let { item.copy(name = it.value) }
        } ?: item
    }

    override fun getCell(item: Parameter, columnId: ColumnId): Cell {
        return when (Column.requireColumn(columnId)) {
            Column.Description -> Cell.EditTextCell(value = item.description)
            Column.Name -> Cell.EditTextCell(value = item.name)
        }
    }

    private sealed class Column(val id: ColumnId) {
        object Name : Column(ColumnId("column_name", "Идентификатор"))
        object Description : Column(ColumnId("column_description", "Описание"))

        companion object {
            fun requireColumn(id: ColumnId): Column {
                return when (id.key) {
                    Name.id.key -> Name
                    Description.id.key -> Description
                    else -> throw IllegalStateException("column with id: $id was not found in $this")
                }
            }
        }
    }
}