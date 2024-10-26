package com.example.compose.rally.ui.backlogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.rally.R
import com.example.compose.rally.data.*
import com.example.compose.rally.ui.components.CommonCard
import com.example.compose.rally.ui.components.RoutineRow
import com.example.compose.rally.ui.theme.RallyTheme

//@Composable
//fun BacklogBody(
//    backlog:Backlog,
//    onBacklogClick:(String)->Unit
//) {
//    val routines:List<Routine> =BacklogData.routines.filter {it.id in fromJsonToList(backlog.routineListJson)}
//    val creditTotal=routines.map{routine -> routine.credit}.sum()
//    CommonCard(
//        onSingleClick = onBacklogClick,
//        timeTitle = backlog.timeTitle,
//        creditTotal = creditTotal,
//        data = routines,
//        colors = {it.color},
//        values = { it.credit }
//    ) { routine ->
//        RoutineRow(
//            modifier = Modifier.clickable { /*waiting for implement*/ },
//            content=routine.content,
//            subcontent=routine.subcontent,
//            credit=routine.credit,
//            finished=routine.finished,
//            color = routine.color
//        )
//    }
//}
