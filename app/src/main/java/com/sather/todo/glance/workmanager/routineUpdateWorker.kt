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
                val id = inputData.getLong("id",(-1).toLong())
                if(id == (-1).toLong())return@withContext Result.failure()
                
                val finished = inputData.getBoolean("finished", false)
                val routineDao = getDatabase(applicationContext).routineDao()
                routineDao.undateFinished(id, finished)
                
                // 4. 返回成功结果
                Result.success()
            } catch (e: Exception) {
                Log.e("UpdateRoutineWorker", "Failed to update routine", e)
                Result.failure()
            }
        }
    }
}
