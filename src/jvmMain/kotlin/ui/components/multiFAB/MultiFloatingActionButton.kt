package ui.components.multiFAB

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.components.IconResource
import ui.components.toVectorPainter

@Composable
fun MultiFloatingActionButton(
    modifier: Modifier = Modifier,
    fabIcon: IconResource,
    items: List<MultiFabItem>,
    toState: MultiFabState,
    showLabels: Boolean = true,
    stateChanged: (fabstate: MultiFabState) -> Unit,
    onFabItemClicked: (item: MultiFabItem) -> Unit
) {

    if (items.size == 1) {
        //show regular FAB
        val item = remember(items) { items.firstOrNull() } ?: return

        FloatingActionButton(modifier = modifier, onClick = { onFabItemClicked(item) }, content = {
            when (item.icon) {
                is IconResource.ImageVectorIcon -> {
                    Icon(
                        imageVector = item.icon.icon,
                        contentDescription = null
                    )
                }

                is IconResource.PainterResourceIcon -> {
                    Icon(
                        painter = painterResource(item.icon.pathToIcon),
                        contentDescription = null
                    )
                }
            }
        })

    } else {
        //show multi FAB
        val transition: Transition<MultiFabState> = updateTransition(targetState = toState)
        val scale: Float by transition.animateFloat { state ->
            if (state == MultiFabState.EXPANDED) 1f else 0f
        }
        val alpha: Float by transition.animateFloat(
            transitionSpec = {
                tween(durationMillis = 50)
            }
        ) { state ->
            if (state == MultiFabState.EXPANDED) 1f else 0f
        }
        val shadow: Dp by transition.animateDp(
            transitionSpec = {
                tween(durationMillis = 50)
            }
        ) { state ->
            if (state == MultiFabState.EXPANDED) 2.dp else 0.dp
        }
        val rotation: Float by transition.animateFloat { state ->
            if (state == MultiFabState.EXPANDED) 45f else 0f
        }
        Column(modifier = modifier, horizontalAlignment = Alignment.End) {
            items.forEach { item ->
                MiniFabItem(item, alpha, shadow, scale, showLabels, onFabItemClicked)
                Spacer(modifier = Modifier.height(20.dp))
            }
            FloatingActionButton(onClick = {
                stateChanged(
                    if (transition.currentState == MultiFabState.EXPANDED) {
                        MultiFabState.COLLAPSED
                    } else MultiFabState.EXPANDED
                )
            }) {


                when (fabIcon) {
                    is IconResource.ImageVectorIcon -> {
                        Icon(
                            modifier = Modifier.rotate(rotation),
                            imageVector = fabIcon.icon,
                            contentDescription = null
                        )
                    }

                    is IconResource.PainterResourceIcon -> {
                        Icon(
                            modifier = Modifier.rotate(rotation),
                            painter = painterResource(fabIcon.pathToIcon),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniFabItem(
    item: MultiFabItem,
    alpha: Float,
    shadow: Dp,
    scale: Float,
    showLabel: Boolean,
    onFabItemClicked: (item: MultiFabItem) -> Unit
) {
    val fabColor = MaterialTheme.colors.secondary
    val shadowColor = Color.Black.copy(alpha = 0.2f).toArgb()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 12.dp)
    ) {


        if (showLabel) {
            Text(
                item.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(animateFloatAsState(alpha).value)
                    .shadow(animateDpAsState(shadow).value)
                    .background(color = MaterialTheme.colors.surface)
                    .padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
        }


        Column(modifier = Modifier.alpha(alpha)) {

            FloatingActionButton(modifier =
            Modifier
                .size(32.dp)
                .scale(scale),
                content = {
                    when (item.icon) {
                        is IconResource.ImageVectorIcon -> Icon(
                            imageVector = item.icon.icon, contentDescription = null
                        )

                        is IconResource.PainterResourceIcon -> Icon(
                            painter = painterResource(resourcePath = item.icon.pathToIcon),
                            contentDescription = null
                        )
                    }
                }, onClick = { onFabItemClicked(item) })
        }

    }
}

@Composable
private fun Minimal(
    item: MultiFabItem,
    alpha: Float,
    scale: Float,
    onFabItemClicked: (item: MultiFabItem) -> Unit
) {
    val fabColor = MaterialTheme.colors.secondary
    val painter = item.icon.toVectorPainter()
    Canvas(
        modifier = Modifier.size(32.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                onClick = { onFabItemClicked(item) },
                indication = rememberRipple(
                    bounded = false,
                    radius = 20.dp,
                    color = MaterialTheme.colors.onSecondary
                )
            )
    ) {
        drawCircle(color = fabColor, scale)
        with(painter) {
            translate(
                left = (this@Canvas.center.x) - (intrinsicSize.width / 2),
                top = (this@Canvas.center.y) - (intrinsicSize.width / 2)
            ) {
                draw(intrinsicSize)
            }
        }
//        drawImage(
//            item.icon,
//            topLeft = Offset(
//                (this.center.x) - (item.icon.width / 2),
//                (this.center.y) - (item.icon.width / 2)
//            ),
//            alpha = alpha
//        )
    }
}