package ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
            Surface(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.border(
                    color = MaterialTheme.colors.onSurface,
                    width = remember { 2.dp },
                    shape = MaterialTheme.shapes.medium
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    //title
                    WindowDraggableArea {
                        CompositionLocalProvider(
                            LocalContentColor provides MaterialTheme.colors.contentColorFor(
                                titleBGColor
                            ),
                            LocalTextStyle provides MaterialTheme.typography.h6
                        ) {
                            Row(
                                modifier = Modifier.background(color = titleBGColor),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    title()
                                }
                                Icon(
                                    modifier = Modifier.padding(16.dp).clickable { onDismiss() },
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "close dialog"
                                )
                            }
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        content()
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
                    ) {
                        buttons()
                    }
                }
            }
        }
    )
}