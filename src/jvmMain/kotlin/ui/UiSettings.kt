package ui

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object UiSettings {

    object Window {
        val initialWidth = 1024.dp
        val initialHeight = 768.dp
    }

    object Debounce {
        const val debounceTime: Long = 500L
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
        const val cellTextSize: Float = 10f
        val headerStateIconsSize: Dp = 18.dp
        val draggableAreaWidth: Dp = 10.dp
        val cellPadding: Dp = 4.dp
        val additionalRowWidth: Dp = 48.dp

        //material specs: https://material.io/components/data-tables#specs
        val rowHeight: Dp = 52.dp
        val headerRowHeight: Dp = 56.dp
        val columnPadding: Dp = 4.dp

        val columnDefaultWidthNormal: Dp = 180.dp
        val columnDefaultWidthWide: Dp = 240.dp
        val columnDefaultWidthSmall: Dp = 100.dp

        private val dividerAlpha = 0.12f

        val toolTipWidth = 360.dp
        val toolTipHeight = 240.dp

        val cornerRadius = 4.dp

        @Composable
//        fun dividerColor() = MaterialTheme.colors.onSurface.copy(alpha = dividerAlpha)
        fun dividerColor() = MaterialTheme.colors.background
    }

    object PaginationPanel {
        val rowsPerPageFieldWidth: Dp = 128.dp
        val panelHeight: Dp = 56.dp
    }

    object Dialogs {
        val defaultWideDialogWidth = 480.dp
        val defaultWideDialogHeight = 640.dp
        val defaultAlertDialogWidth = 280.dp

    }


}