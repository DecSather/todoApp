package com.sather.todo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sather.todo.R

private val EczarFontFamily = FontFamily(
    Font(R.font.eczar_regular),
    Font(R.font.eczar_semibold, FontWeight.SemiBold)
)

val Typography = Typography(
//    h1 = TextStyle(
//        fontWeight = FontWeight.W100,
//        fontSize = 96.sp,
//    ),
//    h2
    headlineLarge= TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        fontFamily = EczarFontFamily,
        letterSpacing = 1.5.sp
    ),
//    h3 = TextStyle(
//        fontWeight = FontWeight.W400,
//        fontSize = 14.sp
//    ),
//    h4 = TextStyle(
//        fontWeight = FontWeight.W700,
//        fontSize = 34.sp
//    ),
//    h5 = TextStyle(
//        fontWeight = FontWeight.W700,
//        fontSize = 24.sp
//    ),
//    h6
    
    headlineMedium= TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        fontFamily = EczarFontFamily,
        letterSpacing = 3.sp
    ),
//    subtitle1
    titleLarge= TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        letterSpacing = 2.sp
    ),
//    subtitle2
    titleMedium=TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
//        letterSpacing = 0.1.em
    ),
//    body1
    bodyMedium= TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
//    body2
    bodySmall= TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = EczarFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 20.sp,
    )

//    button = TextStyle(
//        fontWeight = FontWeight.Bold,
//        fontSize = 14.sp,
//        lineHeight = 16.sp,
//        letterSpacing = 0.2.em
//    ),
//    caption = TextStyle(
//        fontWeight = FontWeight.W500,
//        fontSize = 12.sp
//    ),
//    overline = TextStyle(
//        fontWeight = FontWeight.W500,
//        fontSize = 10.sp
//    )
)
