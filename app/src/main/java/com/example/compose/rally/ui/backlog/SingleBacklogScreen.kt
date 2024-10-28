package com.example.compose.rally.ui.backlog

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.compose.rally.data.BacklogData
import com.example.compose.rally.data.BackloggetRoutines
import com.example.compose.rally.ui.AppViewModelProvider
import com.example.compose.rally.ui.components.CommonBody
import com.example.compose.rally.ui.components.RoutineRow
import com.example.compose.rally.ui.navigation.NavigationDestination

object SingleBacklogDestination : NavigationDestination {
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
@Composable
fun SingleBacklogScreen(
    navigateToAddBacklog: () -> Unit={},
    navigateToUpdateBacklog: (Int) -> Unit={},
    backlogType: String? = BacklogData.backlogs.first().timeTitle,
    viewModel: SingleBacklogViewModel= viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState = viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
//    旧属性
    val backlog = uiState.value.backlog
//    val routines= BackloggetRoutines(backlog)
//    val amount=routines.map { routine ->routine.credit }.sum()
    val amount=0f
    CommonBody(
//        items=routines,
        creditRatios= listOf(backlog.importCredit/amount,backlog.normalCredit/amount,backlog.faverCredit/amount),
        amountsTotal=amount,
        circleLabel=backlog.timeTitle,
    )
//    { routine ->
//        RoutineRow(
//            modifier = Modifier.clickable { /*waitng for implement*/ },
//            content = routine.content,
//            subcontent = routine.subcontent,
//            credit = routine.credit,
//            finished = routine.finished,
//            color = routine.color
//        )
//    }
}
