package com.sather.todo

import android.app.Application
import com.sather.todo.data.AppContainer
import com.sather.todo.data.AppDataContainer

class ToDoApplication : Application() {
    
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer
    
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
