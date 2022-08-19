package ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private fun lightColors(iTheme: ITheme): Colors =
    lightColors(
        primary = iTheme.primary,
        primaryVariant = iTheme.primaryVariant,
        onPrimary = iTheme.onPrimary,
        secondary = iTheme.secondary,
        secondaryVariant = iTheme.secondaryVariant,
        onSecondary = iTheme.onSecondary,
        surface = iTheme.surface,
        onSurface = iTheme.onSurface,
        error = iTheme.error,
        background = iTheme.background,
        onBackground = iTheme.onBackground,
        onError = iTheme.onError
    )


private fun darkColors(iTheme: ITheme): Colors =
    darkColors(
        primary = iTheme.primary,
        primaryVariant = iTheme.primaryVariant,
        onPrimary = iTheme.onPrimary,
        secondary = iTheme.secondary,
        secondaryVariant = iTheme.secondaryVariant,
        onSecondary = iTheme.onSecondary,
        surface = iTheme.surface,
        onSurface = iTheme.onSurface,
        error = iTheme.error,
        background = iTheme.background,
        onBackground = iTheme.onBackground,
        onError = iTheme.onError
    )


/**
 * Object that holds current ITheme for the app.
 */
object AppTheme {
    val lightTheme: ITheme = ThemeGreenLight
    val darkTheme: ITheme = ThemeGreenDark

    @Composable
    fun currentTheme(isDark: Boolean = isSystemInDarkTheme()): ITheme = when (isDark) {
        true -> darkTheme
        false -> lightTheme
    }
}

@Composable
fun AppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (darkTheme)
            darkColors(AppTheme.darkTheme)
        else lightColors(AppTheme.lightTheme),
        content = content
    )
}
