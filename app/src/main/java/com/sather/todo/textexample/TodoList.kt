package com.sather.todo.textexample

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessAlarm
import androidx.compose.material.icons.rounded.DataSaverOn
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.glance.layout.Column

@Preview
@Composable
fun greeting(){
    Column {
        Icon(Icons.Rounded.Image, "Drag Handle")
        Icon(Icons.Rounded.AccessAlarm, "Drag Handle")
        Icon(Icons.Rounded.DataSaverOn, "Drag Handle")
    }
    
}