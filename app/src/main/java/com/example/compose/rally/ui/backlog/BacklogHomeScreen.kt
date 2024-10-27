package com.example.compose.rally.ui.backlog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.rally.R
import com.example.compose.rally.data.*
import com.example.compose.rally.ui.AppViewModelProvider
import com.example.compose.rally.ui.components.*

//日程-home页

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BacklogHomeScreen(
    onBacklogClick:(String )->Unit={},
    viewModel:BacklogHomeViewModel  = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
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
        homeUiState.backlogList.map { backlog ->
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
