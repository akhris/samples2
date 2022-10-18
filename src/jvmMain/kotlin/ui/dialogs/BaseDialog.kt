package ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.rememberDialogState
import ui.theme.DialogSettings

@Composable
fun BaseDialog(
    dialogState: DialogState = rememberDialogState(
        size = DpSize(
            width = DialogSettings.DatePickerSettings.defaultPickerWidth,
            height = DialogSettings.DatePickerSettings.defaultPickerHeight
        )
    ),
    onDismiss: () -> Unit,
    title: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit,
    buttons: @Composable RowScope.() -> Unit
) {

    val titleBGColor = MaterialTheme.colors.primarySurface

    Dialog(
        state = dialogState,
        onCloseRequest = onDismiss,
        undecorated = true,
        resizable = false,
        transparent = true,
//        focusable = false,
        content = {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    //title
                    WindowDraggableArea {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = titleBGColor)
                        ) {
                            CompositionLocalProvider(
                                LocalContentColor provides MaterialTheme.colors.contentColorFor(
                                    titleBGColor
                                ),
                                LocalTextStyle provides MaterialTheme.typography.h6
                            ) {
                                title()
                            }
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        content()
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.End) {
                        buttons()
                    }
                }
            }
        }
    )
}