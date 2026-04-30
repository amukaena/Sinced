package com.sinced.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SincedDarkColorScheme = darkColorScheme(
    primary = SincedPrimary,
    onPrimary = SincedOnPrimary,
    background = SincedBackground,
    onBackground = SincedOnSurface,
    surface = SincedSurface,
    onSurface = SincedOnSurface,
    surfaceVariant = SincedSurfaceVariant,
    onSurfaceVariant = SincedOnSurfaceVariant,
    outline = SincedOutline,
    error = StatusOverdue
)

@Composable
fun SincedTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SincedDarkColorScheme,
        content = content
    )
}
