
package com.sather.todo.ui.backlog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sather.todo.data.Backlog
import com.sather.todo.data.BacklogsRepository
import com.sather.todo.data.Routine
import com.sather.todo.data.RoutinesRepository
import com.sather.todo.ui.routine.RoutineUiState
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

//    backlog home 热观察，insert,delete
    val backlogHomeUiState:StateFlow<BacklogHomeUiState> =
        backlogsRepository.getAllBacklogsStream().map {
            BacklogHomeUiState(it)
        } .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed((TIMEOUT_MILLIS)),
            initialValue = BacklogHomeUiState()
        )
    
    suspend fun onExpandChange(id:Int,isExpand:Boolean) {
        backlogsRepository.onExpandChange(id,isExpand)
    }
    suspend fun addBacklog(timeTitle:String):Int{
        return backlogsRepository.insertBacklog(
            Backlog(
                timeTitle =timeTitle
            )
        ).toInt()
    }
    suspend fun deleteBacklogById(id:Int) {
        backlogsRepository.deleteBacklogById(id)
        routinesRepository.deleteRoutineByBacklogId(id)
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
    
    suspend fun onRoutineFinishedChange(routineId:Int,finished: Boolean){
        routinesRepository.updateFinished(routineId,finished)
    }
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

//    backlog edit 单次单个频繁更新
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

//    RoutineUiState edit/empty
    suspend fun updateRoutine(routine: Routine) {
        if (validateInput(routine)) {
            routinesRepository.updateRoutine(routine)
            println("update routine: " + routine)
        }else{
            println("update routine failed and delete now: " + routine)
            routinesRepository.deleteRoutineById(routine.id)
        }
    }
    suspend fun insertRoutine(routine: Routine):Int {
        if(validateInput(routine)){
            println("insert routine: " + routine)
            return routinesRepository.insertRoutine(routine.copy(id = 0,rank = 1)).toInt()
        }else {
            println("insert routine failed: " + routine)
            return -1
        }
    }
    
    private fun validateInput(uiState: Routine): Boolean {
        return with(uiState) {
            content.isNotBlank() &&rank>=0 && credit>0.0
        }
    }
}


data class BacklogHomeUiState(val backlogList: List<Backlog> = listOf())
data class RoutineHomeUiState(val routineList: List<Routine> = listOf())
