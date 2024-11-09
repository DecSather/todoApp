package com.example.compose.rally.ui.backlog

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    val backlogHomeUiState by viewModel.backlogUiState.collectAsState()
    val backlogList = backlogHomeUiState.backlogList
    
//    积分属性-可优化-routineMap = remember { mutableStateMapOf<Int, Float>() }
    val routineHomeUiState by viewModel.routineUiState.collectAsState()
    val routineList= routineHomeUiState.routineList.filter { it.finished }
    val coroutineScope = rememberCoroutineScope()
    
    val importTotal by rememberUpdatedState(
        routineList.map { routine ->
            if(routine.rank==0)routine.credit else 0f
        }.sum()
    )
    val normalTotal by rememberUpdatedState(
        routineList.map { routine ->
            if(routine.rank==1)routine.credit else 0f
        }.sum()
    )
    val faverTotal by rememberUpdatedState(
        routineList.map { routine ->
            if(routine.rank==2)routine.credit else 0f
        }.sum()
    )
    val creditsTotal =importTotal+normalTotal+faverTotal
//    界面
    Box(modifier = Modifier.fillMaxSize()){
        BacklogHomeBody(
            creditsTotal=creditsTotal,
            importTotal=importTotal,
            normalTotal=normalTotal,
            faverTotal=faverTotal,
            backlogList=backlogList,
            routineList = routineList,
            onBacklogClick=onBacklogClick,
            )
        //    每日新日程
        val currentDate = LocalDate.now()
        var formattedDate = currentDate.format(formatter)
        if(backlogList.isEmpty() || !backlogList.first().timeTitle.equals(formattedDate)) {
            FloatingActionButton(
                onClick = { coroutineScope.launch {
                    onBacklogClick(viewModel.newCurrentBacklog(formattedDate))
                } },
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
fun BacklogHomeBody(
    creditsTotal:Float,
    importTotal:Float,
    normalTotal:Float,
    faverTotal:Float,
    backlogList: List<Backlog>,
    routineList: List<Routine>,
    onBacklogClick:(Int)->Unit={},
    
){
    Box(modifier = Modifier.fillMaxSize()){
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .semantics { contentDescription = "Backlogs Screen" }
        ) {
//        三色转圈
            var proportions =
                if(creditsTotal>0f) listOf((importTotal/creditsTotal),(normalTotal/creditsTotal),(faverTotal/creditsTotal),0f)
            else listOf(0f,0f,0f,1f)
            item {
                Box(Modifier.padding(16.dp)) {
                    ThreeColorCircle(
                        proportions =proportions,
                        colorIndexs = listOf(0,1,2)
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
            }
//        日志卡
            items(backlogList.size){index->
                val backlog= backlogList[index]
                BacklogsCard(
                    backlog = backlog,
                    routineList = routineList.filter { it -> it .backlogId==backlog.id },
                    onBacklogClick=onBacklogClick,
                )
                Spacer(Modifier.height(12.dp))
                
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
                routineList.map { RoutineColors[it.rank]})
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
val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
