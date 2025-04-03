package com.sather.todo.ui.backlog

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.sather.todo.R
import com.sather.todo.data.Backlog
import com.sather.todo.data.Routine
import com.sather.todo.data.generateSimpleId
import com.sather.todo.ui.AppViewModelProvider
import com.sather.todo.ui.backlog.components.BaseScreenBody
import com.sather.todo.ui.backlog.components.DeleteConfirmationDialog
import com.sather.todo.ui.backlog.components.ThreeColorCircle
import com.sather.todo.ui.components.DetailRoutineRow
import com.sather.todo.ui.components.LargeHeight
import com.sather.todo.ui.navigation.BaseDestination
import com.sather.todo.ui.routine.formatedCredit
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.*

object SingleBacklogDestination : BaseDestination {
    override val route = "single_backlog"
    override val icon = Icons.Filled.Timer
    const val backlogIdArg = "backlogId"
    val routeWithArgs = "$route/{$backlogIdArg}"
    val arguments = listOf(navArgument(backlogIdArg) {
        type = NavType.LongType
    })
}
//记得添加删除键
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SingleBacklogScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    navigateBack: () -> Unit,
    navigateToSingleRoutine: (Long) -> Unit={},
    viewModel: SingleBacklogViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val backlogUiState by viewModel.backlogUiState.collectAsState()
    val backlog = backlogUiState.backlog
    val routineUiState by viewModel.routineUiState.collectAsState()
    val finishedRoutines= routineUiState.routineList.filter { it -> it.finished }
    val unfinishedRoutines = routineUiState.routineList.filter { it -> !it.finished }
    
    val coroutineScope = rememberCoroutineScope()
    SingleBacklogBody(
        backlog =backlog,
        sharedTransitionScope=sharedTransitionScope,
        animatedContentScope=animatedContentScope,
        navigateBack={
            coroutineScope.launch {
                navigateBack()
            }
            
        },
        navigateToSingleRoutine = navigateToSingleRoutine,
        onAddRoutine = {routine ->
            coroutineScope.launch {
                viewModel.addRoutine(routine)
            }
        },
        onDelete ={
            coroutineScope.launch {
                viewModel.deleteBacklogById(backlog.id)
                navigateBack()
            }
        },
        onUpdateSort = { sortList ->
            coroutineScope.launch {
                viewModel.updateSort(sortList)
            }
            
        },
        swipeToDeleteRoutine = { id->
            coroutineScope.launch {
                viewModel.deleteRoutineById( id)
            }
        },
        finishedRoutineList = finishedRoutines,
        unfinishedRoutineList=unfinishedRoutines,
    )
}
@OptIn(ExperimentalSharedTransitionApi::class, FlowPreview::class)
@Composable
fun  SingleBacklogBody(
    backlog: Backlog,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onAddRoutine:(Routine)->Unit,
    onDelete: () -> Unit={},
    navigateBack:()->Unit,
    navigateToSingleRoutine: (Long) -> Unit={},
    onUpdateSort:(List<Routine>)->Unit,
    swipeToDeleteRoutine :(Long)->Unit,
    finishedRoutineList:List<Routine>,
    unfinishedRoutineList:List<Routine>,
) {
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
    val tempUnfinishedList = remember {mutableStateListOf<Routine>()}
    val tempfinishedList = remember {mutableStateListOf<Routine>()}
    var importCredit = 0f
    var normalCredit = 0f
    var faverCredit = 0f
    finishedRoutineList.map{it ->
        when(it.rank){
            1 -> importCredit += it.credit
            2 -> normalCredit += it.credit
            else -> faverCredit += it.credit
        }
    }
    val finishedAmount=importCredit + normalCredit + faverCredit
    val unfinishedAmount=unfinishedRoutineList.map { it.credit }.sum()
    
    LaunchedEffect(unfinishedRoutineList.isNotEmpty()){
        if(unfinishedRoutineList.isNotEmpty()) {
            tempUnfinishedList.clear()
            tempUnfinishedList.addAll(unfinishedRoutineList)
        }
    }
    LaunchedEffect(finishedRoutineList.isNotEmpty()){
        if(finishedRoutineList.isNotEmpty()) {
            tempfinishedList.clear()
            tempfinishedList.addAll(finishedRoutineList)
        }
    }
    // 自动提交更新的逻辑
    LaunchedEffect(tempUnfinishedList) {
        snapshotFlow { tempUnfinishedList.toList() } // 转换为不可变快照
            .debounce(1000) // 防抖：500ms无操作后提交
            .distinctUntilChanged() // 避免重复提交
            .collectLatest { sortedList ->
                onUpdateSort(sortedList)
            }
    }
    LaunchedEffect(tempfinishedList) {
        snapshotFlow { tempfinishedList.toList() } // 转换为不可变快照
            .debounce(1000) // 防抖：500ms无操作后提交
            .distinctUntilChanged() // 避免重复提交
            .collectLatest { sortedList ->
                onUpdateSort(sortedList)
            }
    }

//    拖拽计算
    val density = LocalDensity.current
    
    var draggedItemIndex by remember { mutableIntStateOf(-1) }
    val heightDp = remember { LargeHeight }
    val heightPx = remember { with(density) { heightDp.toPx() } }
    
    BaseScreenBody(
        lazyColumnModifier = Modifier
            .semantics { contentDescription = "No.${backlog.id} Screen" },
        top = {
//                三色圈
            ThreeColorCircle(
                amount = finishedAmount+unfinishedAmount,
                credits = listOf(unfinishedAmount,importCredit,normalCredit,faverCredit),
            )
            IconButton(
                onClick = {
                    navigateBack()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = stringResource(R.string.back_button)
                )
            }
            Spacer(Modifier.height(12.dp))
            Column(modifier = Modifier.align(Alignment.Center)) {
                with(sharedTransitionScope){
                    Text(
                        text = backlog.timeTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .wrapContentWidth()
                            .sharedBounds(
                                rememberSharedContentState(
                                    key = "${backlog.id}/${backlog.timeTitle}"
                                ),
                                animatedVisibilityScope = animatedContentScope,
                                enter = fadeIn(),
                                exit = fadeOut(),
                                resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                            )
                    
                    )
                    Text(
                        text = formatedCredit( finishedAmount.toString()),
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )
                    
                }
            }
        },
        rows = {
//            未完成列
            items(
                items = tempUnfinishedList,
                key = {routine -> routine.id + generateSimpleId()}
            ){ routine ->
                var dragOffsetY by remember { mutableFloatStateOf(0F) }
                var isDragged by remember { mutableStateOf(false) }
                DetailRoutineRow(
                    modifier = Modifier
                        .graphicsLayer {
                            translationY = dragOffsetY
                            shadowElevation = if (isDragged) 8F else 0F
                            alpha = if (isDragged) .9F else 1F
                        }
                        .zIndex(if (isDragged) 2F else 0F)
                        .clickable {navigateToSingleRoutine(routine.id)}
                        .clearAndSetSemantics {
                            contentDescription =
                                "No.${routine.id} routine belong to No.${routine.backlogId}"
                        },
                    id = routine.id,
                    content = routine.content,
                    subcontent = routine.subcontent,
                    isFinished = routine.finished,
                    credit = routine.credit,
                    colorIndex = routine.rank,
                    onFinishedChange={
                        tempUnfinishedList.remove(routine)
                        tempfinishedList.add(0,routine.copy(finished = true))
                    },
                    swipeToDelete = {
                        tempUnfinishedList.remove(routine)
                        swipeToDeleteRoutine(routine.id)
                    },
                ){
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        draggedItemIndex = tempUnfinishedList.indexOf(routine)
                                        isDragged = true
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragOffsetY += dragAmount.y
                                        
                                        val indexOffset = (dragOffsetY / heightPx).toInt()
                                        val newIndex = indexOffset + draggedItemIndex
                                        
                                        if (newIndex in tempUnfinishedList.indices && newIndex != draggedItemIndex) {
                                            Collections.swap(tempUnfinishedList, draggedItemIndex, newIndex)
                                            draggedItemIndex = newIndex
                                            dragOffsetY -= indexOffset * heightPx
                                        }
                                    },
                                    onDragEnd = {
                                        dragOffsetY = 0F
                                        draggedItemIndex = -1
                                        isDragged = false
                                    },
                                    onDragCancel = {
                                        dragOffsetY = 0F
                                        draggedItemIndex = -1
                                        isDragged = false
                                    }
                                )
                            }
                    ) {
                        Icon(Icons.Rounded.Menu, "Drag Handle")
                    }
                }
            }
