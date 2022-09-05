package ui.components.tables.mappers

import domain.Place
import ui.components.tables.Cell
import ui.components.tables.ColumnId
import ui.components.tables.IDataTableMapper

class PlacesDataMapper : IDataTableMapper<Place> {
    override val columns: List<ColumnId>
        get() = TODO("Not yet implemented")

    override fun getId(item: Place): String {
        TODO("Not yet implemented")
    }

    override fun updateItem(item: Place, columnId: ColumnId, cell: Cell): Place {
        TODO("Not yet implemented")
    }

    override fun getCell(item: Place, columnId: ColumnId): Cell {
        TODO("Not yet implemented")
    }
}