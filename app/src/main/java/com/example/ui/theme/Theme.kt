package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryPurpleLight,
    onPrimary = OnPrimaryContainerPurple,
    primaryContainer = PrimaryPurpleDark,
    onPrimaryContainer = PrimaryPurpleLight,
    secondary = SecondarySlate,
    onSecondary = Color.White,
    secondaryContainer = PolishSurfaceVariantDark,
    onSecondaryContainer = PolishTextPrimaryDark,
    tertiary = TertiaryRose,
    background = PolishBackgroundDark,
    surface = PolishSurfaceDark,
    surfaceVariant = PolishSurfaceVariantDark,
    onBackground = PolishTextPrimaryDark,
    onSurface = PolishTextPrimaryDark,
    onSurfaceVariant = PolishTextSecondaryDark,
    outline = PolishOutline,
    outlineVariant = PolishOutlineVariant
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryPurple,
    onPrimary = OnPrimaryPurple,
    primaryContainer = PrimaryContainerPurple,
    onPrimaryContainer = OnPrimaryContainerPurple,
    secondary = SecondarySlate,
    onSecondary = Color.White,
    secondaryContainer = SecondaryContainerSlate,
    onSecondaryContainer = OnSecondaryContainerSlate,
    tertiary = TertiaryRose,
    background = PolishBackground,
    surface = PolishCardWhite,
    surfaceVariant = PolishSurfaceVariant,
    onBackground = PolishTextPrimary,
    onSurface = PolishTextPrimary,
    onSurfaceVariant = PolishTextSecondary,
    outline = PolishOutline,
    outlineVariant = PolishOutlineVariant
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
