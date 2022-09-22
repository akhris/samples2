package ui.components.tables.mappers

import domain.Parameter
import domain.Unit
import persistence.exposed.dto.Tables
import ui.components.tables.Cell
import ui.components.tables.ColumnId
import ui.components.tables.IDataTableMapper

class ParametersDataMapper : IDataTableMapper<Parameter> {
    override val columns: List<ColumnId> = listOf(
        Column.Name.id,
        Column.Description.id,
        Column.Unit.id
    )

    override fun getId(item: Parameter): String = item.id

    override fun updateItem(item: Parameter, columnId: ColumnId, cell: Cell): Parameter {
        return when (Column.requireColumn(columnId)) {
            Column.Description -> (cell as? Cell.EditTextCell)?.let { item.copy(description = it.value) }
            Column.Name -> (cell as? Cell.EditTextCell)?.let { item.copy(name = it.value) }
            Column.Unit -> (cell as? Cell.EntityCell.UnitCell)?.let {
                item.copy(unit = it.entity as? Unit, factor = it.factor)
            }
        } ?: item
    }

    override fun getCell(item: Parameter, columnId: ColumnId): Cell {
        return when (Column.requireColumn(columnId)) {
            Column.Description -> Cell.EditTextCell(value = item.description)
            Column.Name -> Cell.EditTextCell(value = item.name)
            Column.Unit -> Cell.EntityCell.UnitCell(
                entity = item.unit,
                entityClass = Unit::class,
                unit = item.unit,
                factor = item.factor
            )
        }
    }

    private sealed class Column(val id: ColumnId) {
        object Name : Column(ColumnId(Tables.Parameters.name.name, "Идентификатор"))
        object Description : Column(ColumnId(Tables.Parameters.description.name, "Описание"))
        object Unit : Column(ColumnId(Tables.Parameters.unit.name, "Единица измерения"))

        companion object {
            fun requireColumn(id: ColumnId): Column {
                return when (id.key) {
                    Name.id.key -> Name
                    Description.id.key -> Description
                    Unit.id.key -> Unit
                    else -> throw IllegalStateException("column with id: $id was not found in $this")
                }
            }
        }
    }
}