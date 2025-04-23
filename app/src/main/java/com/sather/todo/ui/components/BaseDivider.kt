package com.sather.todo.ui.components

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


//进度线
@Composable
fun BaseDivider(
    modifier: Modifier = Modifier,
    color:Color =MaterialTheme.colorScheme.background
) {
    HorizontalDivider(color = color, thickness = 1.dp, modifier = modifier)
}