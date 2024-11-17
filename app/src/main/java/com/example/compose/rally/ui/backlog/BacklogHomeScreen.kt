package com.example.compose.rally.ui.backlog

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.compose.rally.ui.navigation.BaseDestination
import com.example.compose.rally.ui.routine.RoutineUiState
import com.example.compose.rally.ui.routine.formatedCredit
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


//日程-home页
data object BacklogHome : BaseDestination {
    override val icon =Icons.Filled.Timer
    override val route ="backlogs"
}
//    三次接入-标题展开，进入页面，数据渲染
@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BacklogHomeScreen(
    /*
    * 参数上仅考虑顶级页面间的交互
    */
//    元素共享-动画效果
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
//    导航-跳转单独页面
    onBacklogDetailClick:(Int)->Unit={},
//    数据管理
    viewModel:BacklogHomeViewModel  = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
//    State
    val backlogHomeUiState by viewModel.backlogHomeUiState.collectAsState()
    val routineHomeUiState by viewModel.routineHomeUiState.collectAsState()
//    数据
    val backlogList = backlogHomeUiState.backlogList
    val finishedRoutineList= routineHomeUiState.routineList.filter { it.finished }
    val unfinishedRoutineList= routineHomeUiState.routineList.filter { !it.finished }
    
//    特殊属性-主页面
//      -可优化
//      -routineMap = remember { mutableStateMapOf<Int, Float>() }
    val importTotal by rememberUpdatedState(
        finishedRoutineList.map { routine ->
            if(routine.rank==0)routine.credit else 0f
        }.sum()
    )
    val normalTotal by rememberUpdatedState(
        finishedRoutineList.map { routine ->
            if(routine.rank==1)routine.credit else 0f
        }.sum()
    )
    val faverTotal by rememberUpdatedState(
        finishedRoutineList.map { routine ->
            if(routine.rank==2)routine.credit else 0f
        }.sum()
    )
    val creditsTotal =importTotal+normalTotal+faverTotal
//    界面
    //    每日新日程
    val currentDate = LocalDate.now()
    var formattedDate = currentDate.format(formatter)
    Box(modifier = Modifier.fillMaxSize()){
        BacklogHomeBody(
            sharedTransitionScope=sharedTransitionScope,
            animatedContentScope=animatedContentScope,
            
            onNewBacklog={
                coroutineScope.launch {
                    onBacklogDetailClick(viewModel.newCurrentBacklog(formattedDate))
                }
                         },
            formattedDate=formattedDate,
            onBacklogDetailClick=onBacklogDetailClick,
            
            backlogUiState = viewModel.backlogUiState,
            routineUiState = viewModel.routineUiState,
            updateBacklogUiState=viewModel::updatBacklogUiState,
            updateRoutineUiState=viewModel::updateRoutineUiState,
        insertRoutineClick={
            coroutineScope.launch {
                viewModel.inseetRoutine()
            }
        },
            
            
            creditsTotal=creditsTotal,
            importTotal=importTotal,
            normalTotal=normalTotal,
            faverTotal=faverTotal,
            backlogList=backlogList,
            routineList = unfinishedRoutineList,
            )
    }
}
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BacklogHomeBody(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    
    formattedDate: String,
    onNewBacklog:() -> Unit,
    onBacklogDetailClick:(Int)->Unit={},
    insertRoutineClick:() ->Unit,
    backlogUiState: BacklogUiState,
    routineUiState: RoutineUiState,
    updateRoutineUiState:(Routine) -> Unit,
    updateBacklogUiState:(Backlog) -> Unit,
    
    creditsTotal:Float,
    importTotal:Float,
    normalTotal:Float,
    faverTotal:Float,
    backlogList: List<Backlog>,
    routineList: List<Routine>,
    ){
    var EditCardVisible by rememberSaveable { mutableStateOf(false) }
    
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
                val routineListForBacklog = routineList.filter { it -> it .backlogId==backlog.id }
                BacklogDetailCard(
                    sharedTransitionScope=sharedTransitionScope,
                    animatedContentScope=animatedContentScope,
                    
                    backlog = backlog,
                    routineList = routineListForBacklog,
                    
                    onBacklogDetailClick=onBacklogDetailClick,
                    onBacklogEditClick = { back,rout ->
                        updateBacklogUiState(back)
                        updateRoutineUiState(rout)
                        println("Entry Click: "+back.id+"   Edit: ")
                        EditCardVisible =!EditCardVisible
                    }
                )
                Spacer(Modifier.height(12.dp))

            }
        }
        if(backlogList.isEmpty() || !backlogList.first().timeTitle.equals(formattedDate)) {
            FloatingActionButton(
                onClick = onNewBacklog,
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
    
//    设置可见与否
//    backlogid -> routinelist
//    初始化viewmodel
    if(EditCardVisible){
        BacklogEditCard(
            routineList = routineList.filter { it.backlogId==backlogUiState.backlog.id },
            backlogUiState=backlogUiState,
            routineUiState = routineUiState,
            /*
            * 考虑routinelist
            *   查询一次
            * 每次失去焦点就更新-则热观察提供的list也可以
            * 修改频繁的具体值考虑限定在文字更改
            * 忽略提供的分级选项或视作不频繁修改
            * 需要添加分数修改，属于不频繁修改
            * 不修改备注*/
            
//            ！！修改uistate全为字符串以优化代码！！
            updateBacklogUiState =updateBacklogUiState,
            insertRoutineClick = insertRoutineClick,
            updateRoutineUiState =updateRoutineUiState,
        )
        
    }
}
val formatter = DateTimeFormatter.ofPattern(DateFormatter)
