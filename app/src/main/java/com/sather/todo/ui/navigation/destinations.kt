package com.sather.todo.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddHome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument

interface BaseDestination {
    val icon: ImageVector
    val route: String
}
//日程-home页
data object BacklogHome : BaseDestination {
    override val icon =Icons.Filled.Timer
    override val route = "Backlogs"
}

object SingleBacklogDestination : BaseDestination {
    override val route = "single_backlog"
    override val icon = Icons.Filled.Timer
    const val backlogIdArg = "backlogId"
    val routeWithArgs = "$route/{$backlogIdArg}"
    val arguments = listOf(navArgument(backlogIdArg) {
        type = NavType.LongType
    })
}
object SingleRoutineDestination : BaseDestination {
    override val route = "single_routine"
    override val icon = Icons.Filled.Check
    const val routineIdArg = "routineId"
    val routeWithArgs = "$route/{$routineIdArg}"
    val arguments = listOf(
        navArgument(routineIdArg) { type = NavType.LongType }
    )
}

data object ComeSoon : BaseDestination {
    override val icon = Icons.Filled.AddHome
    override val route = "Comesoon"
}