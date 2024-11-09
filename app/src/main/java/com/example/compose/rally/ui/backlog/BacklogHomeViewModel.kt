
package com.example.compose.rally.ui.backlog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.rally.data.Backlog
import com.example.compose.rally.data.BacklogsRepository
import com.example.compose.rally.data.Routine
import com.example.compose.rally.data.RoutinesRepository
import kotlinx.coroutines.flow.*

/**
 * BacklogHome
 *  StateFlow-热流
 *      热观察，共享数据，复杂但即时
 */
class BacklogHomeViewModel(
    private val backlogsRepository: BacklogsRepository,
    private val routinesRepository: RoutinesRepository
) : ViewModel() {

//    backlog insert方法
    val backlogUiState:StateFlow<BacklogHomeUiState> =
        backlogsRepository.getAllBacklogsStream().map {
            BacklogHomeUiState(it)
        } .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed((TIMEOUT_MILLIS)),
            initialValue = BacklogHomeUiState()
        )
//    routine read-only
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

data class BacklogHomeUiState(val backlogList: List<Backlog> = listOf())
data class RoutineHomeUiState(val routineList: List<Routine> = listOf())