package com.example.atunes.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val VinylRedDarkColorScheme = darkColorScheme(
    primary          = AccentRed,
    onPrimary        = Color.White,
    primaryContainer = AccentRedDark,
    secondary        = TextSecondary,
    onSecondary      = Color.White,
    background       = BackgroundPrimary,
    onBackground     = TextPrimary,
    surface          = CardSurface,
    onSurface        = TextPrimary,
    surfaceVariant   = BackgroundSecondary,
    onSurfaceVariant = TextSecondary,
    outline          = Divider,
    error            = AccentRed
)

private val VinylRedLightColorScheme = lightColorScheme(
    primary          = AccentRed,
    onPrimary        = Color.White,
    primaryContainer = Color(0xFFFFDAD5),
    secondary        = LightTextSecondary,
    onSecondary      = Color.White,
    background       = LightBackground,
    onBackground     = LightTextPrimary,
    surface          = LightSurface,
    onSurface        = LightTextPrimary,
    surfaceVariant   = LightCardSurface,
    onSurfaceVariant = LightTextSecondary,
    outline          = LightDivider,
    error            = AccentRed
)

@Composable
fun ATunesTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) VinylRedDarkColorScheme else VinylRedLightColorScheme
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = if (darkTheme) BackgroundPrimary else LightBackground,
            darkIcons = !darkTheme
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = VinylTypography,
        content     = content
    )
}