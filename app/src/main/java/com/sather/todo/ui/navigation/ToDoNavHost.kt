package com.sather.todo.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddHome
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sather.todo.ui.backlog.BacklogHomeScreen
import com.sather.todo.ui.backlog.SingleBacklogDestination
import com.sather.todo.ui.backlog.SingleBacklogScreen
import com.sather.todo.ui.comesoon.ComeSoonScreen
import com.sather.todo.ui.components.TopTabRow
import com.sather.todo.ui.diary.DiaryHomeScreen
import com.sather.todo.ui.diary.SingleDiaryDestination
import com.sather.todo.ui.diary.SingleDiaryScreen
import com.sather.todo.ui.routine.SingleRoutineDestination
import com.sather.todo.ui.routine.SingleRoutineScreen
import kotlinx.coroutines.launch


//waiting implement:页面转化的过渡


interface BaseDestination {
    val icon: ImageVector
    val route: String
}
// 首先定义所有目的地
sealed class MainScreen(
    val icon: ImageVector = Icons.Filled.AddTask
    ) {
    object BacklogHome : MainScreen(
        icon = Icons.Filled.AddTask
    )
    
    object DiaryHome : MainScreen(
        icon = Icons.Filled.EditNote
    )
    object ComeSoon : MainScreen(
        icon = Icons.Filled.AddHome
    )
    
    companion object {
        val allScreens = listOf(BacklogHome, DiaryHome, ComeSoon)
        const val defaultRoute = "main"
    }
}

@Composable
fun ToDoNavHost(
    navController: NavHostController,
) {
    val coroutineScope = rememberCoroutineScope()
    // 添加pagerState作为局部状态管理
    val pagerState = rememberPagerState(initialPage = 0) {
        MainScreen.allScreens.size
    }
    
    // 当前屏幕状态
    val currentScreen by remember {
        derivedStateOf {
            MainScreen.allScreens[pagerState.currentPage]
        }
    }
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
//            导航栏样式
            topBar = {
                TopTabRow(
                    onTabSelected = { screen ->
                        // 点击导航栏时滑动到对应页面
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(MainScreen.allScreens.indexOf(screen))
                        }
                    },
                    currentScreen = currentScreen
                )
            }
        ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainScreen.defaultRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 主屏幕-水平滑动
            composable(MainScreen.defaultRoute) {
                HorizontalPager(state = pagerState) { page ->
                    when (MainScreen.allScreens[page]) {
//            Backlog Home
                        MainScreen.BacklogHome -> {
                                BacklogHomeScreen(
                                    onBacklogDetailClick = {
                                        navController.navigate("${SingleBacklogDestination.route}/${it}")
                                    }
                                )
                            }
//            Diary Home
                        MainScreen.DiaryHome -> {
                                DiaryHomeScreen(
                                    onDiaryDetailClick = {
                                        navController.navigate("${SingleDiaryDestination.route}/${it}")
                                    }
                                )
                            }
                        MainScreen.ComeSoon -> {
                                ComeSoonScreen()
                        }
                    }
                }
            }

//        Single Diary
            composable(
                route = SingleDiaryDestination.routeWithArgs,
                arguments = SingleDiaryDestination.arguments,
//                动画效果-下滑进入，上滑离开
                enterTransition = {
                    slideIntoContainer(
                        towards =  AnimatedContentTransitionScope.SlideDirection.Down ,
                        animationSpec = tween(durationMillis = 300)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards =  AnimatedContentTransitionScope.SlideDirection.Up ,
                         animationSpec = tween(durationMillis = 300)
                    )
                }
            ){
                SingleDiaryScreen(
                    navigateBack= { navController.popBackStack() },
                    
                )
            }

//        Single Backlog
            composable(
                route = SingleBacklogDestination.routeWithArgs,
                arguments = SingleBacklogDestination.arguments,
//                动画效果-下滑进入
                enterTransition = {
                    slideIntoContainer(
                        towards =  AnimatedContentTransitionScope.SlideDirection.Down ,
                        animationSpec = tween(durationMillis = 300)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards =  AnimatedContentTransitionScope.SlideDirection.Up ,
                        animationSpec = tween(durationMillis = 300)
                    )
                }
            ){
                SingleBacklogScreen(
                    navigateBack= { navController.popBackStack() },
//                跳转指定待办
                    navigateToSingleRoutine ={it ->
                        navController.navigate("${SingleRoutineDestination.route}/${it}")
                    }
                )
            }
            
//        Single Routine
            composable(
                route = SingleRoutineDestination.routeWithArgs,
                arguments = SingleRoutineDestination.arguments,
//                动画效果-下滑进入，上滑离开
                enterTransition = {
                    slideIntoContainer(
                        towards =  AnimatedContentTransitionScope.SlideDirection.Down ,
                        animationSpec = tween(durationMillis = 300)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards =  AnimatedContentTransitionScope.SlideDirection.Up ,
                        animationSpec = tween(durationMillis = 300)
                    )
                }
            ) {
                SingleRoutineScreen(
                    navigateBack= { navController.popBackStack() },
                )
            }
        }
        
    }
}




