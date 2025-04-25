package com.sather.todo.ui.diary.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sather.todo.ui.components.LargeHeight
import com.sather.todo.ui.components.basePadding
import com.sather.todo.ui.theme.ToDoTheme

@Composable
fun KeyboardAwareBottomBarWithTimeInsertion(
    onClick:() ->Unit,
) {
        // 底部功能栏
    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(LargeHeight),
        containerColor = MaterialTheme.colorScheme.background,
//            contentColor = MaterialTheme.colorScheme.background
    ) {
        // 时间插入按钮（靠左）
        Spacer(modifier = Modifier.width(basePadding))
        
        IconButton(
            onClick = onClick
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = "插入时间"
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // 保存文本（靠右）
        Text(
            text = "保存",
            style = MaterialTheme.typography.headlineMedium,
//                color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.width(basePadding))
        
    }
}

@Preview
@Composable
fun bottomTabPreview(){
    ToDoTheme {
        KeyboardAwareBottomBarWithTimeInsertion(
            {}
        )
    }
}