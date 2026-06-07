package com.zacariasthequimo.flowcash.ui.theme

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
    primary = FlowInversePrimary,
    onPrimary = FlowOnPrimary,
    primaryContainer = Color(0xFF1A3A5C),
    onPrimaryContainer = FlowPrimaryContainer,
    secondary = FlowSecondary,
    onSecondary = FlowOnSecondary,
    secondaryContainer = Color(0xFF2A2D38),
    onSecondaryContainer = FlowSecondaryContainer,
    background = FlowDarkBackground,
    onBackground = FlowDarkOnSurface,
    surface = FlowDarkSurface,
    onSurface = FlowDarkOnSurface,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    outline = Color(0xFF8E9099),
    outlineVariant = Color(0xFF44474E),
    surfaceContainerLowest = FlowDarkSurfaceContainerLowest,
    surfaceContainerLow = FlowDarkSurfaceContainerLow,
    surfaceContainer = FlowDarkSurfaceContainer,
    surfaceContainerHigh = FlowDarkSurfaceContainerHigh,
    surfaceContainerHighest = FlowDarkSurfaceContainerHighest
)

private val LightColorScheme = lightColorScheme(
    primary = FlowPrimary,
    onPrimary = FlowOnPrimary,
    primaryContainer = FlowPrimaryContainer,
    onPrimaryContainer = FlowOnPrimaryContainer,
    secondary = FlowSecondary,
    onSecondary = FlowOnSecondary,
    secondaryContainer = FlowSecondaryContainer,
    onSecondaryContainer = FlowOnSecondaryContainer,
    background = FlowBackground,
    onBackground = FlowOnBackground,
    surface = FlowSurface,
    onSurface = FlowOnSurface,
    error = FlowError,
    onError = FlowOnError,
    outline = FlowOutline,
    outlineVariant = FlowOutlineVariant,
    surfaceContainerLowest = FlowSurfaceContainerLowest,
    surfaceContainerLow = FlowSurfaceContainerLow,
    surfaceContainer = FlowSurfaceContainerLow, // Set as FlowSurfaceContainerLow (i.e. #f3f4f9) for matching clean design
    surfaceContainerHigh = FlowSurfaceContainerHigh,
    surfaceContainerHighest = FlowSurfaceContainerHighest
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to force our custom beautiful emerald branding consistently
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
