package com.sather.todo.glance

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
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
        Card(Modifier.fillMaxWidth().height(248.dp).padding(16.dp)) {
            Text("hello")
        }
    }
}