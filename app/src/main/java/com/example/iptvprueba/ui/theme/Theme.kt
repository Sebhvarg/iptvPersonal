package com.example.iptvprueba.ui.theme

import androidx.compose.runtime.Composable
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun IptvPruebaTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = TvPrimary,
        secondary = TvAccent,
        tertiary = TvPrimaryFocused,
        background = TvBackgroundDark,
        surface = TvSurfaceDark,
        surfaceVariant = TvSurfaceVariant,
        onPrimary = TvBackgroundDark,
        onSecondary = TvBackgroundDark,
        onBackground = TvTextPrimary,
        onSurface = TvTextPrimary,
        error = TvError
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}