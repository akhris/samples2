package ui.components.tables.mappers

import domain.OperationType
import ui.components.tables.Cell
import ui.components.tables.ColumnId
import ui.components.tables.IDataTableMapper

class OperationTypesDataMapper : IDataTableMapper<OperationType> {
    override val columns: List<ColumnId>
        get() = TODO("Not yet implemented")

    override fun getId(item: OperationType): String {
        TODO("Not yet implemented")
    }

    override fun updateItem(item: OperationType, columnId: ColumnId, cell: Cell): OperationType {
        TODO("Not yet implemented")
    }

    override fun getCell(item: OperationType, columnId: ColumnId): Cell {
        TODO("Not yet implemented")
    }
}