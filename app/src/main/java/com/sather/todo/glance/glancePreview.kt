package com.sather.todo.glance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sather.todo.ui.theme.ToDoTheme

@Preview(showSystemUi = true)
@Composable
fun glancePreview(){
    ToDoTheme {
        Column {
//            Row(Modifier.fillMaxWidth()) { Text("hello") }
            Spacer(Modifier.height(100.dp).padding(8.dp).background(MaterialTheme.colorScheme.onPrimary))
        }
    }
}