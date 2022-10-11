package utils

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent

fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(modifier(this))
    } else {
        this
    }
}

/**
 * helper modifier function do invoke [doOnHover] when mouse pointer hovers the Composable
 */
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.onHover(doOnHover: (isHovered: Boolean) -> Unit): Modifier {
    return onPointerEvent(PointerEventType.Enter) {
        doOnHover(true)
    }.onPointerEvent(PointerEventType.Exit) {
        doOnHover(false)
    }
}