package com.sather.todo.ui.diary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.sather.todo.ui.components.CheckBoxSize
import com.sather.todo.ui.components.DiaryRowHeight
import com.sather.todo.ui.components.basePadding


@Composable
fun BlueDotPlaceholder(
    modifier: Modifier = Modifier,
){
    
    val typography = MaterialTheme.typography
    val colorScheme  = MaterialTheme.colorScheme
    Box(
        Modifier
            .fillMaxWidth()
            .height(DiaryRowHeight / 2),
        Alignment.Center
    ) {
        Box(
            modifier
                .size(CheckBoxSize) // 设置圆的大小
                .clip(CircleShape)
                //                设置颜色改展开列动画或弹出卡片
                .background(color = colorScheme.primary)
        )
        
    }
    Spacer(Modifier.height(basePadding))
}