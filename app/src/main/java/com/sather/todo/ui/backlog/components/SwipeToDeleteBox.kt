package com.sather.todo.ui.backlog.components

import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.sather.todo.ui.components.boxActionWidth
import kotlin.math.roundToInt

//滑动效果盒子

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeToDeleteBox(
    modifier: Modifier = Modifier,
    actionWidth: Dp  = boxActionWidth,
    startAction: List<@Composable BoxScope.() -> Unit>,
    content: @Composable BoxScope.() -> Unit
) {
    val density = LocalDensity.current
    val actionWidthPx = with(density) {
        actionWidth.toPx()
    }
    val startWidth = actionWidthPx * startAction.size
    val startActionSize =startAction.size + 1
    var contentWidth by remember { mutableFloatStateOf(0f) }
    var contentHeight by remember { mutableFloatStateOf(0f) }
    val state = remember(startWidth, contentWidth) {
        AnchoredDraggableState(
            initialValue = DragAnchors.Center,
            anchors = DraggableAnchors {
                DragAnchors.Start at startWidth
                DragAnchors.Center at 0f
            },
            positionalThreshold = { distance ->
                distance * 0.5f
            },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            snapAnimationSpec = TweenSpec(durationMillis = 350),
            decayAnimationSpec = exponentialDecay(10f),
        )
    }
        Box(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .anchoredDraggable(
                    state = state,
                    orientation = Orientation.Horizontal,
                )
                .clipToBounds()
        ) {
            startAction.forEachIndexed { index, action ->
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .width(actionWidth)
                        .height(with(density) {
                            contentHeight.toDp()
                        })
                        .offset {
                            IntOffset(
                                x = if (state.offset <= actionWidthPx * startActionSize) {
                                    (-actionWidthPx + state.offset / startActionSize * (startActionSize - index)).roundToInt()
                                } else {
                                    (-actionWidthPx * (index + 1) + state.offset).roundToInt()
                                },
                                y = 0,
                            )
                        }
                ) {
                    action()
                }
            }
            Box(
                modifier = Modifier
                    .onSizeChanged {
                        contentWidth = it.width.toFloat()
                        contentHeight = it.height.toFloat()
                    }
                    .offset {
                        IntOffset(
                            x = state.offset.roundToInt(),
                            y = 0,
                        )
                    }
            ) {
                content()
            }
        }
    
    
}

enum class DragAnchors {
    Start,
    Center,
}