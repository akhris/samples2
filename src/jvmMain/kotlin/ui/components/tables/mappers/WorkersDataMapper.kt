package ui.components.tables.mappers

import domain.Worker
import ui.components.tables.Cell
import ui.components.tables.ColumnId
import ui.components.tables.IDataTableMapper

class WorkersDataMapper : IDataTableMapper<Worker> {
    override val columns: List<ColumnId>
        get() = TODO("Not yet implemented")

    override fun getId(item: Worker): String {
        TODO("Not yet implemented")
    }

    override fun updateItem(item: Worker, columnId: ColumnId, cell: Cell): Worker {
        TODO("Not yet implemented")
    }

    override fun getCell(item: Worker, columnId: ColumnId): Cell {
        TODO("Not yet implemented")
    }
}