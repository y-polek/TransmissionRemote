package net.yupol.transmissionremote.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val lightColors = lightColors(
    primary = Color(0xFF607D8B),
    primaryVariant = Color(0xFF455A64),
    secondary = Color(0xFFFF5252),
    secondaryVariant = Color(0xFFFF8A80),
    background = Color.White,
    surface = Color.White,
    error = Color(0xFFB00020),
    onPrimary = Color.White,
    onSecondary = Color(0xFF727272),
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

private val darkColors = darkColors(
    primary = Color(0xFF1C2837),
    primaryVariant = Color(0xFF161F29),
    secondary = Color(0xFF1D88E3),
    secondaryVariant = Color(0xFF1D88E3),
    background = Color(0xFF161f29),
    surface = Color(0xFF1A2430),
    error = Color(0xFFCF6679),
    onPrimary = Color(0xFFF2F2F2),
    onSecondary = Color(0xFF71828F),
    onBackground = Color(0xFFF2F2F2),
    onSurface = Color(0xFFF2F2F2),
    onError = Color.Black
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) darkColors else lightColors,
        content = content
    )
}
