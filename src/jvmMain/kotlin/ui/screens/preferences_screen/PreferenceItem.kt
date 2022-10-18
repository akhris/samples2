package ui.screens.preferences_screen

sealed class PreferenceItem {
    abstract val id: String

    data class FilePreference(override val id: String, val path: String) : PreferenceItem()
}
