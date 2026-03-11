package com.example.nxtdrp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Default Light
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

// Dark
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0)
)

// Mint
private val MintColorScheme = lightColorScheme(
    primary = Color(0xFF2E7D6F),
    onPrimary = Color.White,
    secondary = Color(0xFF4DB6AC),
    onSecondary = Color.White,
    tertiary = Color(0xFF80CBC4),
    onTertiary = Color.Black,
    background = Color(0xFFE0F2F1),
    surface = Color(0xFFF1F9F8),
    onBackground = Color(0xFF1B2B29),
    onSurface = Color(0xFF1B2B29),
    surfaceVariant = Color(0xFFCCE8E4),
    onSurfaceVariant = Color(0xFF3A5551)
)

// High Contrast
private val HighContrastColorScheme = lightColorScheme(
    primary = Color(0xFF000000),
    onPrimary = Color(0xFFFFFF00),
    secondary = Color(0xFF000000),
    onSecondary = Color(0xFFFFFF00),
    tertiary = Color(0xFF000000),
    onTertiary = Color(0xFFFFFF00),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF000000),
    onSurface = Color(0xFF000000),
    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFF000000)
)

@Composable
fun NXTDRPTheme(
    themeName: String = "Default Light",
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeName) {
        "Dark" -> DarkColorScheme
        "Mint" -> MintColorScheme
        "High Contrast" -> HighContrastColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}