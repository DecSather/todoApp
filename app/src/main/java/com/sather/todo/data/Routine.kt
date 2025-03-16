package com.sather.todo.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
@Entity(tableName = "routines")
data class Routine(
    @PrimaryKey val id: Long = generateSimpleId(),
//    分类id由SingleBacklog页面上传
    val backlogId: Long,
    val sortId: Int,
    val content: String,
    val finished: Boolean = false,
    val subcontent: String = "",
    val rank: Int = 1,
    val credit: Float = 1f
)