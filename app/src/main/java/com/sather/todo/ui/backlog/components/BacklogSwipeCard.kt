package com.sather.todo.ui.backlog.components

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import com.sather.todo.R
import com.sather.todo.data.Backlog
import com.sather.todo.data.Routine
import com.sather.todo.ui.components.BaseDivider
import com.sather.todo.ui.components.basePadding
import com.sather.todo.ui.components.cardMaxHeight
import com.sather.todo.ui.components.spacePadding

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BacklogSwipeCard(
    
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
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
                modifier = modifier.padding(horizontal = basePadding) ,
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
                            Column(
                                Modifier.heightIn( max = cardMaxHeight )
                                    .verticalScroll(
                                        rememberScrollState()
                                    )
                            ){
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