package com.sather.todo.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.sather.todo.ui.theme.RoutineColors

//三色圆圈动画
@Composable
fun ThreeColorCircle(
    amount:Float,
    credits:List<Float> = listOf(1f,0f,0f,0f)
) {
    val properties =
        if(amount > 0f)credits.map { it/amount }
        else listOf(1f,0f,0f,0f)
        
    val colorIndexs = listOf(0,1,2,3)
    val currentState = remember {
        MutableTransitionState(CircleProgress.START)
            .apply { targetState = CircleProgress.END }
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
        }, label = "圆的转角"
    ) { progress ->
        if (progress == CircleProgress.START) {
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
        }, label = ""
    ) { progress ->
        if (progress == CircleProgress.START) {
            0f
        } else {
            30f
        }
    }
    Canvas(
        Modifier
            .height(300.dp)
            .fillMaxWidth()
    ) {
        val innerRadius = (size.minDimension - stroke.width) / 2
        val halfSize = size / 2.0f
        val topLeft = Offset(
            halfSize.width - innerRadius,
            halfSize.height - innerRadius
        )
        val size = Size(innerRadius * 2, innerRadius * 2)
        var startAngle = shift - 90f
        var sweep: Float
        colorIndexs.forEachIndexed { index,it ->
            sweep = properties[index] * angleOffset
            if(properties[index]>0f) {
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
        }
    }
}

private enum class CircleProgress { START, END }
private const val DividerLengthInDegrees = 1.8f