package ui.components.tables.mappers

import domain.OperationType
import persistence.exposed.dto.Tables
import ui.components.tables.Cell
import ui.components.tables.ColumnId
import ui.components.tables.ColumnWidth
import ui.components.tables.IDataTableMapper

class OperationTypesDataMapper : IDataTableMapper<OperationType> {
    override val columns: List<ColumnId>
        get() = listOf(Column.Name.id, Column.Description.id)

    override fun getId(item: OperationType): String {
        return item.id
    }

    override fun updateItem(item: OperationType, columnId: ColumnId, cell: Cell): OperationType {
        return when (Column.requireColumn(columnId)) {
            Column.Description -> (cell as? Cell.EditTextCell)?.let {
                item.copy(description = it.value)
            }

            Column.Name -> (cell as? Cell.EditTextCell)?.let {
                item.copy(name = it.value)
            }
        } ?: item
    }

    override fun getCell(item: OperationType, columnId: ColumnId): Cell {
        return when (Column.requireColumn(columnId)) {
            Column.Description -> Cell.EditTextCell(value = item.description)
            Column.Name -> Cell.EditTextCell(value = item.name)
        }
    }

    private sealed class Column(val id: ColumnId) {
        object Name : Column(ColumnId(Tables.OperationTypes.name.name, "Имя", ColumnWidth.Wide))
        object Description : Column(ColumnId(Tables.OperationTypes.description.name, "Описание", ColumnWidth.Wide))
        companion object {
            fun requireColumn(id: ColumnId): Column {
                return when (id.key) {
                    Description.id.key -> Description
                    Name.id.key -> Name
                    else -> throw IllegalStateException("column with id: $id was not found in $this")
                }
            }
        }
    }
}