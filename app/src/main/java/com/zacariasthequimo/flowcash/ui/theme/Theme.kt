package com.zacariasthequimo.flowcash.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
