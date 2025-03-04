package com.sather.todo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Backlog::class, Routine::class], version =5, exportSchema = false)
abstract class BacklogDatabase : RoomDatabase()  {
    abstract fun backlogDao(): BacklogDao
    abstract fun routineDao(): RoutineDao
    companion object {
        @Volatile
        private var Instance: BacklogDatabase?=null
        
        fun getDatabase(context: Context): BacklogDatabase {
//            抄的示例，没用过，万一改版本再学
            val MIGRATION_5_6 = object : Migration(5,6) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    // 1. 创建临时表
                    database.execSQL("CREATE TABLE your_table_temp (id INTEGER PRIMARY KEY, new_column TEXT)")
                    
                    // 2. 将数据从旧表复制到临时表
                    database.execSQL("INSERT INTO your_table_temp (id, new_column) SELECT id, existing_column FROM your_table")
                    
                    // 3. 删除旧表
                    database.execSQL("DROP TABLE your_table")
                    
                    // 4. 重命名临时表为新表
                    database.execSQL("ALTER TABLE your_table_temp RENAME TO your_table")
                }
            }
            // synchronized-避免出现竞态条件
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context,
                    BacklogDatabase::class.java,"backlog_database")
//                        迁移策略-销毁并重建数据库
                    .addMigrations(MIGRATION_5_6)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}