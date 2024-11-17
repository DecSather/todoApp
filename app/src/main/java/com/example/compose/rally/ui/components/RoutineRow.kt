package com.example.compose.rally.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.compose.rally.data.Routine
import com.example.compose.rally.ui.theme.unfinishedColor


//列竖线
@Composable
fun RowIndicator(color: Color, modifier: Modifier = Modifier) {
    Spacer(
        modifier
            .size(4.dp, 36.dp)
            .background(color = color)
    )
}
/*
* 添加信息展示的Routine
*   Backlog Edit Card用
*/




/*
* 简单信息展示的Routine
*   主页用
*/
@Composable
fun BriefRoutineRow(
    modifier: Modifier = Modifier,
    routine: Routine,
    onFinishedChange:(Int,Boolean)->Unit
) {
    val content=routine.content
    val credit=routine.credit
    val color=RoutineColors[routine.rank]
    val id=routine.id
    var finished by remember { mutableStateOf(routine.finished) }
    val dollarSign ="$ "
    val customColors = CheckboxDefaults.colors(
        checkedColor = MaterialTheme.colors.primary, // 选中时的颜色
    )
    Row(
        modifier = modifier
            .height(68.dp),
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
                onFinishedChange(id,it)
            }
        )
        Column{
            Text(text = content, style = typography.body1)
        }
        Spacer(Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = dollarSign,
                style = typography.h6,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                text = credit.toString(),
                style = typography.h6,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        Spacer(Modifier.width(16.dp))
    }
}
@Composable
fun BriefEmptyRow(
    modifier: Modifier = Modifier,
    content: String,
) {
    val customColors = CheckboxDefaults.colors(
        checkedColor = MaterialTheme.colors.primary, // 选中时的颜色
    )
    Row(
        modifier = modifier
            .height(68.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val typography = MaterialTheme.typography
        RowIndicator(
            color = unfinishedColor,
            modifier = Modifier
        )
        Spacer(Modifier.width(12.dp))
        Checkbox(
            colors = customColors,
            checked = false,
            onCheckedChange = {}
        )
        Column{
            Text(text = content, style = typography.body1)
        }
        Spacer(Modifier.weight(1f))
        
        Spacer(Modifier.width(16.dp))
    }
}

/*
* 全信息展示的Routine
*   Single Backlog用
*/
//Routine列-修改
@Composable
fun DetailRoutineRow(
    modifier: Modifier = Modifier,
    routine:Routine,
    onFinishedChange:(Int,Boolean)->Unit
) {
    val content=routine.content
    val subcontent=routine.subcontent
    val credit=routine.credit
    val color=RoutineColors[routine.rank]
    val id=routine.id
    var finished by remember { mutableStateOf(routine.finished) }
    val dollarSign ="$ "
    val customColors = CheckboxDefaults.colors(
        checkedColor =MaterialTheme.colors.primary, // 选中时的颜色
    )
    Row(
        modifier = modifier
            .height(68.dp)
            .clearAndSetSemantics {
                contentDescription =
                    "$content account ending in ${subcontent.takeLast(4)}, current balance $dollarSign$credit"
            },
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
                onFinishedChange(id,it)
            }
        )
        Column(Modifier) {
            Text(text = content, style = typography.body1)
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(text = subcontent, style = typography.subtitle1)
            }
        }
        Spacer(Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = dollarSign,
                style = typography.h6,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                text = credit.toString(),
                style = typography.h6,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        Spacer(Modifier.width(16.dp))
        
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(24.dp)
            )
        }
    }
    RallyDivider()
}
//Routine列-新增
@Composable
fun DetailEmptyRow(
    modifier: Modifier = Modifier,
    content: String,
    subcontent:String,
) {
    val dollarSign ="$ "
    Row(
        modifier = modifier
            .height(68.dp)
            .clearAndSetSemantics {
                contentDescription =
                    "$content account ending in ${subcontent.takeLast(4)}, current balance $dollarSign"
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val typography = MaterialTheme.typography
        Spacer(Modifier.width(12.dp))
        Column(Modifier) {
            Text(text = content, style = typography.body1)
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(text = subcontent, style = typography.subtitle1)
            }
        }
        Spacer(Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = dollarSign,
                style = typography.h6,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                text = "0.0",
                style = typography.h6,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        Spacer(Modifier.width(16.dp))
        
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(24.dp)
            )
        }
    }
    RallyDivider()
}
@Composable
fun RallyDivider(modifier: Modifier = Modifier) {
    Divider(color = MaterialTheme.colors.background, thickness = 1.dp, modifier = modifier)
}
