package com.moderntv.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api

// Netflix TV Dark + Glassmorphism
object ModernTvColors {
    val Background = Color(0xFF0A0A0B)
    val Surface = Color(0xFF141414)
    val SurfaceElevated = Color(0xFF1F1F1F)
    val SurfaceGlass = Color(0x1FFFFFFF) // 12% branco para glass
    val GlassBorder = Color(0x1EFFFFFF) // borda iluminada suave
    val Primary = Color(0xFFE50914) // Netflix Red
    val PrimaryGlow = Color(0x66E50914) // glow ao focar
    val OnBackground = Color(0xFFFFFFFF)
    val OnSurface = Color(0xFFE5E5E5)
    val OnSurfaceVariant = Color(0xFF9E9E9E)
    val FocusBorder = Color(0xFFFFFFFF)
    val FocusGlow = Color(0x33FFFFFF)
    val LiveRed = Color(0xFFEB3349)
    val EpgNow = Color(0xFF00E676)
}

private val DarkColorScheme = darkColorScheme(
    primary = ModernTvColors.Primary,
    onPrimary = Color.White,
    background = ModernTvColors.Background,
    onBackground = ModernTvColors.OnBackground,
    surface = ModernTvColors.Surface,
    onSurface = ModernTvColors.OnSurface,
    surfaceVariant = ModernTvColors.SurfaceElevated,
    outline = ModernTvColors.GlassBorder,
    error = Color(0xFFFF3B30)
)

private val AppTypography = Typography(
    headlineLarge = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White),
    headlineMedium = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = Color.White),
    titleLarge = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, color = ModernTvColors.OnSurface),
    bodyLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, color = ModernTvColors.OnSurface),
    bodyMedium = TextStyle(fontSize = 13.sp, color = ModernTvColors.OnSurfaceVariant),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
)

val CardShape = RoundedCornerShape(12.dp)
val CardShapeFocused = RoundedCornerShape(14.dp)
val InputShape = RoundedCornerShape(10.dp)

@Composable
fun ModernTvTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        content = content
    )
}

// Helpers para foco TV
object FocusDefaults {
    val scaleFocused = 1.08f
    val scalePressed = 0.98f
    val glowPadding = 8.dp
    val borderWidthFocused = 2.dp
    val borderWidthIdle = 1.dp
}
