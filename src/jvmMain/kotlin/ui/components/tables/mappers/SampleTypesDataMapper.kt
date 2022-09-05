package ui.components.tables.mappers

import domain.SampleType
import ui.components.tables.Cell
import ui.components.tables.ColumnId
import ui.components.tables.IDataTableMapper

class SampleTypesDataMapper : IDataTableMapper<SampleType> {
    override val columns: List<ColumnId>
        get() = TODO("Not yet implemented")

    override fun getId(item: SampleType): String {
        TODO("Not yet implemented")
    }

    override fun updateItem(item: SampleType, columnId: ColumnId, cell: Cell): SampleType {
        TODO("Not yet implemented")
    }

    override fun getCell(item: SampleType, columnId: ColumnId): Cell {
        TODO("Not yet implemented")
    }
}