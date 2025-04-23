package com.sather.todo.ui.diary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sather.todo.ui.components.CheckBoxSize
import com.sather.todo.ui.components.basePadding
import com.sather.todo.ui.components.borderWidth
import com.sather.todo.ui.theme.ToDoTheme

@Composable
fun DiaryDisplaysRow(content:String){
    
    val typography = MaterialTheme.typography
    val colorScheme  = MaterialTheme.colorScheme
    
    if(content.isNotBlank()) {
        Row (
            modifier = Modifier
                .padding(horizontal = basePadding)
                .fillMaxWidth()
                .height(90.dp)
                .background(colorScheme.secondaryContainer)
                .border(borderWidth, colorScheme.onBackground)
            
        ) {
            Column(
                Modifier.padding(basePadding)
            ) {
                Text(
                    text = content,
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
        Box(Modifier.fillMaxWidth().height(45.dp),
            Alignment.Center
            ) {
            Box(Modifier.size(CheckBoxSize) // 设置圆的大小
                .clip(CircleShape)
//                设置颜色改展开列动画或弹出卡片
                .background(color = colorScheme.primary)
            
            )
            
        }
    }

}

@Preview
@Composable
fun DiaryDisplaysRowPreview(){
    ToDoTheme {
        DiaryDisplaysRow("")
    }
}