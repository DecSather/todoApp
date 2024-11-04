
package com.example.compose.rally.ui.backlog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.rally.data.Backlog
import com.example.compose.rally.data.BacklogsRepository
import com.example.compose.rally.data.fromListToJson
import kotlinx.coroutines.flow.*

//SingleBacklog
class SingleBacklogViewModel(
    savedStateHandle: SavedStateHandle,
    private val backlogsRepository: BacklogsRepository
) : ViewModel() {
    private val backlogId: Int = checkNotNull(savedStateHandle[SingleBacklogDestination.backlogIdArg])
    
    val backlogUiState: StateFlow<BacklogUiState> =
        backlogsRepository.getBacklogStream(id = backlogId)
            .map { BacklogUiState(it?:Backlog(timeTitle = "", routineListJson = "")) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = BacklogUiState()
            )
    
    suspend fun deleteBacklogById(id:Int) {
        backlogsRepository.deleteBacklogById(id)
    }
    
}
fun Backlog.toBacklogUiState():BacklogUiState=BacklogUiState(
    backlog = this
)
data class BacklogUiState(
    val backlog: Backlog=
        Backlog(timeTitle="yyyy-MM-dd", routineListJson = fromListToJson(listOf(1,0)))
)
