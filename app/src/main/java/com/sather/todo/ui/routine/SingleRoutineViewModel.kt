package com.sather.todo.ui.routine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sather.todo.data.Routine
import com.sather.todo.data.RoutinesRepository
import com.sather.todo.ui.navigation.SingleRoutineDestination
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * AddSingleRoutine
 */
class SingleRoutineViewModel(
    savedStateHandle: SavedStateHandle,
    private val routinesRepository: RoutinesRepository
) : ViewModel() {
    
    private val routineId: Long = checkNotNull(savedStateHandle[SingleRoutineDestination.routineIdArg])
    var routineUiState by mutableStateOf(RoutineUiState())
        private set
    init {
        viewModelScope.launch {
            routineUiState=routinesRepository.getRoutineStream(routineId)
                .filterNotNull()
                .first()
                .toRoutineUiState()
        }
    }
    
    suspend fun updateRoutine() {
        if (validateInput(routineUiState.routine)) {
            routinesRepository.updateRoutine(routineUiState.routine)
        }else{
            routinesRepository.deleteRoutineById(routineId)
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

data class RoutineUiState(
    val routine: Routine = Routine(
        backlogId=0,
        content="",
        sortId = 0,
    ),
    val isEntryValid: Boolean = false
)

fun Routine.toRoutineUiState(): RoutineUiState = RoutineUiState(
    routine = this,
    isEntryValid = this.content.isNotBlank() &&this.rank>=0 && this.credit>0.0
)

fun formatedCredit(creditText:String):String{
    val df = DecimalFormat("#.0")
    df.roundingMode = RoundingMode.HALF_UP
    var formatted: String = df.format(creditText.toFloat())
    if(formatted.equals(".0")) formatted="0.0"
    return formatted
}