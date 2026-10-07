package cl.aiep.organicsapp.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val OrganicsLightColors = lightColorScheme(
    primary = ForestGreen,
    onPrimary = White,
    primaryContainer = AquaGreenSoft,
    onPrimaryContainer = ForestGreenDark,
    secondary = AquaGreen,
    onSecondary = White,
    secondaryContainer = AquaGreenPale,
    onSecondaryContainer = ForestGreenDark,
    background = AppBackground,
    onBackground = TextPrimary,
    surface = AppSurface,
    onSurface = TextPrimary,
    surfaceVariant = AppSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = AppBorder,
    tertiary = WarmAccent,
    onTertiary = White,
    error = ErrorRed,
    onError = White,
    errorContainer = ErrorContainer,
    onErrorContainer = ErrorRed
)

@Composable
fun OrganicsAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = OrganicsLightColors,
        typography = OrganicsAppTypography,
        shapes = OrganicsAppShapes,
        content = content
    )
}
