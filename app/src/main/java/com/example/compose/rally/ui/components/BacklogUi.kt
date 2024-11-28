package com.example.compose.rally.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.rally.R
import com.example.compose.rally.data.Backlog
import com.example.compose.rally.data.Routine
import com.example.compose.rally.ui.theme.*
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material3.*
import androidx.compose.material3.IconButton


/*
* Common Ui
* -Backlog Detail Card
* -进度线
* -竖线色条
*/

/*
* Backlog Detail Card
    -主页用
 */

//backlog卡片-可改finished
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BacklogDetailCard(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    
    backlog: Backlog,
    routineList:List<Routine>,
    
    onFinishedChange:(Int,Boolean)->Unit,
    onBacklogDetailClick: (Int) -> Unit,
    
    onBacklogEditClick:(Backlog,Routine,Int) -> Unit
    
) {
    val creditTotal:Float =routineList.map { it.credit }.sum()
    var expanded by rememberSaveable { mutableStateOf(false) }
    
    Card {
        Column {
            Column(Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .animateContentSize()
            
            ) {
                with(sharedTransitionScope) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = backlog.timeTitle,
                            style = MaterialTheme.typography.h2,
                            modifier = Modifier
                                .weight(1f)
                                .sharedBounds(
                                    rememberSharedContentState(
                                        key = backlog.timeTitle
                                    ),
                                    animatedVisibilityScope = animatedContentScope,
                                    enter = fadeIn(),
                                    exit = fadeOut(),
                                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                                )
                                .clickable {
                                    onBacklogEditClick(backlog,  Routine(),-1)
                                }
                        )
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = if (expanded) {
                                    stringResource(R.string.show_less)
                                } else {
                                    stringResource(R.string.show_more)
                                }
                            )
                        }
                    }
                    val amountText = "$" + creditTotal+" unfinished"
                    Text(
                        text = amountText,
                        style = MaterialTheme.typography.subtitle2,
                    )
                    if(expanded){
                        /*
                        * 考虑使用backlog点击编辑删除列编辑*/
                        routineList.map{it ->
                            BriefRoutineRow(
                                modifier = Modifier
                                    .clickable { onBacklogEditClick(backlog,it,it.id) },
                                routine=it,
                                onFinishedChange=onFinishedChange,
                            )
                        }
                        BriefEmptyRow(
                            modifier = Modifier
                                .clickable { onBacklogEditClick(backlog,Routine(),-2) },
                            content = stringResource(R.string.click_to_add)
                        )
                    }
                }
                
            }

//            进度横线
            BaseDivider(creditTotal, routineList.map {it.credit },
                routineList.map { RoutineColors[it.rank]})
            Column(Modifier
                .padding(start = 16.dp, top = 4.dp, end = 8.dp)
            ) {
//                展开所有
                SeeAllButton(
                    modifier = Modifier.clearAndSetSemantics {
                        contentDescription = "All ${backlog.timeTitle}'s Routines"
                    }.clickable { onBacklogDetailClick(backlog.id) }
                )
            }
        }
    }
}
//* -Backlog Edit Card




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
                    .background(MaterialTheme.colors.background)
            )
        }
    }
}

/*
* BacklogHome
*/
@Composable
fun SeeAllButton(modifier: Modifier = Modifier) {
    Box(modifier=modifier
        .padding(16.dp) // 设置内边距，和 TextButton 一致
        .fillMaxWidth()
        .wrapContentSize(Alignment.Center)
    ){
        Text(
            fontSize = 16.sp,
            text= stringResource(R.string.see_all),
            color = MaterialTheme.colors.primary
        )
    }
}

/*
* Single Backlog
*/
//警示对话框-删除
@Composable
fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit, onDeleteCancel: () -> Unit, modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = { /* Do nothing */ },
        title = { Text(stringResource(R.string.attention)) },
        text = { Text(stringResource(R.string.delete_question)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(text = stringResource(R.string.yes))
            }
        })
}

//三色圆圈动画
@Composable
fun ThreeColorCircle(
    proportions:List<Float>,
    colorIndexs:List<Int>,
    modifier: Modifier = Modifier
        .height(300.dp)
        .fillMaxWidth()
) {
    val currentState = remember {
        MutableTransitionState(ThreeCircleProgress.START)
            .apply { targetState = ThreeCircleProgress.END }
    }
    val stroke = with(LocalDensity.current) { Stroke(5.dp.toPx()) }
    val transition = rememberTransition(currentState)
    val angleOffset by transition.animateFloat(
        transitionSpec = {
            tween(
                delayMillis = 500,
                durationMillis = 900,
                easing = LinearOutSlowInEasing
            )
        }
    ) { progress ->
        if (progress == ThreeCircleProgress.START) {
            0f
        } else {
            360f
        }
    }
    val shift by transition.animateFloat(
        transitionSpec = {
            tween(
                delayMillis = 500,
                durationMillis = 900,
                easing = CubicBezierEasing(0f, 0.75f, 0.35f, 0.85f)
            )
        }
    ) { progress ->
        if (progress == ThreeCircleProgress.START) {
            0f
        } else {
            30f
        }
    }
    Canvas(modifier) {
        val innerRadius = (size.minDimension - stroke.width) / 2
        val halfSize = size / 2.0f
        val topLeft = Offset(
            halfSize.width - innerRadius,
            halfSize.height - innerRadius
        )
        val size = Size(innerRadius * 2, innerRadius * 2)
        var startAngle = shift - 90f
        var sweep:Float
        var index=0
        colorIndexs.map { it ->
            sweep = proportions[index] * angleOffset
            if(proportions[index]>0f) {
                drawArc(
                    color = RoutineColors[it],
                    startAngle = startAngle + DividerLengthInDegrees / 2,
                    sweepAngle = sweep - DividerLengthInDegrees,
                    topLeft = topLeft,
                    size = size,
                    useCenter = false,
                    style = stroke
                )
                startAngle += sweep
            }
            index++
        }
        
        sweep = proportions[index] * angleOffset
        if(proportions[index]>0f) {
            drawArc(
                color = unfinishedColor,
                startAngle = startAngle + DividerLengthInDegrees / 2,
                sweepAngle = sweep - DividerLengthInDegrees,
                topLeft = topLeft,
                size = size,
                useCenter = false,
                style = stroke
            )
            startAngle += sweep
        }
    }
}
private enum class ThreeCircleProgress { START, END }
private const val DividerLengthInDegrees = 1.8f
val RoutineColors= listOf(importColor, normalColor, faverColor)

