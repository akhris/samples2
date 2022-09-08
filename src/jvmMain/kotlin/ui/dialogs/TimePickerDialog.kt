package ui.dialogs

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import kotlinx.coroutines.delay
import ui.theme.DialogSettings
import java.time.LocalDateTime
import kotlin.math.sign

/**
 * Material time picker dialog as described here:
 * https://material.io/components/time-pickers#specs
 */
@Composable
fun TimePickerDialog(
    initialTime: LocalDateTime? = null,
    onDismiss: () -> Unit,
    onTimeSelected: (LocalDateTime) -> Unit
) {


    val dialogState = rememberDialogState(
        size = DpSize(
            width = DialogSettings.TimePickerSettings.defaultPickerWidth,
            height = DialogSettings.TimePickerSettings.defaultPickerInputModeHeight
        )
    )

    Dialog(
        state = dialogState,
        onCloseRequest = onDismiss,
        undecorated = true,
        resizable = false,
        transparent = true,
        content = {
            TimePickerDialogContent(
                initialTime = initialTime,
                onCancelClick = onDismiss,
                onOkClick = {
                    onTimeSelected(it)
                    onDismiss()
                }
            )
        }
    )
}

@Composable
private fun TimePickerDialogContent(
    initialTime: LocalDateTime? = null,
    onCancelClick: (() -> Unit)? = null,
    onOkClick: ((LocalDateTime) -> Unit)? = null
) {
    var hour by remember(initialTime) { mutableStateOf((initialTime ?: LocalDateTime.now()).hour) }
    var minute by remember(initialTime) { mutableStateOf((initialTime ?: LocalDateTime.now()).minute) }
    Surface(shape = MaterialTheme.shapes.medium) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                //title
                Box(modifier = Modifier.height(28.dp)) {
                    Text(
                        modifier = Modifier.align(Alignment.BottomStart),
                        text = "введите время",
                        style = MaterialTheme.typography.overline,
                        color = MaterialTheme.colors.contentColorFor(MaterialTheme.colors.surface)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            //input
            Box {
                Row(modifier = Modifier.height(70.dp).fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.SpaceBetween) {

                    TimePickerTextField(
                        modifier = Modifier.width(96.dp).fillMaxHeight(),
                        initialValue = hour,
                        onValueChange = { hour = it },
                        valueRange = 0..23
                    )

                    Text(
                        modifier = Modifier.width(24.dp).align(Alignment.CenterVertically),
                        text = ":",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h4
                    )
                    TimePickerTextField(
                        modifier = Modifier.width(96.dp).fillMaxHeight(),
                        initialValue = minute,
                        onValueChange = { minute = it },
                        valueRange = 0..59
                    )
                }
            }

            //buttons
            Row(modifier = Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.End) {
                onCancelClick?.let {
                    TextButton(onClick = it, content = {
                        Text(text = "отмена".uppercase())
                    })
                }
                onOkClick?.let { onClick ->
                    TextButton(onClick = {
                        onClick(
                            (initialTime ?: LocalDateTime.now()).withHour(hour).withMinute(minute)
                        )
                    }, content = {
                        Text(text = "ок".uppercase())
                    })
                }
            }
        }
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TimePickerTextField(
    modifier: Modifier = Modifier,
    initialValue: Int,
    onValueChange: (Int) -> Unit,
    valueRange: IntRange
) {
    var isHover by remember { mutableStateOf(false) }

    var currentValue by remember { mutableStateOf(initialValue) }

    Box(modifier = modifier
        .border(
            width = 2.dp,
            color = if (isHover) MaterialTheme.colors.primary else Color.Unspecified,
            shape = MaterialTheme.shapes.small
        )
        .onPointerEvent(PointerEventType.Enter) { isHover = true }
        .onPointerEvent(PointerEventType.Exit) { isHover = false }
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()
                    if (event.type == PointerEventType.Scroll) {
                        val delta = event.changes.first().scrollDelta

                        currentValue = ((currentValue - sign(delta.y).toInt()).coerceIn(valueRange))
                        event.changes.first().consume()
                    }
                }
            }
        }) {
        BasicTextField(
            modifier = Modifier.align(Alignment.Center).wrapContentWidth(),
            value = "%02d".format(currentValue),
            onValueChange = {
                currentValue = (it.toIntOrNull() ?: 0).coerceIn(valueRange)
            },
            textStyle = MaterialTheme.typography.h4
        )
    }

    //debounce logic:
    LaunchedEffect(currentValue) {
        println("launched effect. currentValue=$currentValue initialValue=$initialValue")
        if (currentValue == initialValue) {
            return@LaunchedEffect
        }
        delay(500L)
        println("onValueChange: $currentValue")
        onValueChange(currentValue)
    }

}