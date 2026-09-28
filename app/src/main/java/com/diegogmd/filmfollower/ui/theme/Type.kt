package com.diegogmd.filmfollower.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.diegogmd.filmfollower.R

val CourierPrime_Regular = FontFamily(
    Font(R.font.courierprime_regular, FontWeight.Normal)
)

val CourierPrime_Bold = FontFamily(
    Font(R.font.courierprime_bold, FontWeight.Normal)
)

val CourierPrime_Italic = FontFamily(
    Font(R.font.courierprime_italic, FontWeight.Normal)
)

val CourierPrime_BoldItalic = FontFamily(
    Font(R.font.courierprime_bolditalic, FontWeight.Normal)
)

val PlayfairDisplay_Bold = FontFamily(
    Font(R.font.playfairdisplay_bold, FontWeight.Normal)
)

val PlayfairDisplay_Medium = FontFamily(
    Font(R.font.playfairdisplay_medium, FontWeight.Normal)
)

val PlayfairDisplay_Italic = FontFamily(
    Font(R.font.playfairdisplay_italic, FontWeight.Normal)
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

val FilmTypography = Typography(
    titleMedium = TextStyle(
        fontFamily = PlayfairDisplay_Bold,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        letterSpacing = 1.sp
    ),
    titleSmall = TextStyle(
        fontFamily = PlayfairDisplay_Italic,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = PlayfairDisplay_Medium,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontFamily = CourierPrime_Bold,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)