package com.sather.todo.data

import kotlinx.coroutines.flow.Flow

class DiariesRepository(
    private val diaryDao: DiaryDao,
    ) {
    fun getDiaryStream(id: Long): Flow<Diary?> = diaryDao.getDiary(id)
    
    fun getDiariesByMonth(yearMonth: String): Flow<List<Diary>> = diaryDao.getDiariesByMonth(yearMonth)
    
    suspend fun insertDiary(diary: Diary) = diaryDao.insert(diary)
}