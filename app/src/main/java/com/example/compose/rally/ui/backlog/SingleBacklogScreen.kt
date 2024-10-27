package com.example.compose.rally.ui.backlog

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.compose.rally.data.BacklogData
import com.example.compose.rally.data.BackloggetRoutines
import com.example.compose.rally.ui.components.CommonBody
import com.example.compose.rally.ui.components.RoutineRow

@Composable
fun SingleBacklogScreen(
    navigateToAddBacklog: () -> Unit={},
    navigateToUpdateBacklog: (Int) -> Unit={},
    backlogType: String? = BacklogData.backlogs.first().timeTitle
) {
    val backlog = remember(backlogType) { BacklogData.getBacklog(backlogType) }
    val routines= BackloggetRoutines(backlog)
    val amount=routines.map { routine ->routine.credit }.sum()
    
    CommonBody(
        items=routines,
        creditRatios= listOf(backlog.importCredit/amount,backlog.normalCredit/amount,backlog.faverCredit/amount),
        amountsTotal=amount,
        circleLabel=backlog.timeTitle,
    ){ routine ->
        RoutineRow(
            modifier = Modifier.clickable { /*waitng for implement*/ },
            content = routine.content,
            subcontent = routine.subcontent,
            credit = routine.credit,
            finished = routine.finished,
            color = routine.color
        )
    }
}
