//
//package com.example.compose.rally.ui.backlog
//
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.SavedStateHandle
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.compose.rally.data.Backlog
//import com.example.compose.rally.data.BacklogsRepository
//import com.example.compose.rally.data.Routine
//import com.example.compose.rally.data.RoutinesRepository
//import com.example.compose.rally.ui.routine.RoutineUiState
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//
///*
//* Backlog Entry
//*   频繁更新，数据查询仅为初始化
//*   routineUiState不绑定数据
//*/
//class BacklogEditViewModel(
//    private val backlogsRepository: BacklogsRepository,
//    private val routinesRepository: RoutinesRepository
//) : ViewModel() {
//
//
//    //    routineList 频繁查询
//    var routineHomeUiState:StateFlow<RoutineHomeUiState> =
//        routinesRepository.getRoutinesStreamByBacklogId(backlogId).map {
//            RoutineHomeUiState(it)
//        }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed((TIMEOUT_MILLIS)),
//                initialValue = RoutineHomeUiState()
//            )
//    companion object {
//        private const val TIMEOUT_MILLIS = 5_000L
//    }
//    init {
//        viewModelScope.launch {
//            backlogUiState=backlogsRepository.getBacklogStream(backlogId)
//                .filterNotNull()
//                .first()
//                .toBacklogUiState()
//        }
//    }
//
//}