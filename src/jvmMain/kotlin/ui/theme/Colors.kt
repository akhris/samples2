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
    override val primary: Color = Color(0x27292b)
    override val primaryVariant: Color = Color(0x6C7D88)
    override val secondary: Color = Color(0xE040FB)
    override val secondaryVariant: Color = Color(0xEA80FC)
    override val surface: Color = Color(0x303335)
    override val error: Color = Color(0xF50057)
    override val background: Color = Color(0x000000)

    override val onPrimary: Color = Color(0x4e5a60)
    override val onSecondary: Color = Color(0x2979FF)
    override val onSurface: Color = Color(0xC4C4C4)
    override val onError: Color = Color(0x702320)
    override val onBackground: Color = Color(0xCACACA)

}
