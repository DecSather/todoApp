package com.sather.todo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Backlog::class, Routine::class, Diary::class], version = 7, exportSchema = false)
abstract class BacklogDatabase : RoomDatabase() {
    abstract fun backlogDao(): BacklogDao
    abstract fun routineDao(): RoutineDao
    abstract fun diaryDao(): DiaryDao // 需要添加新的DAO接口
    
    companion object {
        @Volatile
        private var Instance: BacklogDatabase? = null
        
        fun getDatabase(context: Context): BacklogDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    BacklogDatabase::class.java,
                    "backlog_database"
                )
                    .addMigrations(MIGRATION_6_7) // 添加迁移策略
                    .fallbackToDestructiveMigration() // 保留作为后备方案
                    .build()
                    .also { Instance = it }
            }
        }
        
        // 定义从版本6到7的迁移
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 创建新的diaries表
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS diaries (
                        id INTEGER PRIMARY KEY NOT NULL,
                        timeTitle TEXT NOT NULL,
                        content TEXT NOT NULL
                    )
                """)
                // 如果有其他迁移逻辑也可以在这里添加
            }
        }
    }
}