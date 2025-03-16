package com.sather.todo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Backlog::class, Routine::class], version =6, exportSchema = false)
abstract class BacklogDatabase : RoomDatabase()  {
    abstract fun backlogDao(): BacklogDao
    abstract fun routineDao(): RoutineDao
    companion object {
        @Volatile
        private var Instance: BacklogDatabase?=null
        fun getDatabase(context: Context): BacklogDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    BacklogDatabase::class.java,
                    "backlog_database"
                )
                    .fallbackToDestructiveMigration() // 如果迁移失败，则销毁并重建数据库
                    .build()
                    .also { Instance = it }
            }
        }
    }
}