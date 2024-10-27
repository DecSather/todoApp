package com.example.compose.rally

import android.app.Application
import com.example.compose.rally.data.AppContainer
import com.example.compose.rally.data.AppDataContainer

class RallyApplication : Application() {
    
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer
    
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
