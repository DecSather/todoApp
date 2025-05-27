package com.sather.todo.ui.diary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.sather.todo.ui.components.DiaryRowHeight
import com.sather.todo.ui.components.basePadding
import com.sather.todo.ui.components.borderWidth

@Composable
fun DiaryDisplaysRow(
    modifier: Modifier = Modifier,
    timeTitle:String = "",
    content:String = "",
){
    
    val typography = MaterialTheme.typography
    val colorScheme  = MaterialTheme.colorScheme
        Row (
            modifier = modifier
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
                    style = typography.headlineMedium,
                )
                Text(
                    text = content,
                    style = typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    Spacer(Modifier.height(basePadding))

}
