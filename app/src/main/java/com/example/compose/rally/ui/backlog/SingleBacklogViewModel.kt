
package com.example.compose.rally.ui.backlog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.rally.data.Backlog
import com.example.compose.rally.data.BacklogsRepository
import com.example.compose.rally.data.fromListToJson
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SingleBacklogViewModel(
    savedStateHandle: SavedStateHandle,
    private val backlogsRepository: BacklogsRepository
) : ViewModel() {
    private val backlogIdArg: Int = checkNotNull(savedStateHandle[SingleBacklogDestination.backlogIdArg])
    
    val uiState: StateFlow<BacklogDetailsUiState> =
        backlogsRepository.getBacklogStream(backlogIdArg)
            .filterNotNull()
            .map {
                BacklogDetailsUiState(backlog = it)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = BacklogDetailsUiState()
            )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    suspend fun deleteBacklogById(id:Int) {
        backlogsRepository.deleteBacklogById(id)
    }
}

data class BacklogDetailsUiState(
    val backlog: Backlog=
        Backlog(timeTitle="yyyy-MM-dd", routineListJson = fromListToJson(listOf(1,0)))
)
