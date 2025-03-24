package com.sather.todo.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sather.todo.R
import com.sather.todo.data.Backlog
import com.sather.todo.data.Routine
import com.sather.todo.ui.components.backlogs.TextDisplaysRow
import com.sather.todo.ui.theme.faverColor
import com.sather.todo.ui.theme.importColor
import com.sather.todo.ui.theme.normalColor
import com.sather.todo.ui.theme.unfinishedColor
import kotlin.math.roundToInt


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
//backlog卡片-可改finished
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BacklogSwipeCard(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    
    modifier: Modifier = Modifier,
    backlog: Backlog,
    routineList:List<Routine>,
    
    onExpandClick:(Long,Boolean)->Unit,
    onVisibleClick:(Long,Boolean)->Unit,
    onFinishedChange:(Long,Boolean)->Unit,
    
    onDelete:(Long)->Unit,
    onBacklogDetailClick: (Long) -> Unit,
    onBacklogEditClick:(Int) -> Unit
    
) {
    val creditTotal:Float =routineList.map { it.credit }.sum()
    val amountText = "" + routineList.size + stringResource(R.string.unfinished)
    var expanded by rememberSaveable { mutableStateOf(backlog.isExpand) }
    var isVisble by rememberSaveable{ mutableStateOf(backlog.isVisible) }
    
    val animVisibleState = remember {  MutableTransitionState(false).apply {  targetState = true  }  }
    
    if (!animVisibleState.targetState &&
        !animVisibleState.currentState
    ) {
        onDelete(backlog.id)
        return
    }
    AnimatedVisibility(
        visibleState = animVisibleState,
    ) {
        
        SwipeToDeleteBox(
            startAction = listOf {
                FloatingActionButton(
                    shape = CircleShape,
                    modifier = Modifier.align(Alignment.Center),
                    onClick = {
                        animVisibleState.targetState = false
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Delete Backlog No.${backlog.id}"
                    )
                }
            }
        ) {
            Card(
                modifier = modifier.padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(basePadding)
                            .animateContentSize()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
//                            isVisible
                            Checkbox(
                                checked = !isVisble,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary, // 选中时的颜色
                                ),
                                onCheckedChange = {
                                    isVisble = !isVisble
                                    expanded = isVisble
                                    onVisibleClick(backlog.id,isVisble)
                                    
                                
                                }
                            )
//                            标题
                            with(sharedTransitionScope) {
                                Text(
                                    text = backlog.timeTitle,
                                    style = MaterialTheme.typography.headlineLarge,
                                    modifier = Modifier
                                        .weight(1f)
                                        .sharedBounds(
                                            rememberSharedContentState(
                                                key = "${backlog.id}/${backlog.timeTitle}"
                                            ),
                                            animatedVisibilityScope = animatedContentScope,
                                            enter = fadeIn(),
                                            exit = fadeOut(),
                                            resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                                        )
                                        .clickable {
                                            onBacklogEditClick(-1)
                                        }
                                )
                            }
//                            展开按钮
                            IconButton(onClick = {
                                expanded = !expanded
                                onExpandClick(backlog.id, expanded)
                            }) {
                                Icon(
                                    imageVector =
                                    if (expanded)
                                        Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                    contentDescription = if (expanded) {
                                        stringResource(R.string.show_less)
                                    } else {
                                        stringResource(R.string.show_more)
                                    }
                                )
                            }
                        }
                        Text(
                            text = amountText,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        if (expanded) {
                            Column{
                                var sortId = 0
                                routineList.map{it ->
                                    key(it.id){
                                        val routine = it.copy(sortId = sortId)
                                        sortId++
                                        TextDisplaysRow(
                                            modifier = Modifier.clickable { onBacklogEditClick(routine.sortId) },
                                            content = routine.content,
                                            credit = routine.credit.toString(),
                                            colorIndex = routine.rank,
                                            finished = routine.finished,
                                            onFinishedChange = { it ->
                                                if(routineList.size == 1) {
                                                    isVisble = false
                                                    expanded = isVisble
                                                    onVisibleClick(backlog.id, isVisble)
                                                }
                                                onFinishedChange(routine.id,it)
                                                               },
                                        )
                                    }
                                }
                            }
                            TextDisplaysRow(
                                modifier = Modifier.clickable { onBacklogEditClick(-2) },
                                content = stringResource(R.string.click_to_add)
                            )
                        }
                        
                    }
                    
                    if(isVisble) {
//            进度横线
                        BaseDivider(creditTotal, routineList.map { it.credit },
                            routineList.map { RoutineColors[it.rank] })
                        Column(
                            Modifier
                                .padding(start = basePadding, top = spacePadding, end = basePadding)
                        ) {
//                展开所有
                            SeeAllButton(
                                modifier = Modifier.clearAndSetSemantics {
                                    contentDescription = "${backlog.timeTitle}'s Routines"
                                }.clickable { onBacklogDetailClick(backlog.id) }
                            )
                        }
                    }
                }
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
                    .background(MaterialTheme.colorScheme.background)
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
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
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
                Text(
                    text = stringResource(R.string.no),
                    style = MaterialTheme.typography.headlineMedium,
                    )
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(
                    text = stringResource(R.string.yes),
                    style = MaterialTheme.typography.headlineMedium,
                    )
            }
        })
}

val RoutineColors= listOf(unfinishedColor,importColor, normalColor, faverColor)

