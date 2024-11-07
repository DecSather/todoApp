package com.example.compose.rally.ui.routine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.rally.data.Backlog
import com.example.compose.rally.data.BacklogsRepository
import com.example.compose.rally.data.RoutinesRepository
import com.example.compose.rally.data.Routine
import com.example.compose.rally.ui.backlog.BacklogUiState
import com.example.compose.rally.ui.backlog.toBacklogUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * AddSingleRoutine
 */
class SingleRoutineViewModel(
    savedStateHandle: SavedStateHandle,
    private val routinesRepository: RoutinesRepository,
    private  val backlRepository: BacklogsRepository
) : ViewModel() {
    var routineUiState by mutableStateOf(RoutineUiState())
        private set
    var backlogUiState by mutableStateOf(BacklogUiState())
    private set
    
    private val routineId: Int = checkNotNull(savedStateHandle[SingleRoutineDestination.routineIdArg])
    init {
        viewModelScope.launch {
            routineUiState=routinesRepository.getRoutineStream(routineId)
                .filterNotNull()
                .first()
                .toRoutineUiState(true)
            backlogUiState= backlRepository.getBacklogStream(routineUiState.routine.backlogId)
                .filterNotNull()
                .first()
                .toBacklogUiState()
        }
    }
    
    suspend fun updateRoutine() {
        if (validateInput(routineUiState.routine)) {
            routinesRepository.updateRoutine(routineUiState.routine)
            backlRepository.updateBacklog(backlogUiState.backlog)
        }
    }
    
    fun updateRoutineUiState(routine: Routine) {
        routineUiState=
            RoutineUiState(routine=routine, isEntryValid = validateInput(routine))
    }
    fun updateBacklogUiState(backlog:Backlog) {
        backlogUiState=
            BacklogUiState(backlog=backlog)
    }
    private fun validateInput(uiState: Routine = routineUiState.routine): Boolean {
        return with(uiState) {
            content.isNotBlank() &&rank>=0 && credit>0.0
        }
    }
    
    
}
