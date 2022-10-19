package ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tooltip(tip: String, title: String? = null, content: @Composable () -> Unit) {
    TooltipArea(tooltip = {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column {
                title?.let {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = title,
                        style = MaterialTheme.typography.subtitle1
                    )
                }
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = tip,
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }, content = content)
}