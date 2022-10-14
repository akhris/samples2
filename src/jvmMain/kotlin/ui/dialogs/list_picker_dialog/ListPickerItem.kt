package ui.dialogs.list_picker_dialog

import java.util.UUID

data class ListPickerItem(val id: String = UUID.randomUUID().toString(), val title: String, val caption: String? = null)
