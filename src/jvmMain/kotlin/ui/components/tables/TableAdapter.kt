package ui.components.tables

interface ITableAdapter {
    fun getColumnCount(): Int
    fun withHeader(): Boolean
    fun getHeader(column: Int): String
    fun getTotalRows(): Int
    fun getCellValue(column: Int, row: Int): String
    fun setCellValue(column: Int, row: Int, newValue: String)
}