//           预加载空列
            item(key = "add_item"){
                DetailRoutineRow(
                    modifier = Modifier
                        .clickable{
                            val routine = Routine(
                                content = "",
                                sortId = tempUnfinishedList.size,
                                backlogId = backlog.id
                            )
                            onAddRoutine(routine)
                            navigateToSingleRoutine(routine.id)
                        }
                        .clearAndSetSemantics {
                            contentDescription = "Empty routine"
                        },
                    id = -1,
                    content = stringResource(R.string.todo_list),
                    subcontent = stringResource(R.string.click_to_add),
                    isFinished = false,
                    credit = 0f,
                    colorIndex = 0,
                )
            }
//            完成列
            items(
                items = tempfinishedList,
                key = {routine -> routine.id + generateSimpleId()}
            ){ routine ->
                DetailRoutineRow(
                    id = routine.id,
                    content = routine.content,
                    subcontent = routine.subcontent,
                    isFinished = routine.finished,
                    credit = routine.credit,
                    colorIndex = routine.rank,
                    onFinishedChange={
                        tempfinishedList.remove(routine)
                        tempUnfinishedList.add(0,routine.copy(finished = false))
                    },
                    swipeToDelete = {
                        tempfinishedList.remove(routine)
                        swipeToDeleteRoutine(routine.id)
                    },
                )
            }
        },
        floatButtonAction = {
            deleteConfirmationRequired = true
        },
        floatButtoncontent = {
            Icon(
                imageVector = Icons.Default.DeleteForever,
                contentDescription = "Delete Backlog"
            )
        }
    )
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