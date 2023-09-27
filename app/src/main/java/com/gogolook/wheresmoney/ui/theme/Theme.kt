package com.gogolook.wheresmoney.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

private val defaultColors = WMColors(
    primary = Color(0xFF68AA7E),
    secondary = Color(0x3368AA7E),
    dangerous = Color(0xFFDE6E6E),
    warning = Color(0xFFE48900),
    safe = Color(0xFF73B95B),
    mark = Color(0xFFFF976A),
    reddot = Color(0xFFFF004F),
    background = Color(0xFFF8F8F8),
    mask = Color(0x19000000),
    surfacePrimary = Color(0xFFFBFDF8),
    surfaceSecondary = Color(0x07000000),
    surfaceTertiary = Color(0xFFF4F4F4),
    surfaceVariant = Color(0xFFDCE5DB),
    onSurfaceBlack = Color(0xFF000000),
    onSurfacePrimary = Color(0xB2000000),
    onSurfaceSecondary = Color(0x7F000000),
    onSurfaceTertiary = Color(0x4C000000),
    onSurfaceVariant = Color(0xFF414942),
    onSurfaceQuaternary = Color(0x33000000),
    onSurfaceLimited = Color(0x19000000),
    onSurfaceLimitedSecondary = Color(0x0C000000),
    onSurfaceHighlightPrimary = Color(0xFFFFFFFF),
    onSurfaceHighlightSecondary = Color(0xB2FFFFFF),
    onSurfaceHighlightTertiary = Color(0x7FFFFFFF),
    isLight = true,
    red = Color(0xFFf43f5e),
    green = Color(0xFF10b981),
    blue = Color(0xFF0ea5e9)
)

val LocalColors = compositionLocalOf { defaultColors }

private val defaultShapes = WMShapes()

val LocalShapes = compositionLocalOf { defaultShapes }

private val defaultTypography = WMTypography()

val LocalTypography = compositionLocalOf { defaultTypography }

val defaultRippleTheme = object : RippleTheme {
    @Composable
    override fun defaultColor(): Color {
        return LocalColors.current.rippleColor
    }

    @Composable
    override fun rippleAlpha(): RippleAlpha {
        return LocalColors.current.rippleColor.alpha.let {
            RippleAlpha(it, it, it, it)
        }
    }
}

@Composable
fun WheresMoneyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    val colors = defaultColors
    val shapes = defaultShapes
    val typography = defaultTypography
    val rippleTheme = defaultRippleTheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography.materialTypography,
        shapes = shapes.materialShapes,
    ) {
        CompositionLocalProvider(
            LocalColors provides colors,
            LocalShapes provides shapes,
            LocalTypography provides typography,
            LocalRippleTheme provides rippleTheme,
            content = content
        )
    }
}