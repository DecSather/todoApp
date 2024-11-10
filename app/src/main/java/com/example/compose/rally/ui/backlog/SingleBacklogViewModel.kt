
package com.example.compose.rally.ui.backlog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.rally.data.Backlog
import com.example.compose.rally.data.BacklogsRepository
import com.example.compose.rally.data.RoutinesRepository
import com.example.compose.rally.ui.routine.RoutineHomeUiState
import kotlinx.coroutines.flow.*
/*
* SingleBacklog
*   数据展示-或可考虑将删除键移到主页以简化交互逻辑
*/
class SingleBacklogViewModel(
    savedStateHandle: SavedStateHandle,
    private val backlogsRepository: BacklogsRepository,
    private val routinesRepository: RoutinesRepository
) : ViewModel() {
    private val backlogId: Int = checkNotNull(savedStateHandle[SingleBacklogDestination.backlogIdArg])
//    backlog delete
    val backlogUiState: StateFlow<BacklogUiState> =
        backlogsRepository.getBacklogStream(id = backlogId)
            .map { BacklogUiState(it?:Backlog(timeTitle = "")) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = BacklogUiState()
            )
    
//    routine update finished-热观察
    val routineUiState: StateFlow<RoutineHomeUiState> =
        routinesRepository.getRoutinesStreamByBacklogId(backlogId = backlogId).map { RoutineHomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = RoutineHomeUiState()
            )
    
    
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    suspend fun deleteBacklogById(id:Int) {
        backlogsRepository.deleteBacklogById(id)
        routinesRepository.deleteRoutineByBacklogId(id)
    }
    
    suspend fun onRoutineFinishedChange(routineId:Int,finished: Boolean){
        routinesRepository.updateFinished(routineId,finished)
    }
    
}
fun Backlog.toBacklogUiState():BacklogUiState=BacklogUiState(
    backlog = this
)
data class BacklogUiState(
    val backlog: Backlog=
        Backlog(timeTitle="")
)
