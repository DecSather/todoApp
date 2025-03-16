package com.sather.todo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backlogs")
data class Backlog(
    @PrimaryKey val id: Long = generateSimpleId(),
    val timeTitle: String = "",
    val isExpand: Boolean = true,
    val isVisible: Boolean = true // 新增字段，默认值为 true
)
// 使用时间戳 + 随机数生成 Long 类型主键
fun generateSimpleId(): Long {
    val timestamp = System.currentTimeMillis()
    val random = (Math.random() * 10000).toLong() // 随机数范围 0-9999
    return timestamp * 10000 + random // 时间戳左移 4 位，加上随机数
}