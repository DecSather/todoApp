package com.sather.todo.ui.diary.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sather.todo.ui.components.spacePadding

@Composable
fun BaseTimeText(
    text:String,
    selected:Boolean,
    onTextSelected:(String)->Unit,
){
    Text(
        text = text,
        modifier = Modifier
            .clickable {
                onTextSelected(text)
            }
            .padding(horizontal =  spacePadding),
        style = if (selected) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.titleLarge,
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    )

}

@Composable
fun TimeStringSelectionRow(
    selectedText:String,
    selectedList:List<Int>,
    selectedAction:(String)-> Unit,
){
    var selectedYear by remember { mutableStateOf(selectedText) }
    
        Row(
            Modifier
                .horizontalScroll(rememberScrollState())
                .selectableGroup(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            selectedList.map {it->
                val text =
                    if(it >= 10) it.toString()
                    else String.format("%02d",it)
                BaseTimeText(
                    text = text,
                    selected = (text == selectedYear),
                    onTextSelected = {
                        selectedYear = it
                        selectedAction(it)
                    }
                )
            }
        }
}
enum class DiaryTabMode {DEFAULT,SELECT_YEAR,SELECT_MOUTH,EDIT}