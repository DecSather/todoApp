//package com.example.compose.rally.ui.backlog
//
//@Composable
//fun BacklogHome(
//    backlogViewModel: BacklogViewModel = viewModel()
//) {
//    val backlogs: List<Backlog> by backlogViewModel.backlogs.observeAsState(emptyList())
//    // 创建一个映射来存储每个BacklogID对应的RoutineList加载状态
//    val routineListStates = remember { mutableMapOf<Int, RoutineListState>() }
//
//    LazyColumn {
//        items(backlogs) { backlog ->
//            // 为每个Backlog创建一个key来唯一标识它
//            val backlogKey = backlog.id
//
//            // 检查是否已经为这个Backlog请求过RoutineList
//            val routineListState = routineListStates[backlogKey] ?: RoutineListState.Loading
//
//            // 根据加载状态显示不同的UI
//            when (routineListState) {
//                is RoutineListState.Loading -> {
//                    // 显示加载中的UI
//                    LoadingIndicator()
//
//                    // 发起数据请求（这里使用LaunchedEffect来确保只在组合时执行一次）
//                    LaunchedEffect(backlogKey) {
//                        // 请求数据并更新状态
//                        val result = backlogViewModel.getRoutineListForBacklog(backlogKey)
//                        routineListStates[backlogKey] =
//                            if (result.isSuccess) {
//                                RoutineListState.Success(result.data!!)
//                            } else {
//                                RoutineListState.Error(result.error!!)
//                            }
//                    }
//                }
//                is RoutineListState.Success -> {
//                    // 显示加载成功的UI，并传入RoutineList数据
//                    BacklogCard(backlog = backlog, routineList = routineListState.routineList)
//                }
//                is RoutineListState.Error -> {
//                    // 显示加载失败的UI，并提供重试选项
//                    ErrorMessage(message = routineListState.errorMessage) {
//                        // 重试请求
//                        LaunchedEffect(backlogKey) {
//                            val result = backlogViewModel.getRoutineListForBacklog(backlogKey)
//                            routineListStates[backlogKey] =
//                                if (result.isSuccess) {
//                                    RoutineListState.Success(result.data!!)
//                                } else {
//                                    RoutineListState.Error(result.error!!)
//                                }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//// 定义一个密封类来表示RoutineList的加载状态
//sealed class RoutineListState {
//    object Loading : RoutineListState()
//    data class Success(val routineList: List<Routine>) : RoutineListState()
//    data class Error(val errorMessage: String) : RoutineListState()
//}
