package com.example.compose.rally.ui.routine

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.rally.data.Backlog
import com.example.compose.rally.data.Routine
import com.example.compose.rally.data.RoutinesRepository
import com.example.compose.rally.ui.backlog.SingleBacklogDestination
import kotlinx.coroutines.flow.*

class RoutineHomeViewModel(
    savedStateHandle: SavedStateHandle,
    private val routinesRepository: RoutinesRepository) : ViewModel() {
    private val backlogId: Int = checkNotNull(savedStateHandle[SingleBacklogDestination.backlogIdArg])
    val homeUiState: StateFlow<RoutineHomeUiState> =
        routinesRepository.getRoutinesStreamByBacklogId(backlogId = backlogId).map { RoutineHomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = RoutineHomeUiState()
            )

    suspend fun insertRoutine(
        content: String,
        finished: Boolean =false,
        subcontent:String="",
        credit: Float =0f,
        rank:Int=1,
        ) {
        routinesRepository.insertRoutine(
            Routine(
                backlogId = backlogId,
                content = content,
                finished = finished,
                subcontent = subcontent,
                credit = credit,
                rank = rank
            )
        )
    }
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    
}

/**
 * Ui State for HomeScreen
 */
data class RoutineHomeUiState(val routineList: List<Routine> = listOf())
