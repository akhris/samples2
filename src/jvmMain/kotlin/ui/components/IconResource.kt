package ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource

sealed class IconResource {
    class ImageVectorIcon(val icon: ImageVector) : IconResource()
    class PainterResourceIcon(val pathToIcon: String) : IconResource()
}

@Composable
fun IconResource.toVectorPainter(): Painter {
    return when (this) {
        is IconResource.ImageVectorIcon -> {
            rememberVectorPainter(this.icon)
        }

        is IconResource.PainterResourceIcon -> {
            painterResource(this.pathToIcon)
        }
    }
}