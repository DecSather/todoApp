package com.sather.todo.ui.diary.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.sather.todo.ui.components.basePadding

@Composable
fun DiaryEditRow(
    modifier: Modifier = Modifier,
    timeTitle:String,
    content:String,
){
    Column(
        Modifier.padding(basePadding)
    ) {
        Text(
            text = timeTitle,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis
        )
        
    }
}
