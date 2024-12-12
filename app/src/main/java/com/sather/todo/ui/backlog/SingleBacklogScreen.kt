
package com.sather.todo.ui.backlog

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.sather.todo.R
import com.sather.todo.data.Backlog
import com.sather.todo.data.Routine
import com.sather.todo.ui.AppViewModelProvider
import com.sather.todo.ui.components.*
import com.sather.todo.ui.components.DeleteConfirmationDialog
import com.sather.todo.ui.components.ThreeColorCircle
import com.sather.todo.ui.navigation.BaseDestination
import com.sather.todo.ui.routine.formatedCredit
import kotlinx.coroutines.launch

object SingleBacklogDestination : BaseDestination {
    override val route = "single_backlog"
    override val icon = Icons.Filled.Timer
    const val backlogIdArg = "backlogId"
    val routeWithArgs = "$route/{$backlogIdArg}"
    val arguments = listOf(navArgument(backlogIdArg) {
        type = NavType.IntType
    })
}
//记得添加删除键
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SingleBacklogScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    navigateBack: () -> Unit,
    navigateToNewRoutine:(Int)->Unit,
    navigateToSingleRoutine: (Int) -> Unit={},
    viewModel: SingleBacklogViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val backlogUiState = viewModel.backlogUiState.collectAsState()
    val backlog = backlogUiState.value.backlog
    val routineUiState=viewModel.routineUiState.collectAsState()
    val finishedRoutines=routineUiState.value.routineList.filter { it -> it.finished }
    val unfinishedRoutines = routineUiState.value.routineList.filter { it -> !it.finished }
    
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
        navigateBack=navigateBack,
        finishedItems = finishedRoutines,
        unfinishedItems=unfinishedRoutines,
    )
    { routine ->
        DetailRoutineRow(
            modifier = Modifier.clickable {navigateToSingleRoutine(routine.id)},
            routine=routine,
            onFinishedChange={ id,finished ->
                coroutineScope.launch {
                    viewModel.onRoutineFinishedChange(id,finished)
                }
            },
            swipeToDelete ={ coroutineScope.launch {
                viewModel.deleteRoutineById(routine.id)
            } },
        )
        
    }
}
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun  SingleBacklogBody(
    backlog: Backlog,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    newRoutineClick:(Int)->Unit,
    onDelete: () -> Unit={},
    navigateBack:()->Unit,
    finishedItems:List<Routine>,
    unfinishedItems:List<Routine>,
    rows: @Composable (Routine) -> Unit,
) {
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
    Box(modifier=Modifier.fillMaxSize())
    {
        val finishedAmount=finishedItems.map{ it -> it.credit}.sum()
        val amount=finishedAmount+unfinishedItems.map { it.credit }.sum()
        val creditRatios=
           if(amount>0f) {
               finishedItems.map { it -> it.credit / amount }+unfinishedItems.map{it -> it.credit}.sum()/amount
           }
            else listOf(1f)
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Box(Modifier.padding(16.dp)) {
//                三色圈
                ThreeColorCircle(
                    proportions = creditRatios,
                    colorIndexs =finishedItems.map { it -> it.rank }
                )
                IconButton(onClick = navigateBack) {
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
            }
            Spacer(Modifier.height(10.dp))
//            routineList
            Card(
                shape = RectangleShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer,
            )) {
                Column(modifier = Modifier.padding(12.dp).fillMaxWidth()){
                    unfinishedItems.map {
                            item ->
                        rows(item)
                    }
//                预加载空列
                    DetailEmptyRow(
                        modifier = Modifier.clickable{ newRoutineClick(backlog.id)},
                        content = stringResource(R.string.todo_list),
                        subcontent = stringResource(R.string.click_to_add),
                    )
                    finishedItems.map {
                            item ->
                        rows(item)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
        
        FloatingActionButton(
            shape = CircleShape,
            onClick = { deleteConfirmationRequired = true },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.DeleteForever,
                contentDescription = "Delete Backlog"
            )
        }
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
    
}

