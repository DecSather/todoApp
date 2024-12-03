package com.sather.todo.ui.routine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sather.todo.data.RoutinesRepository
import com.sather.todo.data.Routine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * AddSingleRoutine
 */
class SingleRoutineViewModel(
    savedStateHandle: SavedStateHandle,
    private val routinesRepository: RoutinesRepository
) : ViewModel() {
    
    private val routineId: Int = checkNotNull(savedStateHandle[SingleRoutineDestination.routineIdArg])
    var routineUiState by mutableStateOf(RoutineUiState())
        private set
    init {
        viewModelScope.launch {
            routineUiState=routinesRepository.getRoutineStream(routineId)
                .filterNotNull()
                .first()
                .toRoutineUiState(true)
        }
    }
    
    suspend fun updateRoutine() {
        if (validateInput(routineUiState.routine)) {
            routinesRepository.updateRoutine(routineUiState.routine)
        }
    }
    
    fun updateRoutineUiState(routine: Routine) {
        routineUiState=
            RoutineUiState(routine=routine, isEntryValid = validateInput(routine))
    }
    private fun validateInput(uiState: Routine = routineUiState.routine): Boolean {
        return with(uiState) {
            content.isNotBlank() &&rank>=0 && credit>0.0
        }
    }
    
    
}
