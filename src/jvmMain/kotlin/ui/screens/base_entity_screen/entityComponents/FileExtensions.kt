package ui.screens.base_entity_screen.entityComponents

import javax.swing.filechooser.FileNameExtensionFilter

sealed class FileExtensions(val descr: String, vararg val extensions: String) {
    object JSON : FileExtensions(descr = "JSON текстовые документы", "txt", "json")
    object EXCEL : FileExtensions(descr = "Протоколы измерений в формате EXCEL", "xls", "xlsx")
}

fun FileExtensions.toFileNameExtensionsFilter(): FileNameExtensionFilter =
    FileNameExtensionFilter(descr, *extensions)