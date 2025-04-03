package com.sather.todo.ui.backlog.components

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

//三色圆圈动画
@Composable
fun ThreeColorCircle(
    amount:Float,
    credits:List<Float> = listOf(1f,0f,0f,0f)
) {
    val properties =
        if(amount > 0f)credits.map { it/amount }
        else credits
        
    val colorIndexs = listOf(0,1,2,3)
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
        var sweep:Float
        var index=0
        colorIndexs.map { it ->
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
            index++
        }
    }
}

private enum class ThreeCircleProgress { START, END }
private const val DividerLengthInDegrees = 1.8f