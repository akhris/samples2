package ui.theme

import androidx.compose.ui.unit.dp


object AppSettings {
    val appTitle = "Components"
}

object NavigationPanelSettings {
    val widthExpanded = 240.dp
    val widthCollapsed = 72.dp
    val elevation = 1.dp
}

object ContentSettings {
    val contentHorizontalPadding = 16.dp
    val contentCardMinWidth = 360.dp
    val contentCardMaxWidth = 640.dp
    val contentCardHeight = 360.dp
    val stickyHeaderElevationOnScroll = 1.dp
    val stickyHeaderElevationOnRest = 1.dp
}

object DialogSettings {
    val defaultWideDialogWidth = 480.dp
    val defaultWideDialogHeight = 640.dp
    val defaultAlertDialogWidth = 280.dp

    object DatePickerSettings {
        val defaultPickerWidth = 328.dp
        val defaultPickerHeight = 512.dp
        val defaultPickerTitleHeight = 120.dp
        val defaultHorizontalPadding = 12.dp
        val defaultDateCellSize = 40.dp
        val defaultDateCellSelectionSize = 36.dp
        val defaultIconSize = 24.dp

        val defaultYearCellWidth = 88.dp
        val defaultYearCellHeight = 52.dp
        val defaultYearCellSelectionWidth = 72.dp
        val defaultYearCellSelectionHeight = 36.dp
    }

    object TimePickerSettings{
        val defaultPickerWidth = 328.dp
        val defaultPickerHeight = 512.dp
        val defaultPickerInputModeHeight = 218.dp
    }
}

