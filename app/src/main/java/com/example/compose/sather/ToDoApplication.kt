package com.example.compose.sather

import android.app.Application
import com.example.compose.sather.data.AppContainer
import com.example.compose.sather.data.AppDataContainer

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
