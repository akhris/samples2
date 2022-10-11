package ui.components.tables.mappers

import domain.SampleType
import persistence.exposed.dto.Tables
import ui.components.tables.Cell
import ui.components.tables.ColumnId
import ui.components.tables.IDataTableMapper

class SampleTypesDataMapper : IDataTableMapper<SampleType> {
    override val columns: List<ColumnId>
        get() = listOf(Column.Name.id, Column.Description.id)

    override fun getId(item: SampleType): String {
        return item.id
    }

    override fun updateItem(item: SampleType, columnId: ColumnId, cell: Cell): SampleType {
        return when (Column.requireColumn(columnId)) {
            Column.Description -> (cell as? Cell.EditTextCell)?.let {
                item.copy(description = it.value)
            }

            Column.Name -> (cell as? Cell.EditTextCell)?.let {
                item.copy(name = it.value)
            }
        } ?: item
    }

    override fun getCell(item: SampleType, columnId: ColumnId): Cell {
        return when (Column.requireColumn(columnId)) {
            Column.Description -> Cell.EditTextCell(value = item.description)
            Column.Name -> Cell.EditTextCell(value = item.name)
        }
    }

    private sealed class Column(val id: ColumnId) {
        object Name : Column(ColumnId(Tables.SampleTypes.name.name, "Имя"))
        object Description : Column(ColumnId(Tables.SampleTypes.description.name, "Описание"))
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