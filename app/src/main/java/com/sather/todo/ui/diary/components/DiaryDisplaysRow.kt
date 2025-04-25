package com.sather.todo.ui.diary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.sather.todo.ui.components.CheckBoxSize
import com.sather.todo.ui.components.DiaryRowHeight
import com.sather.todo.ui.components.basePadding
import com.sather.todo.ui.components.borderWidth

@Composable
fun DiaryDisplaysRow(
    modifier: Modifier = Modifier,
    timeTitle:String = "",
    content:String = "",
    onDetailClick:()->Unit,
    onNewClick:()->Unit,
){
    
    val typography = MaterialTheme.typography
    val colorScheme  = MaterialTheme.colorScheme
    
    if(content.isNotBlank()) {
        Row (
            modifier = modifier
                .clickable { onDetailClick() }
                .padding(horizontal = basePadding)
                .fillMaxWidth()
                .height(DiaryRowHeight)
                .background(colorScheme.secondaryContainer)
                .border(borderWidth, colorScheme.onBackground)
            
        ) {
            Column(
                Modifier.padding(basePadding)
            ) {
                Text(
                    text = timeTitle,
                    style = typography.bodyLarge
                )
                Text(
                    text = content,
                    style = typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
        }
    }else{
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
                    .clickable { onNewClick() }
            )
            
        }
    }
    Spacer(Modifier.height(basePadding))

}
