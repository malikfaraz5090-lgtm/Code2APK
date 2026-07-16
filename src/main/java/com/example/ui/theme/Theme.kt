package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val Code2ApkDarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = OnBackgroundDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark
)

// Since Code2APK Studio is a code editor styled strictly like VS Code / Spck,
// we will use the dark theme by default across the entire app for that premium developer aesthetic.
private val Code2ApkLightColorScheme = lightColorScheme(
    primary = VsCodeAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC1F8C9),
    secondary = Color(0xFF43684B),
    background = Color(0xFFF9F9F9),
    surface = Color(0xFFF1F1F1),
    onBackground = Color(0xFF1E1E1E),
    onSurface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFFE5E5E5),
    onSurfaceVariant = Color(0xFF444444)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme for the premium IDE feel
    dynamicColor: Boolean = false, // Disable dynamic colors so our brand #00C853 green stands out
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) Code2ApkDarkColorScheme else Code2ApkLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
