package ui.components.tables.mappers

import domain.*
import domain.valueobjects.Factor
import persistence.exposed.dto.Tables
import ui.components.tables.Cell
import ui.components.tables.ColumnId
import ui.components.tables.ColumnWidth
import ui.components.tables.IDataTableMapper
import utils.replaceOrAdd

data class MeasurementsDataMapper(val parameters: List<Parameter> = listOf()) : IDataTableMapper<Measurement> {
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
        return when (val col = requireColumn(columnId)) {
            Column.Sample -> {
                (cell as? Cell.EntityCell)?.let {
                        item.copy(sample = it.entity as? Sample)
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
                item.copy(operator = it.entity as? Worker)
            }

            Column.Place -> {
                (cell as? Cell.EntityCell)?.let {
                    item.copy(place = it.entity as? Place)
                }
            }

            is Column.Result -> {
                (cell as? Cell.EditTextCell)?.let { textCell ->
                    val changedResult =
                        item.results.find { res -> res.parameter.id == col.parameter.id }?.copy(value = textCell.value)
                            ?: MeasurementResult(parameter = col.parameter, value = textCell.value)


                    item.copy(results = item.results.replaceOrAdd(changedResult) {
                        it.parameter.id == changedResult.parameter.id
                    })

                }
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

        object Sample : Column(ColumnId(Tables.Measurements.sample.name, "Образец", width = ColumnWidth.Small))
        object DateTime : Column(ColumnId(Tables.Measurements.dateTime.name, "Дата", width = ColumnWidth.Normal))
        object Operator : Column(ColumnId(Tables.Measurements.operator.name, "Оператор"))
        object Place : Column(ColumnId(Tables.Measurements.place.name, "Место", width = ColumnWidth.Small))
        object Comment : Column(ColumnId(Tables.Measurements.comment.name, "Комментарий"))
        object Conditions : Column(ColumnId(Tables.Measurements.conditions.name, "Условия"))
        class Result(val parameter: Parameter) :
            Column(
                ColumnId(
                    parameter.id,
                    title = parameter.name,
                    width = ColumnWidth.Small,
                    secondaryText = parameter.unit?.unit?.let { unit: String ->
                        ", "
                            .plus(parameter.factor?.let { if (it == Factor.NoFactor) "" else it.prefix } ?: "")
                            .plus(unit)
                    } ?: ""
                )
            )

    }
}