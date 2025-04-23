package com.sather.todo.ui.backlog.components
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.sather.todo.ui.backlog.formatter
import com.sather.todo.ui.components.basePadding
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun SystemTimeTitle() {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    
    // 每秒更新一次时间（支持秒级精度）
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000) // 每秒更新
            currentTime = System.currentTimeMillis()
        }
    }
    
    val timeFormatter = remember {
        DateTimeFormatter
            .ofPattern("EEEE HH:mm:ss") // 星期几 + 时:分:秒
            .withLocale(Locale.getDefault())
    }
    
    // 转换为 LocalDateTime（用于格式化）
    val localDateTime = remember(currentTime) {
        Instant.ofEpochMilli(currentTime)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    }
    
    Column(Modifier.padding(basePadding)) {
        // 第一行：年月日（系统格式）
        Text(
            text = localDateTime.format(formatter),
            style = MaterialTheme.typography.headlineLarge
        )
        
        // 第二行：星期几 时:分:秒
        Text(
            text = localDateTime.format(timeFormatter),
            style = MaterialTheme.typography.headlineMedium
        )
    }
}