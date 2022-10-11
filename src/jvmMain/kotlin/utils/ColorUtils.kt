package utils

import androidx.compose.ui.graphics.Color


object ColorUtils {

    fun darkenColor(color: Color, factor: Float = 0.2f): Color {
        return blendARGB(color, Color.Black, factor)
    }


    /**
     * Blend between two ARGB colors using the given ratio.
     *
     *
     * A blend ratio of 0.0 will result in `color1`, 0.5 will give an even blend,
     * 1.0 will result in `color2`.
     *
     * @param color1 the first ARGB color
     * @param color2 the second ARGB color
     * @param ratio  the blend ratio of `color1` to `color2`
     */

    fun blendARGB(
        color1: Color, color2: Color,
        ratio: Float
    ): Color {
        val inverseRatio = 1 - ratio
        val a: Float = color1.alpha * inverseRatio + color2.alpha * ratio
        val r: Float = color1.red * inverseRatio + color2.red * ratio
        val g: Float = color1.green * inverseRatio + color2.green * ratio
        val b: Float = color1.blue * inverseRatio + color2.blue * ratio
        return Color(alpha = a.toInt(), red = r.toInt(), green = g.toInt(), blue = b.toInt())
    }





}

fun Color.darken(factor: Float = 0.2f): Color = ColorUtils.darkenColor(this, factor)