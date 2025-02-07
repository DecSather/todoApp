package com.sather.todo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Backlog::class, Routine::class], version =5, exportSchema = false)
abstract class BacklogDatabase : RoomDatabase()  {
    abstract fun backlogDao(): BacklogDao
    abstract fun routineDao(): RoutineDao
    companion object {
        @Volatile
        private var Instance: BacklogDatabase?=null
        
        fun getDatabase(context: Context): BacklogDatabase {
            // synchronized-避免出现竞态条件
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context,
                    BacklogDatabase::class.java,"backlog_database")
//                        迁移策略-销毁并重建数据库
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}