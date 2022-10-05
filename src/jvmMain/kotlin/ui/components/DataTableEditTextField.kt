package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun DataTableEditTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String? = null,
    leadingIcon: @Composable (RowScope.() -> Unit)? = null,
    trailingIcon: @Composable (RowScope.() -> Unit)? = null
) {
    BasicTextField(modifier = modifier
        .background(
            MaterialTheme.colors.surface,
            MaterialTheme.shapes.small,
        )
        .fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
//        cursorBrush = SolidColor(MaterialTheme.colors.primary),
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colors.onSurface
        ),
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
                if (trailingIcon != null) trailingIcon()
            }
        }
    )
}