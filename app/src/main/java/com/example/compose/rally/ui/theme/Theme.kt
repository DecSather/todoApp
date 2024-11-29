
package com.example.compose.rally.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun ToDoTheme(content: @Composable () -> Unit) {
    if(isSystemInDarkTheme())
    MaterialTheme(colorScheme = DarkColorPalette, typography = Typography, content = content)
    else
        MaterialTheme(colorScheme= LightColorPalette ,typography = Typography, content = content)
        
}
