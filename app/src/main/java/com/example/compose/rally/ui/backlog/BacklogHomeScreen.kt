package com.example.compose.rally.ui.backlog

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.rally.R
import com.example.compose.rally.data.*
import com.example.compose.rally.ui.AppViewModelProvider
import com.example.compose.rally.ui.components.*
import com.example.compose.rally.ui.navigation.RallyDestination
import com.example.compose.rally.ui.routine.formatedCredit
import com.example.compose.rally.ui.theme.faverColor
import com.example.compose.rally.ui.theme.importColor
import com.example.compose.rally.ui.theme.normalColor
import kotlinx.coroutines.launch
import java.time.LocalDate

//日程-home页
data object BacklogHome : RallyDestination {
    override val icon =Icons.Filled.Timer
    override val route ="backlogs"
}
//    三次接入-标题展开，进入页面，数据渲染
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BacklogHomeScreen(
    onBacklogClick:(Int)->Unit={},
    viewModel:BacklogHomeViewModel  = viewModel(factory = AppViewModelProvider.Factory)
) {
    val backlogUiState by viewModel.homeUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
//    积分属性
    val routineUiState by viewModel.routineUiState.collectAsState()
    
    val importTotal by rememberUpdatedState(
        routineUiState.routineList.map { routine ->
            if(routine.rank==0)routine.credit else 0f
        }.sum()
    )
    val normalTotal by rememberUpdatedState(
        routineUiState.routineList.map { routine ->
            if(routine.rank==1)routine.credit else 0f
        }.sum()
    )
    val faverTotal by rememberUpdatedState(
        routineUiState.routineList.map { routine ->
            if(routine.rank==2)routine.credit else 0f
        }.sum()
    )
    val creditsTotal =importTotal+normalTotal+faverTotal
    
//    样式设计
    Box(modifier = Modifier.fillMaxSize()){
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .semantics { contentDescription = "Backlogs Screen" }
        ) {
//        三色转圈
            Box(Modifier.padding(16.dp)) {
                ThreeColorCircle(
                    proportions =if(creditsTotal>0f) listOf((importTotal/creditsTotal),(normalTotal/creditsTotal),(faverTotal/creditsTotal))
                    else listOf(0f,0f,1f)
                )
                Spacer(Modifier.height(12.dp))
                Column(modifier = Modifier.align(Alignment.Center)) {
                    Text(
                        text = stringResource(R.string.credit),
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = formatedCredit( creditsTotal.toString()),
                        style = MaterialTheme.typography.h2,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
//        日志卡
            backlogUiState.backlogList.map {
                    backlog ->
                    BacklogsCard(
                        backlog = backlog,
                        routineList = routineUiState.routineList.filter { it -> it .backlogId==backlog.id },
                        onBacklogClick=onBacklogClick,
                    )
                Spacer(Modifier.height(12.dp))
            }
            
        }
        //    每日新日程
        val currentDate = LocalDate.now()
        var formattedDate = currentDate.format(formatter)
        if(backlogUiState.backlogList.isEmpty() || !backlogUiState.backlogList.first().timeTitle.equals(formattedDate)) {
            FloatingActionButton(
                onClick ={
                    coroutineScope.launch {
                        onBacklogClick(viewModel.newCurrentBacklog(formattedDate))
                    }
                },
                backgroundColor = MaterialTheme.colors.surface,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(16.dp),
                
                ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Backlog"
                )
            }
        }
    }
}

@Composable
private fun BacklogsCard(
    backlog: Backlog,
    routineList:List<Routine>,
    modifier: Modifier =Modifier,
    onBacklogClick: (Int) -> Unit,
) {
    val creditTotal:Float =routineList.map { it ->it.credit }.sum()
    Card {
        Column {
            Column(modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clickable { onBacklogClick(backlog.id) }
            ) {
                Text(text = backlog.timeTitle, style = MaterialTheme.typography.h2)
                val amountText = "$" + creditTotal
                Text(text = amountText, style = MaterialTheme.typography.subtitle2)
            }
            BaseDivider(creditTotal, routineList.map { it->it.credit },
                routineList.map { it ->
                    when(it.rank){
                    0 -> importColor
                    1-> normalColor
                    else -> faverColor
                } })
            Column(Modifier
                .padding(start = 16.dp, top = 4.dp, end = 8.dp)
            ) {
                SeeAllButton(
                    modifier = modifier.clearAndSetSemantics {
                        contentDescription = "All ${backlog.timeTitle}'s Routines"
                    }.clickable { onBacklogClick(backlog.id) }
                )
            }
        }
    }
}
