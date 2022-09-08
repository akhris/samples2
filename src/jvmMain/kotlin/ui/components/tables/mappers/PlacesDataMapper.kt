package ui.components.tables.mappers

import domain.Place
import persistence.dto.Tables
import ui.components.tables.Cell
import ui.components.tables.ColumnId
import ui.components.tables.IDataTableMapper

class PlacesDataMapper : IDataTableMapper<Place> {
    override val columns: List<ColumnId>
        get() = listOf(Column.RoomNumber.id, Column.Description.id)

    override fun getId(item: Place): String {
        return item.id
    }

    override fun updateItem(item: Place, columnId: ColumnId, cell: Cell): Place {
        return when (Column.requireColumn(columnId)) {
            Column.Description -> (cell as? Cell.EditTextCell)?.let {
                item.copy(description = it.value)
            }

            Column.RoomNumber -> (cell as? Cell.EditTextCell)?.let {
                item.copy(roomNumber = it.value)
            }
        } ?: item
    }

    override fun getCell(item: Place, columnId: ColumnId): Cell {
        return when (Column.requireColumn(columnId)) {
            Column.Description -> Cell.EditTextCell(value = item.description)
            Column.RoomNumber -> Cell.EditTextCell(value = item.roomNumber)
        }
    }

    private sealed class Column(val id: ColumnId) {
        object RoomNumber : Column(ColumnId(Tables.Places.roomNumber.name, "Номер комнаты"))
        object Description : Column(ColumnId(Tables.Places.description.name, "Описание"))
        companion object {
            fun requireColumn(id: ColumnId): Column {
                return when (id.key) {
                    Description.id.key -> Description
                    RoomNumber.id.key -> RoomNumber
                    else -> throw IllegalStateException("column with id: $id was not found in $this")
                }
            }
        }
    }
}