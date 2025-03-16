package com.sather.todo.glance.workmanager

import android.content.Context
import android.util.Log
import androidx.work.*
import com.sather.todo.data.BacklogDatabase.Companion.getDatabase
import com.sather.todo.data.RoutinesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdateRoutineWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                // 1. 从输入数据中获取参数
                val id = inputData.getLong("id",(-1).toLong())
                println("UpdateRoutineWorker doWork():${id}")
                if(id == (-1).toLong())return@withContext Result.failure()
                val finished = inputData.getBoolean("finished", false)
                
                // 2. 获取 RoutineRepository 实例
                val routineDao = getDatabase(applicationContext).routineDao()
                val repository = RoutinesRepository(routineDao)
                
                repository.updateFinished(id, finished)
                
                // 4. 返回成功结果
                Result.success()
            } catch (e: Exception) {
                Log.e("UpdateRoutineWorker", "Failed to update routine", e)
                Result.failure()
            }
        }
    }
}
