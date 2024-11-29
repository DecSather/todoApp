
package com.example.compose.rally.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val Green500 = Color(0xFF1EB980)
val DarkBlue900 = Color(0xFF26282F)
val DarkBlue300 =Color(0xFF2E3038)

val Blue300=Color(0xFFD4E5EF)
val Blue500=Color(0xFF317AA1)
val Blue900=Color(0xFFAFD7EC)

val importColor: Color =Color(0xFF005D57)
val normalColor: Color =Color(0xFF039667)
val faverColor: Color = Color(0xFF04B97F)
val unfinishedColor: Color=Color(0xFF6F847D)

val errorColor = Color(0xFF6F94CD)

val LightColorPalette= lightColorScheme(
    primary = Blue500,
    surface = Blue900,
    onSurface = Color.Black,
    background = Blue300,
    onBackground = Color.Black
)
val DarkColorPalette = darkColorScheme(
    primary = Green500,
    surface = DarkBlue900,
    onSurface = Color.White,
    background = DarkBlue300,
    onBackground = Color.White
)
