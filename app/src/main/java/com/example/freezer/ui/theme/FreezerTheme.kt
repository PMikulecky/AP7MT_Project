package com.example.freezer.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily

// Define a basic color scheme
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF40009C),
    primaryContainer = Color(0xFFBBA8D8),
    secondary = Color(0xFF03DAC5),
    background = Color(0xFFE4E2E7),
    surface = Color(0xFFEDEBF0),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    // Include other color definitions as needed
)

private val DarkColorScheme = lightColorScheme(
    primary = Color(0xFF1F004B),
    primaryContainer = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC5),
    background = Color(0xFF2E2C31),
    surface = Color(0xFF4E4E4E),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    // Include other color definitions as needed
)

// Define a typography scheme (customize as needed)
private val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    )
    // Include other text styles as needed
)

@Composable
fun FreezerTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        // Define Dark Color Scheme if needed
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}
