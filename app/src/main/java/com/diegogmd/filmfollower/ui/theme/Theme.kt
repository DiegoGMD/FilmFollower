package com.diegogmd.filmfollower.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

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
    secondaryContainer = OliveWood,          // nav-bar indicator pill (icon only, no text)
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
    surfaceContainerHighest = LightCaramel,  // <- CARD COLOR (default for Card { })

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

//private val OldFilmColorScheme = darkColorScheme(
//    primary = LightCaramel,
//    onPrimary = DarkCoffee,
//    primaryContainer = OliveWood,
//    onPrimaryContainer = LightCaramel,
//
//    secondary = FadedCopper,
//    onSecondary = DarkCoffee,
//    secondaryContainer = FadedCopper,
//    onSecondaryContainer = DarkCoffee,
//
//    tertiary = OliveWood,
//    onTertiary = LightCaramel,
//    tertiaryContainer = DarkCoffee,
//    onTertiaryContainer = LightCaramel,
//
//    background = OliveWood,
//    onBackground = LightCaramel,
//
//    surface = LightCaramel,
//    onSurface = DarkCoffee,
//    surfaceVariant = OliveWood,
//    onSurfaceVariant = LightCaramel,
//
//    outline = FadedCopper,
//    outlineVariant = OliveWood,
//
//    inverseSurface = LightCaramel,
//    inverseOnSurface = DarkCoffee,
//    inversePrimary = DarkCoffee,
//)


@Composable
fun FilmFollowerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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

    val colorScheme = OldFilmColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FilmTypography,
        content = content
    )
}