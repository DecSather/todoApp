package com.example.compose.rally.ui.backlog

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.compose.rally.ui.AppViewModelProvider
import com.example.compose.rally.ui.components.SingleBacklogBody
import com.example.compose.rally.ui.components.RoutineRow
import com.example.compose.rally.ui.navigation.RallyDestination
import com.example.compose.rally.ui.routine.RoutineHomeViewModel
import com.example.compose.rally.ui.theme.faverColor
import com.example.compose.rally.ui.theme.importColor
import com.example.compose.rally.ui.theme.normalColor
import kotlinx.coroutines.launch

object SingleBacklogDestination : RallyDestination {
    override val route = "single_backlog"
    override val icon = Icons.Filled.Timer
    const val backlogIdArg = "backlogId"
    val routeWithArgs = "$route/{$backlogIdArg}"
    val arguments = listOf(navArgument(backlogIdArg) {
        type = NavType.IntType
    })
}
//记得添加删除键
@Composable
fun SingleBacklogScreen(
    navigateBack: () -> Unit,
    navigateToNewRoutine:(Int)->Unit,
    navigateToSingleRoutine: (Int) -> Unit={},
    viewModel: SingleBacklogViewModel= viewModel(factory = AppViewModelProvider.Factory),
    routineHomeViewModel: RoutineHomeViewModel= viewModel(factory = AppViewModelProvider.Factory),
) {
    val backlogUiState = viewModel.backlogUiState.collectAsState()
    val backlog = backlogUiState.value.backlog
    val coroutineScope = rememberCoroutineScope()
    val routineHomeUiState=routineHomeViewModel.homeUiState.collectAsState()
    val routines=routineHomeUiState.value.routineList
    SingleBacklogBody(
        backlog =backlog,
        newRoutineClick=navigateToNewRoutine,
        onDelete ={
            coroutineScope.launch {
                viewModel.deleteBacklogById(backlog.id)
                navigateBack()
            }
        },
        navigateBack=navigateBack,
        items=routines,
    )
    { routine ->
        RoutineRow(
            modifier = Modifier.clickable {navigateToSingleRoutine(routine.id)},
            routine=routine,
            onFinishedChange={ id,finished ->
                coroutineScope.launch {
                    routineHomeViewModel.onRoutineFinishedChange(id,finished)
                }
            }
            
            
        )
        
    }
}
