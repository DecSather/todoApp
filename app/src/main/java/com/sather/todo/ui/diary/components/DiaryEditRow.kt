package com.sather.todo.ui.diary.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sather.todo.ui.components.basePadding
import com.sather.todo.ui.theme.ToDoTheme

@Composable
fun DiaryEditRow(
    modifier: Modifier = Modifier,
    timeTitle:String,
    content:String = "",
){
    if(content.isNotBlank()) {
        OutlinedTextField(
            modifier = modifier.fillMaxWidth().padding(horizontal = basePadding),
            value = content,
            onValueChange = {},
            label = {
                Text(
                    text = timeTitle,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        )
        Spacer(Modifier.height(basePadding))
    }
}

@Preview
@Composable
fun DiaryEditRowPreview(){
    ToDoTheme {
        DiaryEditRow(
            timeTitle = "2025-01-01",
            content = "123he\nllo世界"
        
        )
    }
}