package com.sather.todo.ui.components.backlogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sather.todo.R
import com.sather.todo.data.Routine
import com.sather.todo.ui.components.RoutineColors
import com.sather.todo.ui.components.RowIndicator
import com.sather.todo.ui.components.basePadding

@Composable
fun TextDisplaysRow(
    modifier: Modifier = Modifier,
    content: String,
    credit: String = "",
    colorIndex: Int = 0,
    finished:Boolean = false,
    onFinishedChange:(Boolean)->Unit = {}
) {
    val color = RoutineColors[colorIndex]
    var finished by remember { mutableStateOf(finished) }
    val customColors = CheckboxDefaults.colors(
        checkedColor = MaterialTheme.colorScheme.primary, // 选中时的颜色
    )
    Row(
        modifier = modifier,
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
                finished = it
                onFinishedChange(it)
            }
        )
        Column{
            Text(text = content, style = typography.bodyMedium)
        }
        Spacer(Modifier.weight(1f))
        if(credit.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.dollarSign),
                    style = typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(
                    text = credit,
                    style = typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
        Spacer(Modifier.width(basePadding))
    }
}