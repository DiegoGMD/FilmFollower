package com.diegogmd.filmfollower.ui.theme

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.diegogmd.filmfollower.R

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    onPrimary = White100
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    onPrimary = White100

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

private val OldFilmColorScheme = darkColorScheme(
    // Accent: caramel fills with coffee text (buttons, check marks, selected states)
    primary = LightCaramel,
    onPrimary = DarkCoffee,
    primaryContainer = DarkCoffee,
    onPrimaryContainer = LightCaramel,

    // Supporting: copper for unselected nav/tab items
    secondary = FadedCopper,
    onSecondary = DarkCoffee,
    secondaryContainer = OliveWood, // nav-bar indicator pill (icon only, no text)
    onSecondaryContainer = LightCaramel,

    tertiary = FadedCopper,
    onTertiary = DarkCoffee,
    tertiaryContainer = DarkCoffee,
    onTertiaryContainer = LightCaramel,

    // Page background (as in the original scheme)
    background = OliveWood,
    onBackground = LightCaramel,

    // Caramel paper: ContentCard, EpisodeContentCard, Genres/Overview cards, search field
    surface = LightCaramel,
    onSurface = DarkCoffee,
    surfaceTint = LightCaramel,

    // Dark tonal panels: Settings sections, bottom sheet
    surfaceVariant = DarkCoffee,
    onSurfaceVariant = LightCaramel,

    // Default content color for these is onSurface (coffee), so keep them caramel.
    // Dark components should pass their container color explicitly, as the bars already do.
    surfaceContainerLowest = LightCaramel,
    surfaceContainerLow = LightCaramel,
    surfaceContainer = DarkCoffee,
    surfaceContainerHigh = LightCaramel,
    surfaceContainerHighest = LightCaramel, // <- CARD COLOR (default for Card { })

    outline = FadedCopper,
    outlineVariant = OliveWood,

    error = ErrorSoft,
    onError = DarkCoffee,

    // Snackbars: dark pill with caramel text/action
    inverseSurface = DarkCoffee,
    inverseOnSurface = LightCaramel,
    inversePrimary = LightCaramel,

    scrim = Black,
)

private val OldFilmBWColorScheme = darkColorScheme(
    // Accent: caramel fills with coffee text (buttons, check marks, selected states)
    primary = Silver,
    onPrimary = Graphite,
    primaryContainer = Graphite,
    onPrimaryContainer = Silver,

    // Supporting: copper for unselected nav/tab items
    secondary = GreyOlive,
    onSecondary = Graphite,
    secondaryContainer = DimGrey, // nav-bar indicator pill (icon only, no text)
    onSecondaryContainer = Silver,

    tertiary = GreyOlive,
    onTertiary = Graphite,
    tertiaryContainer = Graphite,
    onTertiaryContainer = Silver,

    // Page background (as in the original scheme)
    background = DimGrey,
    onBackground = Silver,

    // Caramel paper: ContentCard, EpisodeContentCard, Genres/Overview cards, search field
    surface = Silver,
    onSurface = Graphite,
    surfaceTint = Silver,

    // Dark tonal panels: Settings sections, bottom sheet
    surfaceVariant = Graphite,
    onSurfaceVariant = Silver,

    // Default content color for these is onSurface (coffee), so keep them caramel.
    // Dark components should pass their container color explicitly, as the bars already do.
    surfaceContainerLowest = Silver,
    surfaceContainerLow = Silver,
    surfaceContainer = Graphite,
    surfaceContainerHigh = Silver,
    surfaceContainerHighest = Silver, // <- CARD COLOR (default for Card { })

    outline = GreyOlive,
    outlineVariant = DimGrey,

    error = ErrorSoft,
    onError = Graphite,

    // Snackbars: dark pill with caramel text/action
    inverseSurface = Graphite,
    inverseOnSurface = Silver,
    inversePrimary = Silver,

    scrim = Black,
)

