package com.sather.todo.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "routines")
data class Routine(
    @PrimaryKey(autoGenerate = true)
//    val id:String = UUID.randomUUID().toString(),
    val id:Int = 0,
//    分类id由SingleBacklog页面上传
    val backlogId:Int,
//    排序id
    val sortId:Int,
    val content: String,
//
    val finished: Boolean =false,
    val subcontent:String="",
//    0,1,2
    val rank:Int=1,
    val credit: Float =1f,
)