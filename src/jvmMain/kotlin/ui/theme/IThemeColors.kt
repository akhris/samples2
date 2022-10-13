package ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Interface that defines theme colors
 */
interface ITheme {
    val primary: Color
    val primaryVariant: Color
    val onPrimary: Color
    val secondary: Color
    val secondaryVariant: Color
    val onSecondary: Color
    val surface: Color
    val onSurface: Color
    val error: Color
    val onError: Color
    val background: Color
    val onBackground: Color
}


object ThemeGreenLight : ITheme {
    override val primary: Color = md_theme_light_primary
    override val primaryVariant: Color = md_theme_light_primaryContainer
    override val secondary: Color = md_theme_light_secondary
    override val secondaryVariant: Color = md_theme_light_secondaryContainer
    override val surface: Color = md_theme_light_surface
    override val error: Color = md_theme_light_error
    override val background: Color = md_theme_light_background

    override val onPrimary: Color = md_theme_light_onPrimary
    override val onSecondary: Color = md_theme_light_onSecondary
    override val onSurface: Color = md_theme_light_onSurface
    override val onError: Color = md_theme_light_onError
    override val onBackground: Color = md_theme_light_onBackground
}

object ThemeGreenDark : ITheme {
    override val primary: Color = md_theme_dark_primary
    override val primaryVariant: Color = md_theme_dark_primaryContainer
    override val secondary: Color = md_theme_dark_secondary
    override val secondaryVariant: Color = md_theme_dark_secondaryContainer
    override val surface: Color = md_theme_dark_surface
    override val error: Color = md_theme_dark_error
    override val background: Color = md_theme_dark_background

    override val onPrimary: Color = md_theme_dark_onPrimary
    override val onSecondary: Color = md_theme_dark_onSecondary
    override val onSurface: Color = md_theme_dark_onSurface
    override val onError: Color = md_theme_dark_onError
    override val onBackground: Color = md_theme_dark_onBackground
}
