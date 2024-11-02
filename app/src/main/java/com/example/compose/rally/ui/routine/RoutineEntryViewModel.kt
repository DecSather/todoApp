package com.example.compose.rally.ui.routine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.compose.rally.data.RoutinesRepository
import com.example.compose.rally.data.Routine

/**
 * AddRoutineEntry
 */
class RoutineEntryViewModel(private val routinesRepository: RoutinesRepository) : ViewModel() {
    
    var routineUiState by mutableStateOf(RoutineUiState())
        private set
    suspend fun inseetRoutine() {
        if (validateInput(routineUiState.routine)) {
            routinesRepository.insertRoutine(routineUiState.routine)
        }
    }
    
    fun updateUiState(routine: Routine) {
        routineUiState =
            RoutineUiState(routine = routine, isEntryValid = validateInput(routine))
    }
    private fun validateInput(uiState: Routine = routineUiState.routine): Boolean {
        return with(uiState) {
            content.isNotBlank() &&rank>=0 && credit>0.0
        }
    }
    
    
}

data class RoutineUiState(
    val routine: Routine = Routine(
        backlogId=0,
        content=""
    ),
    val isEntryValid: Boolean = false
)
fun Routine.toRoutineUiState(isEntryValid: Boolean = false): RoutineUiState = RoutineUiState(
    routine = this,
    isEntryValid = isEntryValid
)
