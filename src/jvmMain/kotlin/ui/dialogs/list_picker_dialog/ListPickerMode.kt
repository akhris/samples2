package ui.dialogs.list_picker_dialog

sealed class ListPickerMode {
    class SingleSelect(
        val onItemSelected: ((String) -> Unit)? = null,
        val initialSelection: String? = null
    ) : ListPickerMode()
}
