package com.sather.todo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


//列竖线
@Composable
fun RowIndicator(color: Color, modifier: Modifier = Modifier) {
    Spacer(
        modifier.size(4.dp, 36.dp)
            .background(color = color)
    )
}
/*
* 添加信息展示的Routine
*   Backlog Edit Card用
*/

