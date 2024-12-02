package com.example.compose.sather

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.compose.sather.ui.backlog.BacklogHome
import com.example.compose.sather.ui.backlog.SingleBacklogDestination
import com.example.compose.sather.ui.components.TopTabRow
import com.example.compose.sather.ui.navigation.*
import com.example.compose.sather.ui.comesoon.ComeSoon
import com.example.compose.sather.ui.theme.ToDoTheme
// Screens to be displayed in the top RallyTabRow
val rallyTabRowScreens = listOf(BacklogHome, ComeSoon)
@Composable
fun ToDoApp(navController: NavHostController = rememberNavController()) {
    ToDoNavHost(navController = navController)
    ToDoTheme {
//        判断当前页面展开标题
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination
        val currentScreen = rallyTabRowScreens.find {
            it.route == currentDestination?.route
                    ||it == SingleBacklogDestination
                    || (it == ComeSoon && currentDestination?.route?.startsWith( ComeSoon.route ) == true)
        } ?: BacklogHome
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
//            导航栏样式
            topBar = {
                TopTabRow(
                    allScreens = rallyTabRowScreens,
                    onTabSelected = { newScreen ->
                        navController.navigateSingleTopTo(newScreen.route)
                    },
                    currentScreen= currentScreen
                )
            }
        ) { innerPadding ->
            ToDoNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
