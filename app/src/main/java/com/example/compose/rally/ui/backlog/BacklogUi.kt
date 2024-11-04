package com.example.compose.rally.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteForever
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
import com.example.compose.rally.ui.routine.formatedCredit
import com.example.compose.rally.ui.theme.BackgroudBlue
import com.example.compose.rally.ui.theme.Blue900
import java.time.format.DateTimeFormatter

val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
@Composable
fun  SingleBacklogBody(
    backlog: Backlog,
    newRoutineClick:(Int)->Unit,
    onDelete: () -> Unit={},
    items:List<Routine>,
    rows: @Composable (Routine) -> Unit,
) {
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
    Box(modifier=Modifier.fillMaxSize())
    {
        val amount=(backlog.importCredit+backlog.normalCredit+backlog.faverCredit)
        val creditRatios=
            if(amount>0f)
            listOf(backlog.importCredit/amount,backlog.normalCredit/amount,backlog.faverCredit/amount)
        else listOf(0f,0f,1f)
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Box(Modifier.padding(16.dp)) {
//                三色圈
                ThreeColorCircle(
                    proportions = creditRatios
                )
                Spacer(Modifier.height(12.dp))
                Column(modifier = Modifier.align(Alignment.Center)) {
                    Text(
                        text = backlog.timeTitle,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = formatedCredit( amount.toString()),
                        style = MaterialTheme.typography.h2,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
//            routineList
            Card {
                Column(modifier = Modifier.padding(12.dp).weight(1f)){
                    items.map {
                        item ->
                        rows(item)
                    }
//                预加载空列
                    RoutineRow(
                        modifier = Modifier.clickable{ newRoutineClick(backlog.id)},
                        content = "待办清单",
                        subcontent = "点击添加",
                        credit = 0f,
                        finished = false,
                        color = Blue900
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(MaterialTheme.colors.background)
            )
        }
        
        FloatingActionButton(
            onClick = { deleteConfirmationRequired = true },
            backgroundColor = MaterialTheme.colors.surface,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp),
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.DeleteForever,
                contentDescription = "Delete Backlog"
            )
    }
        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    onDelete()
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
    
}


@Composable
private fun DeleteConfirmationDialog(
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

@Composable
fun RoutineRow(
    modifier: Modifier = Modifier,
    content: String,
    subcontent:String,
    credit: Float,
    finished:Boolean,
    color: Color
) {
    BaseRow(
        modifier = modifier,
        color = color,
        title = content,
        subtitle = subcontent,
        amount = credit,
        negative = finished
    )
}

@Composable
private fun BaseRow(
    modifier: Modifier = Modifier,
    color: Color,
    title: String,
    subtitle: String,
    amount: Float,
    negative: Boolean
) {
    val dollarSign = if (negative) "–$ " else "$ "
    Row(
        modifier = modifier
            .height(68.dp)
            .clearAndSetSemantics {
                contentDescription =
                    "$title account ending in ${subtitle.takeLast(4)}, current balance $dollarSign$amount"
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val typography = MaterialTheme.typography
        RowIndicator(
            color = color,
            modifier = Modifier
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier) {
            Text(text = title, style = typography.body1)
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(text = subtitle, style = typography.subtitle1)
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
                text = amount.toString(),
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
//列区分竖线
@Composable
private fun RowIndicator(color: Color, modifier: Modifier = Modifier) {
    Spacer(
        modifier
            .size(4.dp, 36.dp)
            .background(color = color)
    )
}
//列
@Composable
fun BaseDivider(
    total:Float,
    data: List<Float>,
    colors: List<Color>
) {
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
                    .background(BackgroudBlue)
            )
        }
    }
}


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

//三色圆圈动画

private const val DividerLengthInDegrees = 1.8f

@Composable
fun ThreeColorCircle(
    proportions:List<Float>,
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
        if(proportions.get(0)>0){
            sweep = proportions.get(0) * angleOffset
            drawArc(
                color = Color(0xFF005D57),
                startAngle = startAngle + DividerLengthInDegrees / 2,
                sweepAngle = sweep - DividerLengthInDegrees,
                topLeft = topLeft,
                size = size,
                useCenter = false,
                style = stroke
            )
            startAngle += sweep
        }
        if(proportions.get(1)>0) {
            sweep = proportions.get(1) * angleOffset
            drawArc(
                color = Color(0xFF039667),
                startAngle = startAngle + DividerLengthInDegrees / 2,
                sweepAngle = sweep - DividerLengthInDegrees,
                topLeft = topLeft,
                size = size,
                useCenter = false,
                style = stroke
            )
            startAngle += sweep
        }
        if(proportions.get(2)>0) {
            sweep = proportions.get(2) * angleOffset
            drawArc(
                color = Color(0xFF04B97F),
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

