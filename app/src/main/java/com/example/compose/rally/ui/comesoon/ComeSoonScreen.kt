package com.example.compose.rally.ui.comesoon

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddHome
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.rally.ui.navigation.BaseDestination
import com.example.compose.rally.ui.theme.ToDoTheme

data object ComeSoon : BaseDestination {
    override val icon = Icons.Filled.AddHome
    override val route = "comeSoon"
}
@Composable
fun ComeSoonScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = "Coming soon",
            style = MaterialTheme.typography.h2,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.primary
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = "waiting implement",
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.surface
        )
    }
}
@Preview
@Composable
fun ComeSoonScreenPreview(){
    ToDoTheme { ComeSoonScreen() }
    
}