package ui.components.tables.mappers

import domain.*
import persistence.dto.Tables
import ui.components.tables.Cell
import ui.components.tables.ColumnId
import ui.components.tables.IDataTableMapper

class MeasurementsDataMapper(private val parameters: List<Parameter>) : IDataTableMapper<Measurement> {
    override val columns: List<ColumnId>
        get() = listOf(
            Column.Sample.id,
            Column.Operator.id,
            Column.DateTime.id,
            Column.Place.id,
            Column.Comment.id,
            Column.Conditions.id
        ).plus(parameters.map { Column.Result(it).id })

    override fun getId(item: Measurement): String = item.id

    override fun updateItem(item: Measurement, columnId: ColumnId, cell: Cell): Measurement {
        return when (requireColumn(columnId)) {
            Column.Sample -> {
                (cell as? Cell.EntityCell)?.let {
                    (it.entity as? Sample)?.let { e ->
                        item.copy(sample = e)
                    }
                }
            }

            Column.Comment -> {
                (cell as? Cell.EditTextCell)?.let {
                    item.copy(comment = it.value)
                }
            }

            Column.Conditions -> {
                (cell as? Cell.EditTextCell)?.let {
                    item.copy(conditions = it.value)
                }
            }

            Column.DateTime -> {
                (cell as? Cell.DateTimeCell)?.let {
                    item.copy(dateTime = it.value)
                }
            }

            Column.Operator -> (cell as? Cell.EntityCell)?.let {
                (it.entity as? Worker)?.let { e ->
                    item.copy(operator = e)
                }
            }

            Column.Place -> {
                (cell as? Cell.EntityCell)?.let {
                    (it.entity as? Place)?.let { e ->
                        item.copy(place = e)
                    }
                }
            }

            is Column.Result -> {
                TODO("not yet implemented")
            }
        } ?: item
    }

    override fun getCell(item: Measurement, columnId: ColumnId): Cell {
        return when (val col = requireColumn(columnId)) {
            Column.Sample -> Cell.EntityCell(entity = item.sample, Sample::class)
            Column.Comment -> Cell.EditTextCell(value = item.comment ?: "")
            Column.Conditions -> Cell.EditTextCell(value = item.conditions ?: "")
            Column.DateTime -> Cell.DateTimeCell(value = item.dateTime)
            Column.Operator -> Cell.EntityCell(entity = item.operator, Worker::class)
            Column.Place -> Cell.EntityCell(entity = item.place, Place::class)
            is Column.Result -> {
                //todo make Cell.ResultCell with unit
                Cell.EditTextCell(value = item.results.find { it.parameter.id == col.parameter.id }?.value ?: "")
            }
        }
    }

    private fun requireColumn(id: ColumnId): Column {
        return when (id.key) {
            Column.Sample.id.key -> Column.Sample
            Column.DateTime.id.key -> Column.DateTime
            Column.Operator.id.key -> Column.Operator
            Column.Place.id.key -> Column.Place
            Column.Comment.id.key -> Column.Comment
            Column.Conditions.id.key -> Column.Conditions
            else -> parameters.find { it.id == id.key }?.let {
                //id in results:
                Column.Result(it)
            } ?: throw IllegalStateException("column with id: $id was not found in $this")
        }
    }

    private sealed class Column(val id: ColumnId) {

        object Sample : Column(ColumnId(Tables.Measurements.sample.name, "Образец"))
        object DateTime : Column(ColumnId(Tables.Measurements.dateTime.name, "Дата"))
        object Operator : Column(ColumnId(Tables.Measurements.operator.name, "Оператор"))
        object Place : Column(ColumnId(Tables.Measurements.place.name, "Место"))
        object Comment : Column(ColumnId(Tables.Measurements.comment.name, "Комментарий"))
        object Conditions : Column(ColumnId(Tables.Measurements.conditions.name, "Условия"))
        class Result(val parameter: Parameter) : Column(ColumnId(parameter.id, title = parameter.name))

    }
}