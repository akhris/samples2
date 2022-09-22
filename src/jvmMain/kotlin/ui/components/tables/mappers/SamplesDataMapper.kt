package ui.components.tables.mappers

import domain.Sample
import persistence.exposed.dto.Tables
import ui.components.tables.*

class SamplesDataMapper :
    IDataTableMapper<Sample> {

    override val columns: List<ColumnId> = listOf(
        Column.ID.id,
        Column.Description.id,
        Column.Comment.id,
        Column.OrderId.id
    )

    override fun getCell(item: Sample, columnId: ColumnId): Cell {
        return when (Column.requireColumn(columnId)) {
            Column.Comment -> Cell.EditTextCell(value = item.comment ?: "")
            Column.Description -> Cell.EditTextCell(value = item.description ?: "")
            Column.ID -> Cell.EditTextCell(value = item.identifier ?: "")
            Column.OrderId -> Cell.EditTextCell(value = item.orderID ?: "")
        }
    }

    override fun updateItem(item: Sample, columnId: ColumnId, cell: Cell): Sample {
        return when (Column.requireColumn(columnId)) {
            Column.Comment -> (cell as? Cell.EditTextCell)?.let { item.copy(comment = it.value) }
            Column.Description -> (cell as? Cell.EditTextCell)?.let { item.copy(description = it.value) }
            Column.ID -> (cell as? Cell.EditTextCell)?.let { item.copy(identifier = it.value) }
            Column.OrderId -> (cell as? Cell.EditTextCell)?.let { item.copy(orderID = it.value) }
        } ?: item
    }

    override fun getId(item: Sample): String = item.id

    private sealed class Column(val id: ColumnId) {
        object ID :
            Column(
                ColumnId(
                    key = Tables.Samples.sampleID.name,
                    title = "ID",
                    width = ColumnWidth.Small,
                    alignment = ColumnAlignment.End
                )
            )

        object Description :
            Column(ColumnId(key = Tables.Samples.description.name, title = "Описание", width = ColumnWidth.Wide))

        object Comment :
            Column(ColumnId(key = Tables.Samples.comment.name, title = "Комментарий", width = ColumnWidth.Wide))

        object OrderId : Column(ColumnId(key = Tables.Samples.orderID.name, title = "Партия"))

        companion object {
            fun requireColumn(id: ColumnId): Column {
                return when (id.key) {
                    ID.id.key -> ID
                    Description.id.key -> Description
                    Comment.id.key -> Comment
                    OrderId.id.key -> OrderId
                    else -> throw IllegalStateException("column with id: $id was not found in $this")
                }
            }
        }
    }
}