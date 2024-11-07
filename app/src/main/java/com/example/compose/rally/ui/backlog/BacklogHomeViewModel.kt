
package com.example.compose.rally.ui.backlog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.rally.data.Backlog
import com.example.compose.rally.data.BacklogsRepository
import com.example.compose.rally.data.Routine
import com.example.compose.rally.data.RoutinesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * BacklogHome
 */
class BacklogHomeViewModel(
    private val backlogsRepository: BacklogsRepository,
    private val routinesRepository: RoutinesRepository
) : ViewModel() {

    val homeUiState:StateFlow<BacklogHomeUiState> =
        backlogsRepository.getAllBacklogsStream().map {
            BacklogHomeUiState(it)
        } .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed((TIMEOUT_MILLIS)),
            initialValue = BacklogHomeUiState()
        )
    
    val routineUiState:StateFlow<RoutineHomeUiState> =
        routinesRepository.getAllRoutinesStream().map {
            RoutineHomeUiState(it)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed((TIMEOUT_MILLIS)),
                initialValue = RoutineHomeUiState()
            )
    
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    
    suspend fun newCurrentBacklog(timeTitle:String):Int{
        return backlogsRepository.insertBacklog(
            Backlog(
                timeTitle =timeTitle
            )
        ).toInt()
    }
    
}

/**
 * Ui State for HomeScreen
 */
data class BacklogHomeUiState(val backlogList: List<Backlog> = listOf())
data class RoutineHomeUiState(val routineList: List<Routine> = listOf())