package com.sather.todo.ui.backlog

import androidx.compose.animation.*
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.sather.todo.R
import com.sather.todo.data.Backlog
import com.sather.todo.data.Routine
import com.sather.todo.ui.AppViewModelProvider
import com.sather.todo.ui.components.*
import com.sather.todo.ui.components.backlogs.BaseScreenBody
import com.sather.todo.ui.components.backlogs.ThreeColorCircle
import com.sather.todo.ui.navigation.BaseDestination
import com.sather.todo.ui.routine.formatedCredit
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

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
    navigateToNewRoutine:(Long)->Unit,
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
        newRoutineClick=navigateToNewRoutine,
        onDelete ={
            coroutineScope.launch {
                viewModel.deleteBacklogById(backlog.id)
                navigateBack()
            }
        },
        navigateBack={routines ->
            coroutineScope.launch {
                navigateBack()
            }
            
        },
        finishedRoutineList = finishedRoutines,
        unfinishedRoutineList=unfinishedRoutines,
    ){ changeFinished,deleteTempRoutine,routine ->
        DetailRoutineRow(
            modifier = Modifier
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
            onFinishedChange={ id,finished ->
                coroutineScope.launch {
                    viewModel.updateFinished(id,finished)
                }
                deleteTempRoutine()
                changeFinished()
            },
            swipeToDelete = {
                deleteTempRoutine()
                coroutineScope.launch {
                    viewModel.deleteRoutineById(routine.id)
                }
            },
                
                )
        
    }
}
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun  SingleBacklogBody(
    backlog: Backlog,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    newRoutineClick:(Long)->Unit,
    onDelete: () -> Unit={},
    navigateBack:(List<Routine>)->Unit,
    finishedRoutineList:List<Routine>,
    unfinishedRoutineList:List<Routine>,
    rows: @Composable (() -> Unit,() -> Unit,Routine) -> Unit,
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
            tempUnfinishedList.addAll(unfinishedRoutineList)
        }
    }
    LaunchedEffect(finishedRoutineList.isNotEmpty()){
        if(finishedRoutineList.isNotEmpty()) {
            tempfinishedList.addAll(finishedRoutineList)
        }
    }
    
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
                    navigateBack(tempfinishedList+tempUnfinishedList)
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
            itemsIndexed(
                items = tempUnfinishedList,
                key = { _, routine -> "unFinished"+routine.id}
            ){ index, routine ->
                rows(
                    {tempfinishedList.add(0,routine.copy(finished = true))},
                    {tempUnfinishedList.remove(routine)},
                    routine
                )
            }
//                预加载空列
            item(key = "add_item"){
                DetailRoutineRow(
                    modifier = Modifier
                        .clickable{ newRoutineClick(backlog.id)}
                        .clearAndSetSemantics {
                            contentDescription = "Empty routine"
                        },
                    id = -1,
                    content = stringResource(R.string.todo_list),
                    subcontent = stringResource(R.string.click_to_add),
                    isFinished = false,
                    credit = 0f,
                    colorIndex = 0,
                    onFinishedChange={ _,_ -> },
                    swipeToDelete = {}
                )
            }
            itemsIndexed(
                items = tempfinishedList,
                key = { _, routine -> "finished"+routine.id}
            ){ index, routine ->
                rows(
                    {tempUnfinishedList.add(0,routine.copy(finished = false))},
                    {tempfinishedList.remove(routine)},
                    routine
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
