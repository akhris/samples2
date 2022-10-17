package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import utils.conditional
import java.awt.Cursor

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DataTableEditTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: ((String) -> Unit)? = null,
    placeholderText: String? = null,
    singleLine: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    leadingIcon: @Composable (BoxScope.() -> Unit)? = null,
    trailingIcon: @Composable (BoxScope.() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current

    var isHover by remember { mutableStateOf(false) }
    val readOnly = remember(onValueChange) { onValueChange == null }
//    Surface(shape = MaterialTheme.shapes.small) {
    OutlinedTextField(modifier = modifier

        .onPreviewKeyEvent { ke ->
            if (ke.key == Key.Tab && ke.type == KeyEventType.KeyDown) {
                focusManager.moveFocus(FocusDirection.Right)
                true
            } else {
                false
            }
        }
        .onPointerEvent(PointerEventType.Enter) { isHover = true }
        .onPointerEvent(PointerEventType.Exit) { isHover = false }
        .pointerHoverIcon(PointerIcon(Cursor(if (readOnly) Cursor.DEFAULT_CURSOR else Cursor.TEXT_CURSOR)))
        .fillMaxWidth(),
        enabled = !readOnly,
        value = value,
        onValueChange = { onValueChange?.invoke(it) },
        textStyle = textStyle,
        singleLine = singleLine,
        leadingIcon = leadingIcon?.let { li ->
            {
                Box(
                    Modifier.pointerHoverIcon(PointerIcon(Cursor(Cursor.DEFAULT_CURSOR))),
                    contentAlignment = Alignment.Center
                ) {
                    li()
                }
            }
        },
        trailingIcon = if (isHover)
            trailingIcon?.let { ti ->
                {
                    Box(
                        Modifier.pointerHoverIcon(PointerIcon(Cursor(Cursor.DEFAULT_CURSOR))),
                        contentAlignment = Alignment.Center
                    ) {
                        ti()
                    }
                }
            } else null,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Unspecified,
            unfocusedBorderColor = Color.Unspecified,
            disabledBorderColor = Color.Unspecified,
            disabledTextColor = TextFieldDefaults.outlinedTextFieldColors().textColor(true).value
        ),
        shape = MaterialTheme.shapes.small
    )


//    }

    /*
            BasicTextField(modifier = modifier
                .onPointerEvent(PointerEventType.Enter) { isHover = true }
                .onPointerEvent(PointerEventType.Exit) { isHover = false }
                .fillMaxWidth(),
                enabled = onValueChange != null,
                value = value,
                onValueChange = { onValueChange?.invoke(it) },
                textStyle = textStyle,
                singleLine = singleLine,
                decorationBox = { innerTextField ->
                    Row(
                        modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (leadingIcon != null) leadingIcon()
                        Box(Modifier.weight(1f)) {
                            if (value.isEmpty() && placeholderText != null) Text(
                                placeholderText,
                                style = LocalTextStyle.current.copy(
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
                                )
                            )

                            innerTextField()

                        }
                        if (trailingIcon != null && isHover) {
                            Box(
                                Modifier
                                    .alpha(if (isHover) 1f else 0.1f)
                                    .pointerHoverIcon(PointerIcon(Cursor(Cursor.DEFAULT_CURSOR))),
                                contentAlignment = Alignment.Center
                            ) {
                                trailingIcon()
                            }
                        }
                    }
                }
            )


     */

}