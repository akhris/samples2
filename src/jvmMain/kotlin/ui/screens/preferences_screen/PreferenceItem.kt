package ui.screens.preferences_screen

sealed class PreferenceItem {
    abstract val id: String
    abstract val name: String
    abstract val description: String

    data class FilePreference(
        override val id: String,
        override val name: String = "",
        override val description: String = "",
        val path: String
    ) : PreferenceItem()
}
