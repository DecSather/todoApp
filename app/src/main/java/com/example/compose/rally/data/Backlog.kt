package com.example.compose.rally.data

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backlogs")
data class Backlog(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val timeTitle: String ="",
//    差一个记录是否展开的判断值
    ){
//    无参构造-非法数据，预加载使用
    constructor() : this(
        id = -1,
        timeTitle=""
    )
    
}