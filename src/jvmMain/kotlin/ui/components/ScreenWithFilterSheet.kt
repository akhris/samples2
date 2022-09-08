package ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp


@Composable
fun ScreenWithFilterSheet(
    modifier: Modifier = Modifier,
    isOpened: Boolean = false,
    isModal: Boolean = false,
    withCloseButton: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
    mainScreenTitle: @Composable (BoxScope.() -> Unit)? = null,
    filterSheetTitle: @Composable (BoxScope.() -> Unit)? = null,
    filterContent: @Composable () -> Unit
) {
    var isFilterSheetOpened by remember { mutableStateOf(isOpened) }

    var windowWidth by remember { mutableStateOf(0.dp) }

    val filterSheetWidth = remember { 240.dp }

    val filterSheetOffset by animateDpAsState(
        targetValue = if (isFilterSheetOpened) {
            0.dp
        } else filterSheetWidth
    )
    Box(modifier = modifier.fillMaxSize().onSizeChanged {
        windowWidth = it.width.dp
    }) {
        Column(
            modifier = if (isModal) {
                modifier.width(windowWidth - filterSheetWidth + filterSheetOffset)
            } else modifier.fillMaxSize()
        ) {
            Surface(modifier = Modifier.wrapContentHeight().fillMaxWidth()) {
                Row(modifier = Modifier.wrapContentHeight().fillMaxWidth()) {
                    //main title row
                    Box(modifier = Modifier.weight(1f)) {
                        mainScreenTitle?.invoke(this)
                    }

                    if (filterSheetOffset == filterSheetWidth)
                        TextButton(onClick = { isFilterSheetOpened = true }, content = { Text(text = "filter") })
                }
            }
//            Divider(modifier = Modifier.fillMaxWidth())

            Box(
                modifier = Modifier.weight(1f), content = content
            )
        }


        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.End) {
            Surface(
                modifier = Modifier.width(filterSheetWidth).fillMaxHeight().offset(x = filterSheetOffset, y = 0.dp),
//                shape = MaterialTheme.shapes.medium,
                elevation = 1.dp
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        //title row
                        Box(modifier = Modifier.weight(1f)) {
                            filterSheetTitle?.invoke(this)
                        }
                        if (withCloseButton) {
                            IconButton(onClick = { isFilterSheetOpened = false }, content = {
                                Icon(imageVector = Icons.Rounded.Close, contentDescription = "close filter sheet")
                            })
                        }
                    }

                    ScrollableBox(modifier = Modifier.weight(1f)) {
                        filterContent()
                    }
                }
            }
        }
    }
}