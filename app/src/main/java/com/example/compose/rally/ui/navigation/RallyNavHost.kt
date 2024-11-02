package com.example.compose.rally.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.compose.rally.ui.accounts.AccountsScreen
import com.example.compose.rally.ui.accounts.SingleAccountScreen
import com.example.compose.rally.ui.backlog.BacklogHome
import com.example.compose.rally.ui.backlog.BacklogHomeScreen
import com.example.compose.rally.ui.backlog.SingleBacklogDestination
import com.example.compose.rally.ui.backlog.SingleBacklogScreen
import com.example.compose.rally.ui.overview.OverviewScreen
import com.example.compose.rally.ui.routine.RoutineEntryDestination
import com.example.compose.rally.ui.routine.RoutineEntryScreen
import com.example.compose.rally.ui.routine.SingleRoutineDestination
import com.example.compose.rally.ui.routine.SingleRoutineScreen

@Composable
fun RallyNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BacklogHome.route,
        modifier = modifier
    ) {
//            Backlog Home
        composable(route = BacklogHome.route) {
            BacklogHomeScreen(
                onBacklogClick = {
                    navController.navigate("${SingleBacklogDestination.route}/${it}")
                }
            )
        }

//        Single Backlog
        composable(
            route = SingleBacklogDestination.routeWithArgs,
            arguments = listOf(navArgument(SingleBacklogDestination.backlogIdArg) {
                type = NavType.IntType
            })
        ){
                backStackEntry ->
            val backlogId = backStackEntry.arguments?.getInt("backlogId") ?: 0
            SingleBacklogScreen(
                navigateBack= { navController.popBackStack() },
//                跳转新增待办
                navigateToNewRoutine = {navController.navigateToNewRoutine(backlogId)},
                navigateToSingleRoutine ={routineId ->
                    navController.navigateToSingleRoutine(routineId)}
            )
        }
        composable(route = Overview.route) {
            OverviewScreen(
                onClickSeeAllAccounts = {
                    navController.navigateSingleTopTo(Accounts.route)
                },
                onAccountClick = { accountType ->
                    navController.navigateToSingleAccount(accountType)
                },
            )
        }
        composable(route = Accounts.route) {
            AccountsScreen(
//                跳转单个账户
                onAccountClick = { accountType ->
                    navController.navigateToSingleAccount(accountType)
                }
            )
        }

//            new Routine Entry
        composable(
            route = RoutineEntryDestination.route,
        ) {
                backStackEntry ->
            val backlogId = backStackEntry.arguments?.getInt("backlogId") ?: 0
            RoutineEntryScreen(
                backlogId =backlogId,
                navigateBack= { navController.navigateUp() }
            )
            
        }
//        Single Routine
        composable(
            route = SingleRoutineDestination.route,
        ) {
            SingleRoutineScreen(
                navigateBack= { navController.popBackStack() },
            )
        }
        
        composable(route = SingleAccount.routeWithArgs, arguments = SingleAccount.arguments, deepLinks = SingleAccount.deepLinks) { navBackStackEntry ->
            val accountType =
                navBackStackEntry.arguments?.getString(SingleAccount.accountTypeArg)
            SingleAccountScreen(accountType)
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }

private fun NavHostController.navigateToNewRoutine(backlogType: Int) {
    this.navigateSingleTopTo("${RoutineEntryDestination.route}/$backlogType")
}

private fun NavHostController.navigateToSingleRoutine(routineType: Int) {
    this.navigateSingleTopTo("${SingleRoutineDestination.route}/$routineType")
}
private fun NavHostController.navigateToSingleBacklog(backlogType: Int) {
    this.navigateSingleTopTo("${SingleBacklogDestination.route}/$backlogType")
}

private fun NavHostController.navigateToSingleAccount(accountType: String) {
    this.navigateSingleTopTo("${SingleAccount.route}/$accountType")
}






