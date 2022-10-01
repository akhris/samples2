package utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

fun Modifier.conditional(condition : Boolean, modifier : Modifier.() -> Modifier) : Modifier {
    return if (condition) {
        then(modifier(this))
    } else {
        this
    }
}