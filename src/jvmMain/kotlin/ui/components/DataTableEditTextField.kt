package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.TextStyle

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DataTableEditTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String? = null,
    singleLine: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current.copy(
        color = MaterialTheme.colors.onSurface
    ),
    leadingIcon: @Composable (RowScope.() -> Unit)? = null,
    trailingIcon: @Composable (BoxScope.() -> Unit)? = null
) {

    var isHover by remember { mutableStateOf(false) }


    BasicTextField(modifier = modifier
        .background(
            MaterialTheme.colors.surface,
            MaterialTheme.shapes.small,
        )
        .onPointerEvent(PointerEventType.Enter) { isHover = true }
        .onPointerEvent(PointerEventType.Exit) { isHover = false }
        .fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
//        cursorBrush = SolidColor(MaterialTheme.colors.primary),
        textStyle = textStyle,
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
                        Modifier.alpha(if (isHover) 1f else 0.1f), contentAlignment = Alignment.Center
                    ) {
                        trailingIcon()
                    }
                }
            }
        }
    )
}