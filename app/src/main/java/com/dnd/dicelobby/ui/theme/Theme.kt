package com.dnd.dicelobby.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Dark-first theme — the app is designed primarily for dark mode
private val DarkColorScheme = darkColorScheme(
    primary          = Purple80,
    secondary        = PurpleGrey80,
    tertiary         = Pink80,
    background       = DarkBackground,
    surface          = SurfaceVariant,
    onPrimary        = DarkBackground,
    onSecondary      = DarkBackground,
    onBackground     = TextPrimary,
    onSurface        = TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary   = Purple40,
    secondary = PurpleGrey40,
    tertiary  = Pink40
)

@Composable
fun DnDDiceTogetherTheme(
    darkTheme: Boolean = true,   // default dark — can be toggled via settings
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}
