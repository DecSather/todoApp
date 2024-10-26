package com.example.compose.rally.ui.backlogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.rally.R
import com.example.compose.rally.data.Backlog
import com.example.compose.rally.data.BacklogData
import com.example.compose.rally.data.BackloggetRoutines
import com.example.compose.rally.ui.components.*
import com.example.compose.rally.ui.theme.RallyTheme

//全部日志
@Composable
fun BacklogScreen(
    onBacklogClick:(String )->Unit={}
    
) {
//    积分属性
    val importTotal = remember { BacklogData.backlogs.map { backlog -> backlog.importCredit}.sum() }
    val normalTotal = remember { BacklogData.backlogs.map { backlog -> backlog.normalCredit}.sum() }
    val faverTotal = remember { BacklogData.backlogs.map { backlog -> backlog.faverCredit}.sum() }
    val creditsTotal = importTotal+normalTotal+faverTotal
    
//    样式设计
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .semantics { contentDescription = "Backlogs Screen" }
    ) {
//        三色转圈
        Box(Modifier.padding(16.dp)) {
            ThreeColorCircle(
                proportions =  listOf(importTotal/creditsTotal,normalTotal/creditsTotal,faverTotal/creditsTotal)
            )
            Spacer(Modifier.height(12.dp))
            Column(modifier = Modifier.align(Alignment.Center)) {
                Text(
                    text = stringResource(R.string.credit),
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = formatAmount(creditsTotal),
                    style = MaterialTheme.typography.h2,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
//        日志卡
        BacklogData.backlogs.map { backlog ->
            BacklogsCard(
                backlog = backlog,
                modifier=Modifier.clickable {
                    onBacklogClick(backlog.timeTitle)
                }
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}
@Composable
fun SingleBacklogScreen(
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
    ){routine ->
        RoutineRow(
            modifier = Modifier.clickable { /*waiting for implement*/ },
            content=routine.content,
            subcontent=routine.subcontent,
            credit=routine.credit,
            finished=routine.finished,
            color = routine.color
        )
    }
}

@Preview
@Composable
fun BacklogScreenPreview(){
    RallyTheme {
        SingleBacklogScreen()
    }
}

@Composable
private fun BacklogsCard(
    backlog: Backlog,
    modifier: Modifier =Modifier
) {
    val routines=BackloggetRoutines(backlog)
    val amount = routines.map { routine -> routine.credit }.sum()
    CommonCard(
        modifier=modifier,
        timeTitle = backlog.timeTitle,
        creditTotal = amount,
        data = routines,
        colors = { it.color },
        values = { it.credit }
    ) { routine ->
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
