package com.sather.todo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


//进度线
@Composable
fun BaseDivider(total: Float, data: List<Float>, colors: List<Color>) {
    var index=0
    if(total>0.0) {
        Row(Modifier.fillMaxWidth()) {
            data.forEach { item ->
                if(item>0.0){
                    Spacer(
                        modifier = Modifier
                            .weight(item/total)
                            .height(1.dp)
                            .background(colors.get(index++))
                    )
                    
                }
            }
        }
    }else{
        Row(Modifier.fillMaxWidth()) {
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.background)
            )
        }
    }
}