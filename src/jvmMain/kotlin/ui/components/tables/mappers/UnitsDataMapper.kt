package ui.components.tables.mappers

import domain.Unit
import persistence.exposed.dto.Tables
import ui.components.tables.Cell
import ui.components.tables.ColumnId
import ui.components.tables.IDataTableMapper

class UnitsDataMapper : IDataTableMapper<Unit> {
    override val columns: List<ColumnId> = listOf(
        Column.Unit.id,
        Column.isMultipliable.id
    )

    override fun getId(item: Unit): String = item.id

    override fun updateItem(item: Unit, columnId: ColumnId, cell: Cell): Unit {
        return when (Column.requireColumn(columnId)) {
            Column.Unit -> (cell as? Cell.EditTextCell)?.let {
                item.copy(unit = it.value)
            }
            Column.isMultipliable -> (cell as? Cell.BooleanCell)?.let {
                item.copy(isMultipliable = it.value)
            }
        } ?: item

    }

    override fun getCell(item: Unit, columnId: ColumnId): Cell {
        return when (Column.requireColumn(columnId)) {
            Column.Unit -> Cell.EditTextCell(value = item.unit)
            Column.isMultipliable -> Cell.BooleanCell(value = item.isMultipliable)
        }
    }


    private sealed class Column(val id: ColumnId) {
        object Unit : Column(ColumnId(Tables.Units.unit.name, "Ед-ца измерения"))
        object isMultipliable : Column(ColumnId(Tables.Units.isMultipliable.name, "Умножаемое"))
        companion object {
            fun requireColumn(id: ColumnId): Column {
                return when (id.key) {
                    Unit.id.key -> Unit
                    isMultipliable.id.key -> isMultipliable
                    else -> throw IllegalStateException("column with id: $id was not found in $this")
                }
            }
        }
    }
}