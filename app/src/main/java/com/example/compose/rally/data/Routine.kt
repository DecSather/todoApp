package com.example.compose.rally.data

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.compose.rally.ui.theme.normalColor


@Entity(tableName = "routines")
data class Routine(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
//    分类id由SingleBacklog页面上传
    val backlogId:Int,
    val content: String,
//
    val finished: Boolean =false,
    val subcontent:String="",
//    0,1,2
    val rank:Int=1,
    val credit: Float =0f,
){
    // 无参构造-非法数据，预加载使用
    constructor() : this(
        id = -1,
        backlogId = -1,
        content = "",
        finished = false,
        subcontent = "",
        rank = -1,
        credit = -1f
    )
}
