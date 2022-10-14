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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
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
    leadingIcon: @Composable (RowScope.() -> Unit)? = null,
    trailingIcon: @Composable (BoxScope.() -> Unit)? = null
) {

    var isHover by remember { mutableStateOf(false) }

//    Surface(shape = MaterialTheme.shapes.small) {
    OutlinedTextField(modifier = modifier
        .onPointerEvent(PointerEventType.Enter) { isHover = true }
        .onPointerEvent(PointerEventType.Exit) { isHover = false }
        .fillMaxWidth(),
        enabled = onValueChange != null,
        value = value,
        onValueChange = { onValueChange?.invoke(it) },
        textStyle = textStyle,
        singleLine = singleLine,
        trailingIcon = trailingIcon?.let { ti ->
            {
                Box(
                    Modifier
                        .alpha(if (isHover) 1f else 0.1f)
                        .pointerHoverIcon(PointerIcon(Cursor(Cursor.DEFAULT_CURSOR))),
                    contentAlignment = Alignment.Center
                ) {
                    ti()
                }
            }
        },
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