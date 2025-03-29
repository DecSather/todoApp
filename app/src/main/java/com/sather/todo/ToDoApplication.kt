package com.sather.todo

import android.app.*
import androidx.lifecycle.*
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.sather.todo.data.AppContainer
import com.sather.todo.data.AppDataContainer
import com.sather.todo.glance.workmanager.UpdateWidgetWorker
class AppLifecycleObserver(
    private val workManager: WorkManager
) : DefaultLifecycleObserver {
    
    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        // 应用进入后台时触发 WorkManager 任务
        val updateRequest = OneTimeWorkRequestBuilder<UpdateWidgetWorker>()
            .build()
        workManager.enqueue(updateRequest)
    }
    
}

class ToDoApplication : Application() {
    
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer
    
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
//        // 获取 WorkManager 实例
//        val workManager = WorkManager.getInstance(this)
//
//        // 注册生命周期观察者
//        ProcessLifecycleOwner.get().lifecycle.addObserver(
//            AppLifecycleObserver(workManager)
//        )
    }
}
