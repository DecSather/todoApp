package com.sather.todo.ui.backlog.components
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.sather.todo.ui.backlog.formatter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun SystemTimeTitle() {
    val context = LocalContext.current
    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }
    
    // 使用系统广播接收时间变化事件
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                currentTime = LocalDateTime.now()
            }
        }
        
        // 注册广播接收器
        context.registerReceiver(
            receiver,
            IntentFilter().apply {
                addAction(Intent.ACTION_TIME_TICK)    // 每分钟更新
                addAction(Intent.ACTION_TIME_CHANGED)
                addAction(Intent.ACTION_TIMEZONE_CHANGED)
            }
        )
        
        onDispose {
            context.unregisterReceiver(receiver)
        }
    }
    
    // 获取系统默认的日期和时间格式
    val dateFormatter =formatter
        .withLocale(Locale.getDefault())
    
    val timeFormatter = DateTimeFormatter
        .ofPattern("EEEE HH:mm")  // 星期几 + 时分
        .withLocale(Locale.getDefault())
    
    Column {
        // 第一行：年月日（自动适应系统格式）
        Text(
            text = currentTime.format(dateFormatter),
            style = MaterialTheme.typography.headlineLarge
        )
        
        // 第二行：星期几 时分（自动适应系统语言）
        Text(
            text = currentTime.format(timeFormatter),
            style = MaterialTheme.typography.headlineMedium
        )
    }
}