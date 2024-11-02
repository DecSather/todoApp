package com.example.compose.rally.ui.routine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.rally.data.RoutinesRepository
import com.example.compose.rally.data.Routine
import com.example.compose.rally.ui.backlog.BacklogDetailsUiState
import com.example.compose.rally.ui.backlog.SingleBacklogDestination
import com.example.compose.rally.ui.backlog.SingleBacklogViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * AddSingleRoutine
 */
class SingleRoutineViewModel(
    savedStateHandle: SavedStateHandle,
    private val routinesRepository: RoutinesRepository) : ViewModel() {
    var routineUiState by mutableStateOf(RoutineUiState())
        private set
    
    private val routineId: Int = checkNotNull(savedStateHandle[SingleRoutineDestination.routineIdArg])
    init {
        viewModelScope.launch {
            routineUiState=routinesRepository.getRoutineStream(routineId)
                .filterNotNull()
                .first()
                .toRoutineUiState(true)
        }
    }
    
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    suspend fun updateRoutine() {
        if (validateInput(routineUiState.routine)) {
            routinesRepository.updateRoutine(routineUiState.routine)
        }
    }
    
    fun updateUiState(routine: Routine) {
        routineUiState=
            RoutineUiState(routine=routine, isEntryValid = validateInput(routine))
        
        
    }
    suspend fun deleteRoutineById(id:Int) {
        routinesRepository.deleteRoutineById(id)
    }
    private fun validateInput(uiState: Routine = routineUiState.routine): Boolean {
        return with(uiState) {
            content.isNotBlank() &&rank>=0 && credit>0.0
        }
    }
    
    
}
