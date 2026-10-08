package com.diegogmd.filmfollower.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.diegogmd.filmfollower.R

val CourierPrime = FontFamily(
    Font(R.font.courierprime_regular, FontWeight.Normal),
    Font(R.font.courierprime_bold, FontWeight.Bold),
    Font(R.font.courierprime_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.courierprime_bolditalic, FontWeight.Bold, FontStyle.Italic),
)

val PlayfairDisplay = FontFamily(
    Font(R.font.playfairdisplay_medium, FontWeight.Medium),
    Font(R.font.playfairdisplay_bold, FontWeight.Bold),
    Font(R.font.playfairdisplay_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.playfairdisplay_bolditalic, FontWeight.Bold, FontStyle.Italic)
)

private fun serif(
    size: TextUnit,
    lineHeight: TextUnit,
    weight: FontWeight = FontWeight.Bold,
    letterSpacing: TextUnit = 0.5.sp,
    fontStyle: FontStyle = FontStyle.Normal
) = TextStyle(
    fontFamily = PlayfairDisplay,
    fontWeight = weight,
    fontStyle = fontStyle,
    fontSize = size,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing
)

// Courier Prime = the "typewriter" voice (tabs, nav labels, buttons, metadata)
private fun typewriter(
    size: TextUnit,
    lineHeight: TextUnit,
    weight: FontWeight = FontWeight.Bold
) = TextStyle(
    fontFamily = CourierPrime,
    fontWeight = weight,
    fontSize = size,
    lineHeight = lineHeight,
    letterSpacing = 0.5.sp
)

// lineHeight in `em` scales with any fontSize passed at the call site, so the many
// `Text(fontSize = ..., style = FilmTypography.titleMedium)` usages never clip.
val FilmTypography = Typography(
    // Display: screen titles (Settings uses displayMedium)
    displayLarge = serif(57.sp, 64.sp, letterSpacing = 0.sp),
    displayMedium = serif(44.sp, 52.sp, letterSpacing = 1.sp),
    displaySmall = serif(36.sp, 44.sp, letterSpacing = 1.sp),

    // Headline: page/film titles (ContentHeader, ProfileHeader use 32sp / 26sp / 22sp)
    headlineLarge = serif(32.sp, 38.sp, letterSpacing = 1.sp),
    headlineMedium = serif(28.sp, 34.sp, letterSpacing = 1.sp),
    headlineSmall = serif(24.sp, 30.sp, letterSpacing = 1.sp),

    // Titles
    titleLarge = serif(22.sp, 28.sp, letterSpacing = 1.sp),
    titleMedium = serif(20.sp, 1.2.em, letterSpacing = 1.sp), // section headings, empty-state text
    titleSmall = serif(14.sp, 1.3.em, fontStyle = FontStyle.Italic), // original title

    // Body
    bodyLarge = serif(16.sp, 24.sp, FontWeight.Medium), // default Text style
    bodyMedium = serif(14.sp, 20.sp, FontWeight.Medium), // card text
    bodySmall = serif(12.sp, 16.sp, FontWeight.Medium), // supporting / error text

    // Labels
    labelLarge = typewriter(16.sp, 24.sp), // Button text
    labelMedium = typewriter(14.sp, 24.sp), // tabs + nav labels
    labelSmall = typewriter(12.sp, 16.sp, FontWeight.Normal),
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

//val FilmTypography = Typography(
//    titleMedium = TextStyle(
//        fontFamily = PlayfairDisplay_Bold,
//        fontWeight = FontWeight.Bold,
//        fontSize = 45.sp,
//        letterSpacing = 1.sp
//    ),
//    titleSmall = TextStyle(
//        fontFamily = PlayfairDisplay_Italic,
//        fontWeight = FontWeight.Bold,
//        fontSize = 14.sp,
//        letterSpacing = 0.5.sp
//    ),
//    bodyLarge = TextStyle(
//        fontFamily = PlayfairDisplay_Medium,
//        fontWeight = FontWeight.Normal,
//        fontSize = 14.sp,
//        lineHeight = 24.sp,
//        letterSpacing = 0.5.sp
//    ),
//    labelMedium = TextStyle(
//        fontFamily = CourierPrime_Bold,
//        fontWeight = FontWeight.Normal,
//        fontSize = 14.sp,
//
//        lineHeight = 24.sp,
//        letterSpacing = 0.5.sp
//    )
//)