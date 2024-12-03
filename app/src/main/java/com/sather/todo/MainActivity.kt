package com.sather.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

//主程序加载-设置页面
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoApp()
        }
    }
}
