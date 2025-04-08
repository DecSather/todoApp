package com.sather.todo.ui.backlog.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
    amount: Float,
    credits: List<Float> = listOf(1f, 0f, 0f, 0f)
) {
    // 计算各部分比例，使用remember保存以避免不必要的重计算
    val properties by remember(amount, credits) {
        derivedStateOf {
            if (amount > 0f) credits.map { it / amount } else credits
        }
    }
    
    // 定义颜色索引
    val colorIndices = listOf(0, 1, 2, 3)
    
    // 使用animateFloatAsState替代Transition，使动画在参数变化时更平滑
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 900,
            delayMillis = 500,
            easing = LinearOutSlowInEasing
        ),
        label = "circleProgress"
    )
    
    // 计算角度偏移和位移，基于animationProgress
    val angleOffset = 360f * animationProgress
    val shift = 30f * animationProgress
    
    // 定义描边宽度
    val stroke = with(LocalDensity.current) { Stroke(5.dp.toPx()) }
    
    Canvas(
        modifier = Modifier
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
        
        colorIndices.forEachIndexed { index, _ ->
            sweep = properties[index] * angleOffset
            if (properties[index] > 0f) {
                drawArc(
                    color = RoutineColors[index],
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

private const val DividerLengthInDegrees = 1.8f