package ui.components.tables

interface ITableAdapter {
    fun getColumnCount(): Int
    fun withHeader(): Boolean
    fun getHeader(column: Int): String
    fun getTotalRows(): Int
    fun getCellValue(column: Int, row: Int): Cell
    fun setCellValue(column: Int, row: Int, newValue: Cell)
}

sealed class Cell {
    data class TextCell(val text: String) : Cell()

    data class ReferenceCell<T>(val items: List<T>, val selectedItem: T) : Cell()
}