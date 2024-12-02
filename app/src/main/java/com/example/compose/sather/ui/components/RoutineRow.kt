package com.example.compose.sather.ui.components

import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.compose.sather.data.Routine
import com.example.compose.sather.ui.theme.unfinishedColor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


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
        checkedColor = MaterialTheme.colorScheme.primary, // 选中时的颜色
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
            Text(text = content, style = typography.bodyMedium)
        }
        Spacer(Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = dollarSign,
                style = typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                text = credit.toString(),
                style = typography.headlineMedium,
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
        checkedColor = MaterialTheme.colorScheme.primary, // 选中时的颜色
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
            Text(text = content, style = typography.bodyMedium)
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
    onFinishedChange:(Int,Boolean)->Unit,
    swipeToDelete:() ->Unit,
) {
    val content=routine.content
    val subcontent=routine.subcontent
    val credit=routine.credit
    val color=RoutineColors[routine.rank]
    val id=routine.id
    var finished by remember { mutableStateOf(routine.finished) }
    val dollarSign ="$ "
    val customColors = CheckboxDefaults.colors(
        checkedColor =MaterialTheme.colorScheme.primary, // 选中时的颜色
    )
    Row(
        modifier = modifier
            .height(68.dp)
            .swipeToDismiss(swipeToDelete)
            .clearAndSetSemantics {
                contentDescription =
                    "No.$id routine belong to No.${routine.backlogId}"
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
            Text(text = content, style = typography.bodyMedium)
//            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(text = subcontent, style = typography.titleLarge)
//            }
        }
        Spacer(Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = dollarSign,
                style = typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                text = credit.toString(),
                style = typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        Spacer(Modifier.width(16.dp))
        
//        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(24.dp)
            )
//        }
    }
    BaseDivider()
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
                    "Empty routine"
                
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val typography = MaterialTheme.typography
        Spacer(Modifier.width(12.dp))
        Column(Modifier) {
            Text(text = content, style = typography.bodyMedium)
//            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(text = subcontent, style = typography.titleLarge)
//            }
        }
        Spacer(Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = dollarSign,
                style = typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                text = "0.0",
                style = typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        Spacer(Modifier.width(16.dp))
        
//        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(24.dp)
            )
//        }
    }
    BaseDivider()
}
@Composable
fun BaseDivider(
    modifier: Modifier = Modifier,
    color:Color =MaterialTheme.colorScheme.background
) {
    HorizontalDivider(color = color, thickness = 1.dp, modifier = modifier)
}

/**
 * The modified element can be horizontally swiped away.
 *
 * @param onDismissed Called when the element is swiped to the edge of the screen.
 */
private fun Modifier.swipeToDismiss(
    onDismissed: () -> Unit
): Modifier = composed {
    // This `Animatable` stores the horizontal offset for the element.
    val offsetX = remember { androidx.compose.animation.core.Animatable(0f) }
    pointerInput(Unit) {
        // Used to calculate a settling position of a fling animation.
        val decay = splineBasedDecay<Float>(this)
        // Wrap in a coroutine scope to use suspend functions for touch events and animation.
        coroutineScope {
            while (true) {
                // Wait for a touch down event.
                val pointerId = awaitPointerEventScope { awaitFirstDown().id }
                // Interrupt any ongoing animation.
                offsetX.stop()
                // Prepare for drag events and record velocity of a fling.
                val velocityTracker = VelocityTracker()
                // Wait for drag events.
                awaitPointerEventScope {
                    horizontalDrag(pointerId) { change ->
                        // Record the position after offset
                        val horizontalDragOffset = offsetX.value + change.positionChange().x
                        launch {
                            // Overwrite the `Animatable` value while the element is dragged.
                            offsetX.snapTo(horizontalDragOffset)
                        }
                        // Record the velocity of the drag.
                        velocityTracker.addPosition(change.uptimeMillis, change.position)
                        // Consume the gesture event, not passed to external
                        if (change.positionChange() != Offset.Zero) change.consume()
                    }
                }
                // Dragging finished. Calculate the velocity of the fling.
                val velocity = velocityTracker.calculateVelocity().x
                // Calculate where the element eventually settles after the fling animation.
                val targetOffsetX = decay.calculateTargetValue(offsetX.value, velocity)
                // The animation should end as soon as it reaches these bounds.
                offsetX.updateBounds(
                    lowerBound = -size.width.toFloat(),
                    upperBound = size.width.toFloat()
                )
                launch {
                    if (targetOffsetX.absoluteValue <= size.width) {
                        // Not enough velocity; Slide back to the default position.
                        offsetX.animateTo(targetValue = 0f, initialVelocity = velocity)
                    } else {
                        // Enough velocity to slide away the element to the edge.
                        offsetX.animateDecay(velocity, decay)
                        // The element was swiped away.
                        onDismissed()
                    }
                }
            }
        }
    }
        // Apply the horizontal offset to the element.
        .offset { IntOffset(offsetX.value.roundToInt(), 0) }
}
