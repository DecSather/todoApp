package com.example.compose.rally.ui.backlog

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.rally.R
import com.example.compose.rally.data.*
import com.example.compose.rally.ui.AppViewModelProvider
import com.example.compose.rally.ui.components.*
import com.example.compose.rally.ui.routine.RoutineHomeViewModel
import com.example.compose.rally.ui.theme.faverColor
import com.example.compose.rally.ui.theme.importColor
import com.example.compose.rally.ui.theme.normalColor
import kotlinx.coroutines.launch
import java.time.LocalDate

//日程-home页

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BacklogHomeScreen(
    onBacklogClick:(Int)->Unit={},
    viewModel:BacklogHomeViewModel  = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

//    积分属性
    val importTotal by rememberUpdatedState(homeUiState.backlogList.map { backlog -> backlog.importCredit.toDouble()}.sum())
    val normalTotal by rememberUpdatedState(homeUiState.backlogList.map { backlog -> backlog.normalCredit.toDouble()}.sum() )
    val faverTotal by rememberUpdatedState(homeUiState.backlogList.map { backlog -> backlog.faverCredit.toDouble()}.sum() )
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
                    proportions =  listOf((importTotal/creditsTotal).toFloat(),(normalTotal/creditsTotal).toFloat(),(faverTotal/creditsTotal).toFloat())
                )
                Spacer(Modifier.height(12.dp))
                Column(modifier = Modifier.align(Alignment.Center)) {
                    Text(
                        text = stringResource(R.string.credit),
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = creditsTotal.toString(),
                        style = MaterialTheme.typography.h2,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
//        日志卡
            homeUiState.backlogList.map {
                    backlog ->
                BacklogsCard(
                    backlog = backlog,
                    modifier=Modifier.clickable {
                        onBacklogClick(backlog.id)
                    }
                )
                Spacer(Modifier.height(12.dp))
            }
            
        }
        //    每日新日程
        val currentDate = LocalDate.now()
        var formattedDate = currentDate.format(formatter)
        if(!homeUiState.backlogList.isEmpty() && !homeUiState.backlogList.first().timeTitle.equals(formattedDate)) {
            FloatingActionButton(
                onClick ={
                    coroutineScope.launch {
                        onBacklogClick(viewModel.newCurrentBacklog(timeTitle = formattedDate))
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
    modifier: Modifier =Modifier
) {
    val creditTotal:Float =backlog.importCredit+backlog.normalCredit+backlog.faverCredit
    Card {
        Column {
            Column(modifier
                .fillMaxWidth()
                .padding(12.dp)
            ) {
                Text(text = backlog.timeTitle, style = MaterialTheme.typography.h2)
                val amountText = "$" + creditTotal
                Text(text = amountText, style = MaterialTheme.typography.subtitle2)
            }
            BaseDivider(creditTotal, listOf(backlog.importCredit,backlog.normalCredit,backlog.faverCredit), listOf(
                importColor, normalColor, faverColor))
            Column(Modifier
                .padding(start = 16.dp, top = 4.dp, end = 8.dp)
            ) {
                SeeAllButton(
                    modifier = modifier.clearAndSetSemantics {
                        contentDescription = "All ${backlog.timeTitle}'s Routines"
                    }
                )
            }
        }
    }
}
