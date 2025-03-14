package com.sather.todo.glance.workmanager
import android.content.Context
import androidx.compose.ui.util.fastFlatMap
import androidx.glance.appwidget.updateAll
import androidx.work.*
import com.sather.todo.data.BacklogDatabase.Companion.getDatabase
import com.sather.todo.data.Routine
import com.sather.todo.glance.MyAppWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class UpdateWidgetWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                // 1. 从 Room 获取数据
                val items = fetchDataFromDatabase(applicationContext)
                
                // 2. 将数据存储到 DataStore
                saveDataToDataStore(applicationContext, items)
                
                // 3. 通知 Widget 更新
                notifyWidgetUpdate(applicationContext)
                
                Result.success()
            } catch (e: Exception) {
                Result.retry()
            }
        }
    }
    
    private suspend fun fetchDataFromDatabase(context: Context): List<Routine>{
        // 逻辑：从 Room 获取数据并分组
        // 例如：
         val flows = getDatabase(context).routineDao().getRoutinesByBacklogId(34).first()
         return flows
        return listOf(
            Routine(
                backlogId = -1,
                sortId = 0,
                content = "fetchDataFromDatabase wrong"
            )
        ) // 示例返回空数据
    }
    
    private suspend fun notifyWidgetUpdate(context: Context) {
        // 通知 Widget 更新
        val widget = MyAppWidget()
        widget.updateAll(context)
    }
}