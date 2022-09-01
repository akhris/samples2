package ui.components.tables

import domain.IEntity

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

    data class ReferenceCell(
        val entityID: String,
        val referenceID: String,
        val refClass: Class<out IEntity>,
        val text: String
    ) : Cell()
}

data class ReferenceCellValue(val id: String, val text: String)