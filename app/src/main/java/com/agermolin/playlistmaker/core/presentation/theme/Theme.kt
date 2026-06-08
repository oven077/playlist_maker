package com.agermolin.playlistmaker.core.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.agermolin.playlistmaker.R

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = colorResource(R.color.blue),
            onPrimary = Color.White,
            secondary = colorResource(R.color.white),
            onSecondary = colorResource(R.color.white),
            background = colorResource(R.color.screen_background),
            onBackground = colorResource(R.color.text_color_primary),
            surface = colorResource(R.color.screen_background),
            onSurface = colorResource(R.color.text_color_primary),
        )
    } else {
        lightColorScheme(
            primary = colorResource(R.color.blue),
            onPrimary = Color.White,
            secondary = colorResource(R.color.white),
            onSecondary = colorResource(R.color.black),
            background = colorResource(R.color.screen_background),
            onBackground = colorResource(R.color.text_color_primary),
            surface = colorResource(R.color.screen_background),
            onSurface = colorResource(R.color.text_color_primary),
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PlaylistMakerTypography,
        content = content,
    )
}
