package com.example.compose.rally.data

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.room.TypeConverter
import com.google.gson.Gson

//待办项-单天
@Immutable
data class Backlog(
    val id: Int = 0,
    val timeTitle: String,
    val routineListJson: String,
//    val importColor: Color =Color(0xFF005D57),
//    val normalColor: Color =Color(0xFF039667),
//    val faverColor: Color = Color(0xFF04B97F),
    val importCredit: Float =4f,
    val normalCredit: Float =0f,
    val faverCredit: Float =2f,
    
)
fun BackloggetRoutines(backlog: Backlog): List<Routine> {
    return BacklogData.routines.filter {it.id in fromJsonToList(backlog.routineListJson)}
}
//详情卡片
@Immutable
data class Routine(
    val id: Int = 0,
    val finished: Boolean =false,
    val content: String,
    val subcontent:String,
    val credit: Float =0f,
    val color:Color,
)
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
    
    val routines: List<Routine> = listOf(
        Routine(
            content ="1-Routine Content",
            subcontent ="1-Routine subContent",
            credit = 4f,
            color = Color(0xFF005D57),
        ),Routine(
            content ="2-Routine Content",
            subcontent ="2-Routine subContent",
            credit = 2f,
            color = Color(0xFF04B97F),
        ),
    )
    fun getBacklog(backlogId: Int?): Backlog {
        return backlogs.first { it.id == backlogId }
    }
    
    fun getBacklog(backlogStrinf: String?): Backlog {
        return backlogs.first { it.timeTitle == backlogStrinf }
    }
    fun getRoutine(routineId: Int?): Routine {
        return routines.first { it.id == routineId }
    }
    
    
}
