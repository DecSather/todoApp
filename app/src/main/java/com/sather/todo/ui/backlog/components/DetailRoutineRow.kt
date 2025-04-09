package com.sather.todo.ui.backlog.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import com.sather.todo.R
import com.sather.todo.ui.components.*

/*
* 全信息展示的Routine
*   Single Backlog用
*/
//Routine列-修改
@Composable
fun DetailRoutineRow(
    modifier: Modifier = Modifier,
    id:Long,
    content:String,
    subcontent:String,
    isFinished:Boolean,
    credit:Float,
    colorIndex:Int,
    onFinishedChange:()->Unit = {},
    swipeToDelete:() ->Unit = {},
    icon: @Composable () ->Unit = {
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            modifier = Modifier
                .padding(end = basePadding)
                .size(iconSize)
        )
    },
) {
    val color= RoutineColors[colorIndex]
    var finished by remember { mutableStateOf(isFinished) }
    val customColors = CheckboxDefaults.colors(
        checkedColor = MaterialTheme.colorScheme.primary, // 选中时的颜色
    )
    Column {
        Row(
            modifier = modifier
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(horizontal = basePadding)
                .height(LargeHeight),
//                .swipeToDismiss(swipeToDelete),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val typography = MaterialTheme.typography
            RowIndicator(
                color = color,
                modifier = Modifier
            )
            Spacer(Modifier.width(basePadding / 2))
            Checkbox(
                colors = customColors,
                checked = finished,
                onCheckedChange = {
                    onFinishedChange()
                }
            )
            Column(Modifier) {
                if (finished) {
                    Text(
                        text = content,
                        style = typography.bodyMedium,
                        textDecoration = TextDecoration.LineThrough,
                    )
                } else {
                    if (content.isNotBlank())
                        Text(text = content, style = typography.bodyMedium)
                    else
                        Text(text = stringResource(R.string.routine_empty_error), style = typography.bodyMedium)
                    if (subcontent.isNotEmpty())
                        Text(text = subcontent, style = typography.bodySmall)
                    
                }
            }
            Spacer(Modifier.weight(1f))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.dollarSign),
                    style = typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(
                    text = credit.toString(),
                    style = typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Spacer(Modifier.width(basePadding))
            icon()
        }
        BaseDivider()
    }
}