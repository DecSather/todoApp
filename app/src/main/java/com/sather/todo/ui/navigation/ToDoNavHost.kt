package com.sather.todo.ui.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sather.todo.ui.backlog.BacklogHomeScreen
import com.sather.todo.ui.backlog.SingleBacklogScreen
import com.sather.todo.ui.comesoon.ComeSoonScreen
import com.sather.todo.ui.routine.SingleRoutineScreen

//waiting implement:页面转化的过渡



@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ToDoNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    SharedTransitionLayout{
        NavHost(
            navController = navController,
            startDestination = BacklogHome.route,
            modifier = modifier
        ) {
//            Backlog Home
            composable(route = BacklogHome.route) {
                BacklogHomeScreen(
                    this@SharedTransitionLayout,
                    this@composable,
                    onBacklogDetailClick = {
                        navController.navigate("${SingleBacklogDestination.route}/${it}")
                    }
                )
            }

//        Single Backlog
            composable(
                route = SingleBacklogDestination.routeWithArgs,
                arguments = SingleBacklogDestination.arguments,
//                动画效果-淡入
                enterTransition = {
                    slideInVertically() + fadeIn(
                        animationSpec = tween(2000),
                    )
                },
            ){
                SingleBacklogScreen(
                    this@SharedTransitionLayout,
                    this@composable,
                    navigateBack= { navController.popBackStack() },
//                跳转指定待办
                    navigateToSingleRoutine ={it ->
                        navController.navigate("${SingleRoutineDestination.route}/${it}")
                    }
                )
            }
            
            composable(route = ComeSoon.route) {
                ComeSoonScreen()
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




