package com.example.compose.rally.ui.backlog

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.compose.rally.ui.AppViewModelProvider
import com.example.compose.rally.ui.components.BacklogBody
import com.example.compose.rally.ui.components.RoutineRow
import com.example.compose.rally.ui.navigation.NavigationDestination
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
    val arguments = listOf(
        navArgument(backlogIdArg) { type = NavType.IntType }
    )
    val deepLinks = listOf(
        navDeepLink { uriPattern = "rally://$route/{$backlogIdArg}" }
    )
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
    val uiState = viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val routineHomeUiState=routineHomeViewModel.homeUiState.collectAsState()
    val backlog=uiState.value.backlog
    val routines=routineHomeUiState.value.routineList
//    旧属性
    
    val amount=routines.map { routine ->routine.credit }.sum()
    BacklogBody(
        backlogId = backlog.id,
        newRoutineClick=navigateToNewRoutine,
        onDelete ={
            coroutineScope.launch {
                viewModel.deleteBacklogById(backlog.id)
                navigateBack()
            }
        },
        items=routines,
        creditRatios= listOf(backlog.importCredit/amount,backlog.normalCredit/amount,backlog.faverCredit/amount),
        amountsTotal=amount,
        circleLabel=backlog.timeTitle,
    )
    { routine ->
        RoutineRow(
            modifier = Modifier.clickable {navigateToSingleRoutine(routine.id)},
            content = routine.content,
            subcontent = routine.subcontent,
            credit = routine.credit,
            finished = routine.finished,
            color = when(routine.rank){
                0-> importColor
                1-> faverColor
                else -> normalColor
            }
        )
    }
}
