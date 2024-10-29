package com.example.compose.rally.data

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.room.TypeConverter
import com.google.gson.Gson

//待办项-单天

@TypeConverter
fun fromListToJson(list: List<Int>): String = Gson().toJson(list)
@TypeConverter
fun fromJsonToList(json: String): List<Int> = Gson().fromJson(json, Array<Int>::class.java).toList()

object BacklogData {
    val backlogs: List<Backlog> = listOf(
        Backlog(
            timeTitle = "2024-1-1",
            routineListJson = fromListToJson(List<Int>(1,{0})),
        ),Backlog(
            timeTitle = "2024-1-2",
            routineListJson = fromListToJson(List<Int>(1,{1})),
        )
        
    )
    
    fun getBacklog(backlogId: Int?): Backlog {
        return backlogs.first { it.id == backlogId }
    }
    
    fun getBacklog(backlogStrinf: String?): Backlog {
        return backlogs.first { it.timeTitle == backlogStrinf }
    }
    
    
}
