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
    bodyLarge = TextStyle(
        fontFamily = CourierPrime_BoldItalic,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)