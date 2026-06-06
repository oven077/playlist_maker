package com.agermolin.playlistmaker.core.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.agermolin.playlistmaker.R

val YsDisplayMedium = FontFamily(Font(R.font.ys_display_medium, FontWeight.Medium))
val YsDisplayRegular = FontFamily(Font(R.font.ys_display_regular, FontWeight.Normal))
val YsTextRegular = FontFamily(Font(R.font.ys_text_regular, FontWeight.Normal))

val PlaylistMakerTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = YsDisplayMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = YsDisplayMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 19.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = YsDisplayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = YsDisplayMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = YsTextRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
    ),
)
