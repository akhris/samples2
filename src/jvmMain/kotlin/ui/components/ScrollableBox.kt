package ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ScrollableBox(
    modifier: Modifier = Modifier,
    innerHorizontalPadding: Dp = 0.dp,
    scrollState: ScrollState = rememberScrollState(),
    header: (@Composable BoxScope.() -> Unit)? = null,
    additionalContent: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Column(modifier) {
        Box(modifier.padding(horizontal = innerHorizontalPadding)) {
            header?.invoke(this)
        }
        Box(modifier) {
            Box(modifier = Modifier.verticalScroll(scrollState).padding(horizontal = innerHorizontalPadding)) {
                content()
            }

            additionalContent?.invoke(this)

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(scrollState)
            )
        }
    }
}