package com.sather.todo.ui.diary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sather.todo.data.DiariesRepository
import com.sather.todo.data.Diary
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SingleDiaryViewModel(
    savedStateHandle: SavedStateHandle,
    private val diariesRepository: DiariesRepository,
) : ViewModel() {
    private val diaryId: Long = checkNotNull(savedStateHandle[SingleDiaryDestination.diaryIdArg])
    val diaryUiState: StateFlow<DiaryUiState> =
        diariesRepository.getDiaryStream(id = diaryId)
            .map {
                if (it != null) {
                    println("get diary:${it.timeTitle}")
                }
                DiaryUiState(it?: Diary())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DiaryUiState()
            )
    
    suspend fun triggerSave(newContent:String) {
        println("single diary update:$newContent")
        diariesRepository.insertDiary(diaryUiState.value.diary.copy(content = newContent))
    }
    
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class DiaryUiState(
    val diary: Diary = Diary(timeTitle="")
)
