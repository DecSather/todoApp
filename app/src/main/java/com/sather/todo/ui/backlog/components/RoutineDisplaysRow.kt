package com.sather.todo.ui.backlog.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sather.todo.R
import com.sather.todo.ui.components.RowIndicator
import com.sather.todo.ui.components.basePadding
import com.sather.todo.ui.theme.RoutineColors

@Composable
fun RoutineDisplaysRow(
    modifier: Modifier = Modifier,
    content: String,
    colorIndex: Int = 0,
    finished:Boolean = false,
    onFinishedChange:(Boolean)->Unit = {}
) {
    val typography = MaterialTheme.typography
    val color = RoutineColors[colorIndex]
    var finished by remember { mutableStateOf(finished) }
    val customColors = CheckboxDefaults.colors(
        checkedColor = MaterialTheme.colorScheme.primary, // 选中时的颜色
    )
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RowIndicator(
            color = color,
            modifier = Modifier
        )
        Spacer(Modifier.width(basePadding / 2))
        Checkbox(
            colors = customColors,
            checked = finished,
            onCheckedChange = {
                finished = it
                onFinishedChange(it)
            }
        )
        Text(text = if(content.isNotBlank())content
                    else stringResource(R.string.routine_empty_error),
            style = typography.bodyMedium
        )
        Spacer(Modifier.weight(1f))
        Spacer(Modifier.width(basePadding))
    }
}