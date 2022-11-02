package ui.components.tables.mappers

import domain.Norm
import domain.Parameter
import domain.Unit
import domain.valueobjects.Factor
import persistence.exposed.dto.Tables
import ui.components.tables.Cell
import ui.components.tables.ColumnId
import ui.components.tables.IDataTableMapper
import utils.replaceOrAdd

data class ParametersDataMapper(val conditions: List<String> = listOf()) : IDataTableMapper<Parameter> {
    override val columns: List<ColumnId> = listOf(
        Column.Name.id, Column.Description.id, Column.Unit.id
    ).plus(conditions.distinct().map { Column.Norm(it).id })

    override fun getId(item: Parameter): String = item.id

    override fun updateItem(item: Parameter, columnId: ColumnId, cell: Cell): Parameter {
        return when (val column = requireColumn(columnId)) {
            Column.Description -> (cell as? Cell.EditTextCell)?.let { item.copy(description = it.value) }
            Column.Name -> (cell as? Cell.EditTextCell)?.let { item.copy(name = it.value) }
            Column.Unit -> (cell as? Cell.EntityCell)?.let {
                item.copy(unit = it.entity as? Unit, factor = it.tag as? Factor)
            }

            is Column.Norm -> {
                // TODO: put norm values into parameter
                val currentNorm = item.norms.find { it.condition == columnId.key } ?: Norm(condition = columnId.key)
                item.copy(norms = item.norms.replaceOrAdd(currentNorm) {
                    it.id == currentNorm.id
                })
            }
        } ?: item
    }

    override fun updatePosition(item: Parameter, position: Int): Parameter {
        return item.copy(position = position)
    }

    override fun getCell(item: Parameter, columnId: ColumnId): Cell {
        return when (val col = requireColumn(columnId)) {
            Column.Description -> Cell.EditTextCell(value = item.description)
            Column.Name -> Cell.EditTextCell(value = item.name)
            Column.Unit -> Cell.EntityCell(
                entity = item.unit, entityClass = Unit::class, tag = item.factor
            )

            is Column.Norm -> Cell.EditTextCell(value = item.norms.find { it.condition == col.id.key }?.let { norm ->
                //todo: create special type of Cell to handle multiple fields (notLess, notMore, average)
                val builder = StringBuilder()
                norm.notLess?.let {
                    builder.append(it)
                }
                builder.append("...")
                norm.notMore?.let {
                    builder.append(it)
                }
                builder.toString()
            } ?: "")
        }
    }

    private fun requireColumn(id: ColumnId): Column {
        return when (id.key) {
            Column.Name.id.key -> Column.Name
            Column.Description.id.key -> Column.Description
            Column.Unit.id.key -> Column.Unit
            else -> conditions.find { it == id.key }?.let {
                Column.Norm(it)
            } ?: throw IllegalStateException("column with id: $id was not found in $this")
        }
    }

    private sealed class Column(val id: ColumnId) {
        object Name : Column(ColumnId(Tables.Parameters.name.name, "Идентификатор"))
        object Description : Column(ColumnId(Tables.Parameters.description.name, "Описание"))
        object Unit : Column(ColumnId(Tables.Parameters.unit.name, "Единица измерения"))

        class Norm(condition: String) : Column(
            ColumnId(
                key = condition, title = "Норма", secondaryText = " ($condition)"
            )
        )

    }
}