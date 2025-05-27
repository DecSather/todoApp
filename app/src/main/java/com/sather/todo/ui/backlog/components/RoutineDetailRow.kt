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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sather.todo.R
import com.sather.todo.ui.components.BaseDivider
import com.sather.todo.ui.components.LargeHeight
import com.sather.todo.ui.components.RowIndicator
import com.sather.todo.ui.components.basePadding
/*
* 全信息展示的Routine
*   Single Backlog用
*/
@Composable
fun DetailRoutineRow(
    modifier: Modifier = Modifier,
    content:String,
    subcontent:String,
    isFinished:Boolean,
    colorIndex:Int,
    onFinishedChange:()->Unit = {},
    icon: @Composable () ->Unit = {
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            modifier = Modifier
                .padding(end = 12.dp)
                .size(24.dp)
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            val typography = MaterialTheme.typography
            RowIndicator(
                color = color,
                modifier = Modifier
            )
            Spacer(Modifier.width(12.dp))
            Checkbox(
                colors = customColors,
                checked = finished,
                onCheckedChange = {
                    onFinishedChange()
                }
            )
            Column(Modifier.weight(1f)) {
                if (finished) {
                    Text(
                        text = content,
                        style = typography.bodyMedium,
                        textDecoration = TextDecoration.LineThrough,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    if (content.isNotBlank())
                        Text(
                            text = content,
                            style = typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    else
                        Text(text = stringResource(R.string.routine_empty_error), style = typography.bodyMedium)
                    if (subcontent.isNotEmpty())
                        Text(text = subcontent, style = typography.bodySmall)
                    
                }
            }
            icon()
        }
        BaseDivider()
    }
}