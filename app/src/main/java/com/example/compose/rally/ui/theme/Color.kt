
package com.example.compose.rally.ui.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

val Green500 = Color(0xFF1EB980)
val DarkBlue900 = Color(0xFF26282F)

val Blue500=Color(0xFF317AA1)
val Blue900=Color(0xFFAFD7EC)
val Blue300=Color(0xFF94B0C6)

val importColor: Color =Color(0xFF005D57)
val normalColor: Color =Color(0xFF039667)
val faverColor: Color = Color(0xFF04B97F)
val LightColorPalette= lightColors(
    
    primary = Blue500,
    surface = Blue900,
    onSurface = Color.Black,
    background = Blue300,
    onBackground = Color.Black
)
val DarkColorPalette = darkColors(
    primary = Green500,
    surface = DarkBlue900,
    onSurface = Color.White,
    background = DarkBlue900,
    onBackground = Color.White
)
