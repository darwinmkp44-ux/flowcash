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
    primary = FlowDarkPrimary,
    onPrimary = FlowDarkOnPrimary,
    primaryContainer = FlowDarkPrimaryContainer,
    onPrimaryContainer = FlowDarkOnPrimaryContainer,
    secondary = FlowDarkSecondary,
    onSecondary = FlowDarkOnSecondary,
    secondaryContainer = FlowDarkSecondaryContainer,
    onSecondaryContainer = FlowDarkOnSecondaryContainer,
    tertiary = FlowDarkTertiary,
    onTertiary = FlowDarkOnTertiary,
    tertiaryContainer = FlowDarkTertiaryContainer,
    onTertiaryContainer = FlowDarkOnTertiaryContainer,
    background = FlowDarkBackground,
    onBackground = FlowDarkOnBackground,
    surface = FlowDarkSurface,
    onSurface = FlowDarkOnSurface,
    onSurfaceVariant = FlowDarkOnSurfaceVariant,
    surfaceVariant = FlowDarkSurfaceVariant,
    surfaceDim = FlowDarkSurfaceDim,
    surfaceBright = FlowDarkSurfaceBright,
    surfaceContainerLowest = FlowDarkSurfaceContainerLowest,
    surfaceContainerLow = FlowDarkSurfaceContainerLow,
    surfaceContainer = FlowDarkSurfaceContainer,
    surfaceContainerHigh = FlowDarkSurfaceContainerHigh,
    surfaceContainerHighest = FlowDarkSurfaceContainerHighest,
    error = FlowDarkError,
    onError = FlowDarkOnError,
    errorContainer = FlowDarkErrorContainer,
    onErrorContainer = FlowDarkOnErrorContainer,
    outline = FlowDarkOutline,
    outlineVariant = FlowDarkOutlineVariant,
    inverseSurface = FlowDarkInverseSurface,
    inverseOnSurface = FlowDarkInverseOnSurface,
    inversePrimary = FlowDarkInversePrimary
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
    tertiary = FlowTertiary,
    onTertiary = FlowOnTertiary,
    tertiaryContainer = FlowTertiaryContainer,
    onTertiaryContainer = FlowOnTertiaryContainer,
    background = FlowBackground,
    onBackground = FlowOnBackground,
    surface = FlowSurface,
    onSurface = FlowOnSurface,
    onSurfaceVariant = FlowOnSurfaceVariant,
    surfaceVariant = FlowSurfaceVariant,
    surfaceDim = FlowSurfaceDim,
    surfaceBright = FlowSurfaceBright,
    surfaceContainerLowest = FlowSurfaceContainerLowest,
    surfaceContainerLow = FlowSurfaceContainerLow,
    surfaceContainer = FlowSurfaceContainer,
    surfaceContainerHigh = FlowSurfaceContainerHigh,
    surfaceContainerHighest = FlowSurfaceContainerHighest,
    error = FlowError,
    onError = FlowOnError,
    errorContainer = FlowErrorContainer,
    onErrorContainer = FlowOnErrorContainer,
    outline = FlowOutline,
    outlineVariant = FlowOutlineVariant,
    inverseSurface = FlowInverseSurface,
    inverseOnSurface = FlowInverseOnSurface,
    inversePrimary = FlowInversePrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
