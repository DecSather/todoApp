package com.sather.todo.ui.diary

import androidx.lifecycle.ViewModel
import com.sather.todo.data.DiariesRepository
import com.sather.todo.data.Diary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class DiaryHomeViewModel (
    private val diariesRepository: DiariesRepository,
) : ViewModel() {
    
    //    private var currentMonthKey: String? = null
    private val _currentMonthDiaries = MutableStateFlow<Map<String, Diary>>(emptyMap())
    val currentMonthDiaries: StateFlow<Map<String, Diary>> = _currentMonthDiaries
    
    // 加载月份数据（支持强制刷新）
    suspend fun loadMonthDiaries(yearMonth:String) {
         diariesRepository.getDiariesByMonth(yearMonth)
            .map { diaries -> diaries.associateBy { it.timeTitle } }
            .collect {
                _currentMonthDiaries.value = it
                println("loading diary：$yearMonth")
            }
    }
    
    
    suspend fun insertDiary(diary: Diary){
        diariesRepository.insertDiary(diary)
    }
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}
data class DiaryHomeUiState(val diaryList: List<Diary> = listOf())