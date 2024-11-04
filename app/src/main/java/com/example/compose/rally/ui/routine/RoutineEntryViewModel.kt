package com.example.compose.rally.ui.routine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.compose.rally.data.Backlog
import com.example.compose.rally.data.BacklogsRepository
import com.example.compose.rally.data.Routine
import com.example.compose.rally.data.RoutinesRepository
import com.example.compose.rally.ui.backlog.BacklogUiState
import com.example.compose.rally.ui.backlog.toBacklogUiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import java.math.RoundingMode
import java.text.DecimalFormat


/**
 * AddRoutineEntry
 */
class RoutineEntryViewModel(
    private val routinesRepository: RoutinesRepository,
    private  val backlRepository: BacklogsRepository
) : ViewModel() {
    
    var routineUiState by mutableStateOf(RoutineUiState())
        private set
    var backlogUiState by mutableStateOf(BacklogUiState())
        private set
    suspend fun inseetRoutine() {
        if (validateInput(routineUiState.routine)) {
            routinesRepository.insertRoutine(routineUiState.routine)
            backlRepository.updateBacklog(backlogUiState.backlog)
        }
    }
    
    fun updateRoutineUiState(routine: Routine) {
        routineUiState =
            RoutineUiState(routine = routine, isEntryValid = validateInput(routine))
    }
    
    suspend fun initBacklogUiState(){
        backlogUiState=backlRepository.getBacklogStream(routineUiState.routine.backlogId)
            .filterNotNull()
            .first()
            .toBacklogUiState()
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

fun formatedCredit(creditText:String):String{
    val df = DecimalFormat("#.0")
    df.roundingMode = RoundingMode.HALF_UP
    var formatted: String = df.format(creditText.toFloat())
    if(formatted.equals(".0")) formatted="0.0"
    return formatted
}