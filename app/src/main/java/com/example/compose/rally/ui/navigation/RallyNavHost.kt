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
//waiting implement:页面转化的过渡
//      https://codelabs.developers.google.cn/codelabs/material-motion-android?hl=zh_cn#3
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
            arguments = SingleBacklogDestination.arguments
        ){
            val baklogId=it.arguments?.getInt("backlogId")
            SingleBacklogScreen(
                navigateBack= { navController.popBackStack() },
//                跳转新增待办
                navigateToNewRoutine = {navController.navigate("${RoutineEntryDestination.route}/${baklogId}")},
//                跳转指定待办
                navigateToSingleRoutine ={navController.navigate("${SingleRoutineDestination.route}/${it}")}
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

//            Entry new Routine
        composable(
            route = RoutineEntryDestination.routeWithArgs,
            arguments = RoutineEntryDestination.arguments
        ) {
            val backlogId=it.arguments?.getInt("backlogId")?:0
            RoutineEntryScreen(
                backlogId =backlogId,
                navigateBack= { navController.popBackStack() }
            )
            
        }
//        Single Routine
        composable(
            route = SingleRoutineDestination.routeWithArgs,
            arguments = SingleRoutineDestination.arguments
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


private fun NavHostController.navigateToSingleAccount(accountType: String) {
    this.navigateSingleTopTo("${SingleAccount.route}/$accountType")
}






