package ui.components.tables

import domain.Operation

class OperationsAdapter(list: List<Operation>, onEntityChanged: (Operation) -> Unit) :
    BaseAdapter<Operation>(list = list, onEntityChanged = onEntityChanged) {

    override fun setEntityField(entity: Operation, column: Int, value: Cell): Operation {
        TODO("Not yet implemented")
    }

    override fun getEntityField(entity: Operation, column: Int): Cell {
        return when (column) {
            COLUMN_SAMPLE -> Cell.ReferenceCell(ReferenceCellValue(entity.sample.id, entity.sample.identifier ?: ""))
            COLUMN_OPERATION_TYPE -> Cell.TextCell("тип")
            COLUMN_DATE_TIME -> Cell.TextCell(entity.dateTime?.toString() ?: "")
            COLUMN_WORKER -> Cell.TextCell("сотрудник")
            COLUMN_PLACE -> Cell.TextCell("место")
            else -> throw IllegalStateException("column with position: $column was not found in $this")
        }
    }

    override fun getColumnCount(): Int {
        return 5
    }

    override fun getHeader(column: Int): String {
        return when (column) {
            COLUMN_SAMPLE -> "Образец"
            COLUMN_OPERATION_TYPE -> "Тип операции"
            COLUMN_DATE_TIME -> "Дата/время"
            COLUMN_WORKER -> "Сотрудник"
            COLUMN_PLACE -> "Место"
            else -> throw IllegalStateException("column with position: $column was not found in $this")
        }
    }


    companion object {
        private const val COLUMN_SAMPLE = 0
        private const val COLUMN_OPERATION_TYPE = 1
        private const val COLUMN_DATE_TIME = 2
        private const val COLUMN_WORKER = 3
        private const val COLUMN_PLACE = 4
    }
    /*
    val sample: Sample,
    val operationType: OperationType,
    val dateTime: LocalDateTime?,
    val worker: Worker,
    val place: Place
     */
}