package com.sather.todo.ui.backlog

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.core.view.ViewCompat
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
import com.sather.todo.ui.navigation.BaseDestination
import com.sather.todo.ui.routine.formatedCredit
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

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
//    if(routineUiState.routineList.isNotEmpty())
//        VerticalReorderList(routineUiState.routineList)
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
    val view = LocalView.current
    
//    临时数据
    val tempfinishedList = remember {mutableStateListOf<Routine>()}
    var tempUnfinishedList = remember { mutableStateListOf<Routine>()}
    
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        tempUnfinishedList.apply {
            add(to.index-1, removeAt(from.index-1))
        }
        ViewCompat.performHapticFeedback(
            view,
            HapticFeedbackConstantsCompat.SEGMENT_FREQUENT_TICK
        )
    }
//    本地->临时数据
    LaunchedEffect(unfinishedRoutineList.isNotEmpty()){
        if(unfinishedRoutineList.isNotEmpty()){
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
//    本地数据
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
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
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
    
    BaseScreenBody(
        state = lazyListState,
        lazyColumnModifier = Modifier
            .semantics { contentDescription = "No.${backlog.id} Screen" },
        top = {
//                三色圈
            ThreeColorCircle(
                amount = finishedAmount+unfinishedAmount,
                credits = listOf(unfinishedAmount,importCredit,normalCredit,faverCredit),
            )
            IconButton(onClick = {
                    navigateBack()
                }) {
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
                key = {routine -> routine.id}
            ){ routine ->
                
                
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        println("swipe box value：$it")
                        if (it == SwipeToDismissBoxValue.StartToEnd) { // 仅在完全滑动时触发
                                tempUnfinishedList.remove(routine)
                                swipeToDeleteRoutine(routine.id)
                                 true
                            
                        } else {
                            false // 未达阈值时回弹
                        }
                    },
                    positionalThreshold = {
                        it
                    }
                    
                )
                
                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface))
                    },
                    content = {
                        ReorderableItem(
                            state = reorderableLazyListState,
                            key = routine.id
                        ) { isDragging ->
                            val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)
                            Surface(shadowElevation = elevation) {
                                DetailRoutineRow(
                                    modifier = Modifier
                                        .clickable { navigateToSingleRoutine(routine.id) }
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
                                    onFinishedChange = {
                                        tempUnfinishedList.remove(routine)
                                        tempfinishedList.add(0, routine.copy(finished = true))
                                    },
                                    swipeToDelete = {
                                        tempUnfinishedList.remove(routine)
                                        swipeToDeleteRoutine(routine.id)
                                    },
                                ) {
//                    拖拽Icon
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
                    },
                )
                
                
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