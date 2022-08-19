package ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.dp
import ui.composable.flowlayout.FlowCrossAxisAlignment
import ui.composable.flowlayout.FlowRow

import utils.darken


@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean = false,
    withCheckIcon: Boolean = true,
    checkIcon: @Composable (RowScope.(tint: Color) -> Unit)? = null,
    withBorder: Boolean = false,
    color: Color = MaterialTheme.colors.primary,
    onClick: (() -> Unit)? = null
) {

    val unselectedColor = remember(color) { color.copy(alpha = 0.1f) }
    val strokeColor = remember(color) { color.darken(0.2f) }


    val surfaceColor = remember(isSelected) {
        when (isSelected) {
            true -> color
            false -> unselectedColor
        }
    }

    val contentColor =
        MaterialTheme.colors.contentColorFor(surfaceColor).takeIf { it.isSpecified } ?: Color.Black

    Surface(
        color = surfaceColor,
        shape = RoundedCornerShape(16.dp),
        border = if (withBorder && isSelected) {
            BorderStroke(
                width = 2.dp,
                color = strokeColor
            )
        } else null,
        modifier = Modifier
            .wrapContentWidth(align = Alignment.CenterHorizontally)
            .padding(4.dp)
            .clickable {
                onClick?.invoke()
            }
    ) {
        // Inside a Row pack the Image and text together to
        // show inside the chip
        Row(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .defaultMinSize(minWidth = 48.dp)
                .wrapContentWidth()
        ) {
            if (isSelected && withCheckIcon) {
                when (checkIcon) {
                    null -> Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Selected chip",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = 4.dp),
                        tint = contentColor
                    )
                    else -> checkIcon(contentColor)
                }

            }

            Text(
                text = text,
                style = typography.body2,
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.CenterVertically),

                color = contentColor
            )
        }
    }
}


@Composable
fun ChipGroup(modifier: Modifier = Modifier, content: @Composable (() -> Unit)) {

    FlowRow(
        modifier.fillMaxWidth(),
        crossAxisSpacing = 2.dp,
        mainAxisSpacing = 2.dp,
        crossAxisAlignment = FlowCrossAxisAlignment.Start
    ) {
        content()
    }
}

@Preview
@Composable
fun TestChipGroup() {
    ChipGroup {
        FilterChip(text = "test1", isSelected = false, color = Color.Cyan)
        FilterChip(text = "test2", isSelected = true, color = Color.Cyan)
        FilterChip(text = "test3", isSelected = false, color = Color.Magenta)
        FilterChip(text = "test4", isSelected = true, color = Color.Magenta)
        FilterChip(text = "test5", isSelected = false, color = Color.Cyan)
        FilterChip(text = "test6", isSelected = true, color = Color.Cyan)
        FilterChip(text = "test7", isSelected = false, color = Color.Magenta)
        FilterChip(text = "test8", isSelected = true, color = Color.Magenta)
    }
}

@Preview
@Composable
fun TestChip() {
    Column(modifier = Modifier.size(240.dp).background(color = MaterialTheme.colors.surface)) {
        FilterChip(text = "test1", isSelected = false, color = Color.Cyan)
    }
}