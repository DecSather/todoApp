package com.sather.todo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface  DiaryDao {
    @Query("SELECT * from diaries WHERE id = :id")
    fun getDiary(id: Long?): Flow<Diary>
    
    @Query("""
        SELECT * FROM diaries
        WHERE timeTitle BETWEEN :yearMonth || '-01' AND :yearMonth || '-31'
        """)
    fun getDiariesByMonth(yearMonth: String): Flow<List<Diary>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item:Diary)
}