package ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object UiSettings {

    object Window {
        val minWidth = 640.dp
    }

    object Debounce {
        val debounceTime: Long = 1000L
    }

    object AppBar {
        val titleStartPadding = 32.dp
    }

    object NavigationPanel {
        val iconSize = 24.dp
        val widthExpanded = 240.dp
        val widthCollapsed = 72.dp
        val widthCollapsedDefault = 72.dp
        val elevation = 1.dp
    }

    object SampleTypesSelector {
        val selectorWidth = 320.dp
        val dropDownIconSize = 32.dp
    }

    object DataTable {
        val minCellWidth: Dp = 180.dp
        val gridLinesColor = Color.LightGray.copy(alpha = 0.5f)
        val gridLinesWidth = 1.dp
        val cellPadding: Dp = 4.dp
        val selectionRowWidth: Dp = 48.dp

        //material specs: https://material.io/components/data-tables#specs
        val rowHeight: Dp = 52.dp
        val headerRowHeight: Dp = 56.dp
        val columnPadding: Dp = 16.dp
    }

    object Dialogs {
        val defaultWideDialogWidth = 480.dp
        val defaultWideDialogHeight = 640.dp
        val defaultAlertDialogWidth = 280.dp

    }


}