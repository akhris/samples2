package ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DropdownMenuItemWithIcon(
    icon: @Composable (BoxScope.() -> Unit)? = null,
    text: @Composable (BoxScope.() -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(modifier = Modifier.height(48.dp).clickable { onClick() }, verticalAlignment = Alignment.CenterVertically) {
        //icon:

        icon?.let { i ->
//            CompositionLocalProvider(LocalContentColor provides Color.Red) {
            Box(modifier = Modifier.padding(start = 24.dp).size(24.dp)) {
                i()
            }
//            }
        }
        text?.let { t ->
            Box(modifier = Modifier.weight(1f).padding(start = 20.dp, end = 24.dp)) {
                t()
            }
        }
    }
}