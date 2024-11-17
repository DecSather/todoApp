
package com.example.compose.rally.ui.backlog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.rally.data.Backlog
import com.example.compose.rally.data.BacklogsRepository
import com.example.compose.rally.data.Routine
import com.example.compose.rally.data.RoutinesRepository
import com.example.compose.rally.ui.routine.RoutineUiState
import kotlinx.coroutines.flow.*

/**
 * BacklogHome
 *  StateFlow-热流
 *      热观察，共享数据，复杂但即时
 */
class BacklogHomeViewModel(
    private val backlogsRepository: BacklogsRepository,
    private val routinesRepository: RoutinesRepository
) : ViewModel() {

//    backlog home 热观察，insert方法
    val backlogHomeUiState:StateFlow<BacklogHomeUiState> =
        backlogsRepository.getAllBacklogsStream().map {
            BacklogHomeUiState(it)
        } .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed((TIMEOUT_MILLIS)),
            initialValue = BacklogHomeUiState()
        )
    
    suspend fun newCurrentBacklog(timeTitle:String):Int{
        return backlogsRepository.insertBacklog(
            Backlog(
                timeTitle =timeTitle
            )
        ).toInt()
    }
//    routine home 热观察
    val routineHomeUiState:StateFlow<RoutineHomeUiState> =
        routinesRepository.getAllRoutinesStream().map {
            RoutineHomeUiState(it)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed((TIMEOUT_MILLIS)),
                initialValue = RoutineHomeUiState()
            )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

//    backlog state 单次单个频繁更新
    var backlogUiState by mutableStateOf(BacklogUiState())

    fun updatBacklogUiState(backlog: Backlog) {
        backlogUiState =
            BacklogUiState(backlog = backlog)
    }
    suspend fun updateBacklog() {
        if(backlogUiState.backlog.timeTitle.isNotEmpty()) {
            backlogsRepository.updateBacklog(backlogUiState.backlog)
        }
    }

//    routine state 单次单个频繁更新
    var routineUiState  by mutableStateOf(RoutineUiState())
    private set
    suspend fun inseetRoutine() {
        if (validateInput(routineUiState.routine)) {
            routinesRepository.insertRoutine(routineUiState.routine)
        }
    }

    fun updateRoutineUiState(routine: Routine) {
        routineUiState =
            RoutineUiState(routine = routine, isEntryValid = validateInput(routine))
    }
    suspend fun updateRoutine() {
        if (validateInput(routineUiState.routine)) {
            routinesRepository.updateRoutine(routineUiState.routine)
        }
    }

    private fun validateInput(uiState: Routine = routineUiState.routine): Boolean {
        return with(uiState) {
            content.isNotBlank() &&rank>=0 && credit>0.0
        }
    }
}


data class BacklogHomeUiState(val backlogList: List<Backlog> = listOf())
data class RoutineHomeUiState(val routineList: List<Routine> = listOf())