package com.sather.todo.textexample

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.core.view.ViewCompat
import com.sather.todo.data.Routine
import com.sather.todo.ui.components.DetailRoutineRow
import com.sather.todo.ui.components.swipeToDismiss
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun VerticalReorderList(routineList:List<Routine>) {
    val view = LocalView.current
    
    var list = remember { mutableStateListOf<Routine>().apply {
        addAll(routineList)
    } }
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        list.apply {
            add(to.index, removeAt(from.index))
        }
        ViewCompat.performHapticFeedback(
            view,
            HapticFeedbackConstantsCompat.SEGMENT_FREQUENT_TICK
        )
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = lazyListState,
    ) {
        items(
            items = list,
            key = { it.id }
        ) {routine ->
            ReorderableItem(
                state = reorderableLazyListState,
                key =  routine.id
            ) { isDragging ->
                val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)
                
                Surface(shadowElevation = elevation) {
                    DetailRoutineRow(
                        modifier = Modifier
                            .clickable{
                                println("click")
                            }
                        .swipeToDismiss { println("swipe") },
                        id = routine.id,
                        content = routine.content,
                        subcontent = routine.subcontent,
                        isFinished = routine.finished,
                        credit = routine.credit,
                        colorIndex = routine.rank,
                    ) {
                        IconButton(
                            onClick = {},
                            modifier = Modifier.fillMaxHeight()
                                .draggableHandle()
                        ) {
                            Icon(Icons.Rounded.Menu, "Drag Handle")
                        }
                    }
                }
            }
        }
    }
}
