
package com.example.compose.rally.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * A [MaterialTheme] for Rally.
 */
@Composable
fun RallyTheme(content: @Composable () -> Unit) {
    if(isSystemInDarkTheme())
    MaterialTheme(colors = DarkColorPalette, typography = Typography, content = content)
    else
        MaterialTheme(typography = Typography, content = content)
        
}

/**
 * A theme overlay used for dialogs.
 */
@Composable
fun RallyDialogThemeOverlay(content: @Composable () -> Unit) {
    // Rally is always dark themed.
    val darkDialogColors = darkColors(
        primary = Color.White,
        surface = Color.White.copy(alpha = 0.12f).compositeOver(Color.Black),
        onSurface = Color.White
    )
    
    val lightDialogColors = lightColors(
        primary = Color.White,
        surface = Color.White.copy(alpha = 0.12f).compositeOver(Color.Black),
        onSurface = Color.White
    )
    
    // Copy the current [Typography] and replace some text styles for this theme.
    val currentTypography = MaterialTheme.typography
    val dialogTypography = currentTypography.copy(
        body2 = currentTypography.body1.copy(
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            letterSpacing = 1.sp
        ),
        button = currentTypography.button.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.2.em
        )
    )
    if(isSystemInDarkTheme())
        MaterialTheme(colors = darkDialogColors, typography = dialogTypography, content = content)
    else
    MaterialTheme(colors = lightDialogColors, typography = dialogTypography, content = content)
}
