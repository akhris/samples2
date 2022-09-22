package ui.components.tables.mappers

import domain.*
import persistence.exposed.dto.Tables
import ui.components.tables.*

class OperationsDataMapper : IDataTableMapper<Operation> {
    override val columns: List<ColumnId> = listOf(
        Column.Sample.id,
        Column.OperationType.id,
        Column.DateTime.id,
        Column.Worker.id,
        Column.Place.id
    )

    override fun getId(item: Operation): String = item.id

    override fun updateItem(item: Operation, columnId: ColumnId, cell: Cell): Operation {
        return when (Column.requireColumn(columnId)) {
            Column.DateTime -> (cell as? Cell.DateTimeCell)?.let { item.copy(dateTime = it.value) }
            Column.OperationType -> (cell as? Cell.EntityCell)?.let {
                (it.entity as? OperationType)?.let { e ->
                    item.copy(operationType = e)
                }
            }

            Column.Place -> (cell as? Cell.EntityCell)?.let {
                (it.entity as? Place)?.let { e ->
                    item.copy(place = e)
                }
            }

            Column.Sample -> (cell as? Cell.EntityCell)?.let {
                (it.entity as? Sample)?.let { e ->
                    item.copy(sample = e)
                }
            }

            Column.Worker -> (cell as? Cell.EntityCell)?.let {
                (it.entity as? Worker)?.let { e ->
                    item.copy(worker = e)
                }
            }
        } ?: item
    }

    override fun getCell(item: Operation, columnId: ColumnId): Cell {
        return when (Column.requireColumn(columnId)) {
            Column.DateTime -> Cell.DateTimeCell(value = item.dateTime)
            Column.OperationType -> Cell.EntityCell.SimpleEntityCell(entity = item.operationType, OperationType::class)
            Column.Place -> Cell.EntityCell.SimpleEntityCell(entity = item.place, Place::class)
            Column.Sample -> Cell.EntityCell.SimpleEntityCell(entity = item.sample, Sample::class)
            Column.Worker -> Cell.EntityCell.SimpleEntityCell(entity = item.worker, Worker::class)
        }
    }


    private sealed class Column(val id: ColumnId) {
        object Sample :
            Column(
                ColumnId(
                    key = Tables.Operations.sample.name,
                    title = "Образец",
                    width = ColumnWidth.Small,
                    alignment = ColumnAlignment.End
                )
            )

        object OperationType : Column(ColumnId(key = Tables.Operations.operationType.name, title = "Тип операции"))
        object DateTime :
            Column(ColumnId(key = Tables.Operations.dateTime.name, title = "Дата", width = ColumnWidth.Wide))

        object Worker :
            Column(ColumnId(key = Tables.Operations.worker.name, title = "Сотрудник", width = ColumnWidth.Wide))

        object Place : Column(
            ColumnId(
                key = Tables.Operations.place.name,
                title = "Место",
                width = ColumnWidth.Small,
                alignment = ColumnAlignment.End
            )
        )


        companion object {
            fun requireColumn(id: ColumnId): Column {
                return when (id.key) {
                    Sample.id.key -> Sample
                    OperationType.id.key -> OperationType
                    DateTime.id.key -> DateTime
                    Worker.id.key -> Worker
                    Place.id.key -> Place
                    else -> throw IllegalStateException("column with id: $id was not found in $this")
                }
            }
        }
    }
}