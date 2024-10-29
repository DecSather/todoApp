
package com.example.compose.rally.ui.backlog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.rally.data.Backlog
import com.example.compose.rally.data.BacklogsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to retrieve all items in the Room database.
 */
class BacklogHomeViewModel(private val backlogsRepository: BacklogsRepository) : ViewModel() {

    /**
     * Holds home ui state. The list of items are retrieved from [ItemsRepository] and mapped to
     * [HomeUiState]
     */
    val homeUiState: StateFlow<HomeUiState> =
        backlogsRepository.getAllBacklogsStream().map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    
    suspend fun newCurrentBacklog(timeTitle:String) {
        backlogsRepository.insertBacklog(
            Backlog(
            timeTitle =timeTitle,
                routineListJson = "{}"
            )
        )
    }
    suspend fun deleteBacklogById(id:Int) {
        backlogsRepository.deleteBacklogById(id)
    }
}

/**
 * Ui State for HomeScreen
 */
data class HomeUiState(val backlogList: List<Backlog> = listOf())