private val ColdBlueScheme = darkColorScheme(
    // Accent: caramel fills with coffee text (buttons, check marks, selected states)
    primary = SlateGrey,
    onPrimary = Black,
    primaryContainer = Black,
    onPrimaryContainer = SlateGrey,

    // Supporting: copper for unselected nav/tab items
    secondary = BalticBlue,
    onSecondary = Black,
    secondaryContainer = JetBlack, // nav-bar indicator pill (icon only, no text)
    onSecondaryContainer = SlateGrey,

    tertiary = BalticBlue,
    onTertiary = Black,
    tertiaryContainer = Black,
    onTertiaryContainer = SlateGrey,

    // Page background (as in the original scheme)
    background = JetBlack,
    onBackground = SlateGrey,

    // Caramel paper: ContentCard, EpisodeContentCard, Genres/Overview cards, search field
    surface = SlateGrey,
    onSurface = Black,
    surfaceTint = SlateGrey,

    // Dark tonal panels: Settings sections, bottom sheet
    surfaceVariant = Black,
    onSurfaceVariant = SlateGrey,

    // Default content color for these is onSurface (coffee), so keep them caramel.
    // Dark components should pass their container color explicitly, as the bars already do.
    surfaceContainerLowest = SlateGrey,
    surfaceContainerLow = SlateGrey,
    surfaceContainer = Black,
    surfaceContainerHigh = SlateGrey,
    surfaceContainerHighest = SlateGrey, // <- CARD COLOR (default for Card { })

    outline = BalticBlue,
    outlineVariant = JetBlack,

    error = ErrorSoft,
    onError = Black,

    // Snackbars: dark pill with caramel text/action
    inverseSurface = Black,
    inverseOnSurface = SlateGrey,
    inversePrimary = SlateGrey,

    scrim = Black,
)

private val MovieTimeScheme = darkColorScheme(
    // Accent: caramel fills with coffee text (buttons, check marks, selected states)
    primary = BrightGold,
    onPrimary = Black,
    primaryContainer = Black,
    onPrimaryContainer = BrightGold,

    // Supporting: copper for unselected nav/tab items
    secondary = Graphite,
    onSecondary = White100,
    secondaryContainer = CarbonBlack, // nav-bar indicator pill (icon only, no text)
    onSecondaryContainer = BrightGold,

    tertiary = Graphite,
    onTertiary = Black,
    tertiaryContainer = Black,
    onTertiaryContainer = BrightGold,

    // Page background (as in the original scheme)
    background = CarbonBlack,
    onBackground = BrightGold,

    // Caramel paper: ContentCard, EpisodeContentCard, Genres/Overview cards, search field
    surface = Graphite,
    onSurface = Black,
    surfaceTint = BrightGold,

    // Dark tonal panels: Settings sections, bottom sheet
    surfaceVariant = Black,
    onSurfaceVariant = BrightGold,

    // Default content color for these is onSurface (coffee), so keep them caramel.
    // Dark components should pass their container color explicitly, as the bars already do.
    surfaceContainerLowest = BrightGold,
    surfaceContainerLow = BrightGold,
    surfaceContainer = Black,
    surfaceContainerHigh = BrightGold,
    surfaceContainerHighest = Graphite, // <- CARD COLOR (default for Card { })

    outline = Graphite,
    outlineVariant = CarbonBlack,

    error = ErrorSoft,
    onError = Black,

    // Snackbars: dark pill with caramel text/action
    inverseSurface = Black,
    inverseOnSurface = BrightGold,
    inversePrimary = BrightGold,

    scrim = Black,
)

data class ThemePlaceholders(
    @DrawableRes val poster: Int,
)

val LocalPlaceholders = staticCompositionLocalOf {
    ThemePlaceholders(R.drawable.placeholder_poster_oldfilm)
}

val MaterialTheme.placeholders: ThemePlaceholders
    @Composable get() = LocalPlaceholders.current

private val OldFilmPlaceholders = ThemePlaceholders(R.drawable.placeholder_poster_oldfilm)
private val OldFilmBWPlaceholders = ThemePlaceholders(R.drawable.placeholder_poster_oldfilmbw)
private val ColdBluePlaceholders = ThemePlaceholders(R.drawable.placeholder_poster_coldblue)
private val MovieTimePlaceholders = ThemePlaceholders(R.drawable.placeholder_poster_movietime)

@Composable
fun FilmFollowerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    themeId: Int = ThemeManager.themeId,
    content: @Composable () -> Unit
) {
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }

    val colorScheme = when (themeId) {
        ThemeManager.OLD_FILM_BW -> OldFilmBWColorScheme
        ThemeManager.COLD_BLUE -> ColdBlueScheme
        ThemeManager.MOVIE_TIME -> MovieTimeScheme
        else -> OldFilmColorScheme
    }

    val posterPlaceholders = when (themeId) {
        ThemeManager.OLD_FILM_BW -> OldFilmBWPlaceholders
        ThemeManager.COLD_BLUE -> ColdBluePlaceholders
        ThemeManager.MOVIE_TIME -> MovieTimePlaceholders
        else -> OldFilmPlaceholders
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                colorScheme.background.luminance() > 0.5f // light background -> dark icons
        }
    }

    CompositionLocalProvider(LocalPlaceholders provides posterPlaceholders) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = FilmTypography,
            content = content
        )
    }
}