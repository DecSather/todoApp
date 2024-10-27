package com.example.compose.rally.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.compose.rally.ui.accounts.AccountsScreen
import com.example.compose.rally.ui.accounts.SingleAccountScreen
import com.example.compose.rally.ui.backlog.BacklogHomeScreen
import com.example.compose.rally.ui.backlog.SingleBacklogScreen
import com.example.compose.rally.ui.overview.OverviewScreen

@Composable
fun RallyNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Backlogs.route,
        modifier = modifier
    ) {
        composable(route = Backlogs.route) {
            BacklogHomeScreen(
//                跳转单个日志
                onBacklogClick = {backlogType ->
                    navController.navigateToSingleBacklog(backlogType)
                },
//                跳转单个待办-待实现
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
        
        composable(
            route = SingleBacklog.routeWithArgs,
            arguments = SingleBacklog.arguments,
            deepLinks = SingleBacklog.deepLinks
        ) {
            navBackStackEntry ->
            val backlogType =
                navBackStackEntry.arguments?.getString(SingleBacklog.backlogTypeArg)
            println(backlogType)
            
            SingleBacklogScreen(backlogType=backlogType)
        }
        composable(
            route = SingleAccount.routeWithArgs,
            arguments = SingleAccount.arguments,
            deepLinks = SingleAccount.deepLinks
        ) { navBackStackEntry ->
            val accountType =
                navBackStackEntry.arguments?.getString(SingleAccount.accountTypeArg)
            println(accountType)
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

private fun NavHostController.navigateToSingleBacklog(backlogType: String) {
    this.navigateSingleTopTo("${SingleBacklog.route}/$backlogType")
}

private fun NavHostController.navigateToSingleAccount(accountType: String) {
    this.navigateSingleTopTo("${SingleAccount.route}/$accountType")
}






