package com.sudoku.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class AppTheme(val label: String) {
    SYSTEM("Auto"),
    LIGHT("Light"),
    DARK("Dark"),
    HIGH_CONTRAST("AMOLED"),
    VIVID("Vivid"),
    COSMIC("Cosmic"),
    CUSTOM("Custom")
}

object ThemeState {
    var theme by mutableStateOf(AppTheme.SYSTEM)
    var customPrimary by mutableStateOf(Color(0xFF6200EE))
    var customOnPrimary by mutableStateOf(Color(0xFFFFFFFF))
    var customSecondary by mutableStateOf(Color(0xFF03DAC6))
    var customOnSecondary by mutableStateOf(Color(0xFF000000))
    var customBackground by mutableStateOf(Color(0xFF121212))
    var customOnBackground by mutableStateOf(Color(0xFFFFFFFF))
    var customSurface by mutableStateOf(Color(0xFF121212))
    var customOnSurface by mutableStateOf(Color(0xFFFFFFFF))
    var customSurfaceVariant by mutableStateOf(Color(0xFF1E1E1E))
    var customOnSurfaceVariant by mutableStateOf(Color(0xFFEEEEEE))
    var customOutline by mutableStateOf(Color(0xFFBBBBBB))
    
    // Grid specific
    var customGridLine by mutableStateOf(Color(0xFF000000))
    var customGridThinLine by mutableStateOf(Color(0xFF999999))
    var customCellNormal by mutableStateOf(Color(0xFFFFFFFF))
    var customCellAlternate by mutableStateOf(Color(0xFFF5F5F5))
    var customCellSelected by mutableStateOf(Color(0xFFBBDEFB))
    var customCellRelated by mutableStateOf(Color(0xFFE3F2FD))
    var customCellSameNum by mutableStateOf(Color(0xFFC5CAE9))
    var customGivenNumber by mutableStateOf(Color(0xFF000000))
    var customUserNumber by mutableStateOf(Color(0xFF3F51B5))
    var customNoteColor by mutableStateOf(Color(0xFF757575))
    var customErrorNumber by mutableStateOf(Color(0xFFB00020))
}

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF9DBAFF),
    onPrimary = Color(0xFF002870),
    primaryContainer = Color(0xFF284090),
    onPrimaryContainer = Color(0xFFD8E2FF),
    secondary = PurpleGrey80,
    background = Color(0xFF111120),
    onBackground = Color(0xFFE8E8FF),
    surface = Color(0xFF1C1C2E),
    onSurface = Color(0xFFE8E8FF),
    surfaceVariant = Color(0xFF252540),
    onSurfaceVariant = Color(0xFFBBBBDD),
    outline = Color(0xFF666688)
)

private val VividColorScheme = lightColorScheme(
    primary = Color(0xFF7C3AED),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = Color(0xFF4C1D95),
    secondary = Color(0xFF0EA5E9),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0C4A6E),
    tertiary = Color(0xFFF43F5E),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFE4E6),
    onTertiaryContainer = Color(0xFF881337),
    background = Color(0xFFFAF5FF),
    onBackground = Color(0xFF1E1B4B),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1E1B4B),
    surfaceVariant = Color(0xFFF3E8FF),
    onSurfaceVariant = Color(0xFF6D28D9),
    outline = Color(0xFFA78BFA),
    error = Color(0xFFE11D48)
)

private val CosmicColorScheme = darkColorScheme(
    primary = Color(0xFFA78BFA),
    onPrimary = Color(0xFF1E0060),
    primaryContainer = Color(0xFF2D1B69),
    onPrimaryContainer = Color(0xFFD4BBFF),
    secondary = Color(0xFF34D399),
    onSecondary = Color(0xFF003823),
    secondaryContainer = Color(0xFF005236),
    onSecondaryContainer = Color(0xFFA7F3D0),
    tertiary = Color(0xFFFB923C),
    onTertiary = Color(0xFF431500),
    tertiaryContainer = Color(0xFF5A2100),
    onTertiaryContainer = Color(0xFFFFDBC4),
    background = Color(0xFF07080F),
    onBackground = Color(0xFFEDE9FE),
    surface = Color(0xFF0F1021),
    onSurface = Color(0xFFEDE9FE),
    surfaceVariant = Color(0xFF1A1B30),
    onSurfaceVariant = Color(0xFFC4B5FD),
    outline = Color(0xFF4C3D8F),
    error = Color(0xFFFF6B8A)
)

private val HighContrastColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF222222),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFCCCCCC),
    background = Color.Black,
    onBackground = Color.White,
    surface = Color(0xFF0D0D0D),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1A1A1A),
    onSurfaceVariant = Color(0xFFCCCCCC),
    outline = Color(0xFF666666)
)

@Composable
fun SudokuTheme(content: @Composable () -> Unit) {
    val appTheme = ThemeState.theme
    val systemDark = isSystemInDarkTheme()
    val isDark = when (appTheme) {
        AppTheme.SYSTEM -> systemDark
        AppTheme.LIGHT, AppTheme.VIVID -> false
        AppTheme.DARK, AppTheme.HIGH_CONTRAST, AppTheme.COSMIC -> true
        AppTheme.CUSTOM -> ThemeState.customBackground.luminance() < 0.5f
    }

    val colorScheme = when (appTheme) {
        AppTheme.HIGH_CONTRAST -> HighContrastColorScheme
        AppTheme.VIVID -> VividColorScheme
        AppTheme.COSMIC -> CosmicColorScheme
        AppTheme.CUSTOM -> {
            val scheme = if (isDark) darkColorScheme() else lightColorScheme()
            scheme.copy(
                primary = ThemeState.customPrimary,
                onPrimary = ThemeState.customOnPrimary,
                secondary = ThemeState.customSecondary,
                onSecondary = ThemeState.customOnSecondary,
                background = ThemeState.customBackground,
                onBackground = ThemeState.customOnBackground,
                surface = ThemeState.customSurface,
                onSurface = ThemeState.customOnSurface,
                surfaceVariant = ThemeState.customSurfaceVariant,
                onSurfaceVariant = ThemeState.customOnSurfaceVariant,
                outline = ThemeState.customOutline
            )
        }
        AppTheme.SYSTEM, AppTheme.LIGHT, AppTheme.DARK -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val ctx = LocalContext.current
                if (isDark) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
            } else {
                if (isDark) DarkColorScheme else LightColorScheme
            }
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        SideEffect {
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
