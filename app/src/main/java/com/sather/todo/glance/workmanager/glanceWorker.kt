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

class UpdateWidgetWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        println("UpdateWidgetWorker doWork()")
        return withContext(Dispatchers.IO) {
            try {
//                获得数据-转存数据-widget更新
                val timeTitle = getTimeTitleFromDataStore(applicationContext).first()
                if(timeTitle == "1970-01-01") return@withContext Result.retry()
                val items = fetchDataFromDatabase(applicationContext,timeTitle)
                saveRoutinesToDataStore(applicationContext, items)
                notifyWidgetUpdate(applicationContext)
                Result.success()
            } catch (e: Exception) {
                Result.retry()
            }
        }
    }
    
    private suspend fun fetchDataFromDatabase(context: Context,timeTitle:String):  List<Routine>  {
        val backlogs = getDatabase(context).backlogDao().getBacklogByString(timeTitle)
        val routineDao = getDatabase(context).routineDao()
        val flows:List<Routine>
//        backlog防空判断
        if(backlogs.first().isEmpty()){
            return listOf(
                Routine(
                    backlogId = -1,
                    sortId = 0,
                    content = "nothing for today",
                    finished = true
                )
            )
        }else {
            val backlogIds = backlogs.first().map { it.id }
            flows = backlogIds.map {
                routineDao.getRoutinesByBacklogId(it).first()
            }.fastFlatMap { it }.filter { !it.finished }
        }
//        事项完成防空判断：这里是为了后续UI不会卡在Loading占位，目前还没想好算不算bug
        if(flows.isEmpty()){
            return listOf(
                Routine(
                    backlogId = -1,
                    sortId = 0,
                    content = "nothing for today",
                    finished = true
                )
            )
        }
         return flows
    }
    
    private suspend fun notifyWidgetUpdate(context: Context) {
        // 通知 Widget 更新
        val widget = MyAppWidget()
        widget.updateAll(context)
    }
}