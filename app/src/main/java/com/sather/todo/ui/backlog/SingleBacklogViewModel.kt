
package com.sather.todo.ui.backlog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sather.todo.data.Backlog
import com.sather.todo.data.BacklogsRepository
import com.sather.todo.data.Routine
import com.sather.todo.data.RoutinesRepository
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
    private val backlogId: Long = checkNotNull(savedStateHandle[SingleBacklogDestination.backlogIdArg])
//    backlog delete-热观察
    val backlogUiState: StateFlow<BacklogUiState> =
        backlogsRepository.getBacklogStream(id = backlogId)
            .map {
                BacklogUiState(it?: Backlog())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = BacklogUiState()
            )
    suspend fun deleteBacklogById(id:Long) {
        backlogsRepository.deleteBacklogById(id)
        routinesRepository.deleteRoutineByBacklogId(id)
    }
    
    
    //    routine updateFinished deleteById-热观察
    val routineUiState: StateFlow<RoutineHomeUiState> =
        routinesRepository.getRoutinesStreamByBacklogId(backlogId = backlogId).map {
            RoutineHomeUiState(it)
        }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = RoutineHomeUiState()
            )
    
    
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    suspend fun updateFinished(routineId:Long,finished:Boolean){
        routinesRepository.updateFinished(routineId,finished)
    }
    suspend fun updateSort(sortedList: List<Routine>) {
        sortedList.forEachIndexed{ index,it ->
                routinesRepository.updateRoutine(it.copy(sortId = index))
        }
    }
    suspend fun deleteRoutineById(id:Long) {
        routinesRepository.deleteRoutineById(id)
    }
    suspend fun addRoutine(routine:Routine) {
        routinesRepository.insertRoutine(routine)
    }
}
data class BacklogUiState(
    val backlog: Backlog =
        Backlog(timeTitle="")
)
