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

private val StudioDarkColorScheme = darkColorScheme(
    primary = OchreGoldPrimary,
    onPrimary = Color(0xFF261900),
    primaryContainer = OchreGoldDark,
    onPrimaryContainer = OchreGoldLight,
    secondary = StudioTealSecondary,
    onSecondary = Color(0xFF00363D),
    secondaryContainer = StudioTealDark,
    onSecondaryContainer = Color(0xFFE0F7FA),
    tertiary = CoralAccentTertiary,
    onTertiary = Color.White,
    background = StudioDarkBg,
    onBackground = StudioTextPrimary,
    surface = StudioSurfaceDark,
    onSurface = StudioTextPrimary,
    surfaceVariant = StudioSurfaceElevated,
    onSurfaceVariant = StudioTextSecondary,
    outline = StudioOutline
)

private val StudioLightColorScheme = lightColorScheme(
    primary = OchreGoldDark,
    onPrimary = Color.White,
    primaryContainer = OchreGoldLight,
    onPrimaryContainer = Color(0xFF261900),
    secondary = StudioTealDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F7FA),
    onSecondaryContainer = Color(0xFF00363D),
    tertiary = CoralAccentTertiary,
    onTertiary = Color.White,
    background = Color(0xFFF9F9FC),
    onBackground = Color(0xFF191B24),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF191B24),
    surfaceVariant = Color(0xFFEEF0F6),
    onSurfaceVariant = Color(0xFF5E6278),
    outline = Color(0xFFC0C4D6)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to artistic studio dark theme for rich contrast
    dynamicColor: Boolean = false, // Keep bespoke artist palette
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> StudioDarkColorScheme
        else -> StudioLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
