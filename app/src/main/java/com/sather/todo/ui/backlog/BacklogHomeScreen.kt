package com.sather.todo.ui.backlog

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.sather.R
import com.sather.todo.ui.AppViewModelProvider
import com.sather.todo.ui.navigation.BaseDestination
import com.sather.todo.ui.routine.RoutineUiState
import com.sather.todo.ui.routine.formatedCredit
import com.sather.todo.data.Backlog
import com.sather.todo.data.Routine
import com.sather.todo.ui.components.BacklogDetailCard
import com.sather.todo.ui.components.ThreeColorCircle
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
//    导航
    onBacklogDetailClick:(Int)->Unit={},
//    数据管理
    viewModel: BacklogHomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
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
    
    var showEditDialog by remember { mutableStateOf(false) }
    
    var clickPart by rememberSaveable { mutableIntStateOf(0) }
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
                    viewModel.updatBacklogUiState(
                        Backlog(
                            id = viewModel.newCurrentBacklog(formattedDate),
                            timeTitle = formattedDate
                        )
                    )
                    clickPart = -1
                    showEditDialog =!showEditDialog
                }
                         } ,
            onDelete ={ it ->
                coroutineScope.launch {
                    viewModel.deleteBacklogById(it)
                }
            },
                    clickPart=clickPart,
            showEditDialog =showEditDialog,
            onChangeEditStatus ={showEditDialog = !showEditDialog},
            formattedDate=formattedDate,
            onBacklogDetailClick = onBacklogDetailClick,
            onExpandChange ={ id,isExpand ->
                coroutineScope.launch {
                    viewModel.onExpandChange(id,isExpand)
                }
            
            },
            onFinishedChange={ id,finished ->
                coroutineScope.launch {
                    viewModel.onRoutineFinishedChange(id,finished)
                }
            },
            
            backlogUiState = viewModel.backlogUiState,
            routineUiState = viewModel.routineUiState,
            updateBacklogUiState={backlog ->
                viewModel.updatBacklogUiState(backlog)
                clickPart=-1
                
            },
            updateRoutineUiState=viewModel::updateRoutineUiState,
            
            onSaveBacklog={
                coroutineScope.launch {
                    viewModel.updateBacklog()
                }
            },
            onSaveRoutine={
                coroutineScope.launch {
                    viewModel.updateRoutine()
                }
            },
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
    onDelete:(Int)->Unit,
    clickPart:Int,
    showEditDialog :Boolean,
    onChangeEditStatus:()->Unit,
    onBacklogDetailClick:(Int)->Unit={},
    onExpandChange:(Int,Boolean)->Unit,
    onFinishedChange:(Int,Boolean)->Unit,
    
    insertRoutineClick:() ->Unit,
    backlogUiState: BacklogUiState,
    routineUiState: RoutineUiState,
    updateRoutineUiState:(Routine) -> Unit,
    updateBacklogUiState:(Backlog) -> Unit,
    onSaveBacklog:() -> Unit,
    onSaveRoutine:() -> Unit,
    
    creditsTotal:Float,
    importTotal:Float,
    normalTotal:Float,
    faverTotal:Float,
    backlogList: List<Backlog>,
    routineList: List<Routine>,
    ){
    val density = LocalDensity.current
    
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
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Text(
                            text = formatedCredit( creditsTotal.toString()),
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
//        日志卡
            
                items(backlogList.size) { index ->
                    val backlog = backlogList[index]
                    val routineListForBacklog = routineList.filter { it -> it.backlogId == backlog.id }
                    key(backlog.id) {
                        BacklogDetailCard(
                            sharedTransitionScope = sharedTransitionScope,
                            animatedContentScope = animatedContentScope,
                            backlog = backlog,
                            onExpandClick = {
                                onExpandChange(backlog.id, it)
                            },
                            routineList = routineListForBacklog,
                            onDelete = onDelete,
                            
                            onBacklogDetailClick = onBacklogDetailClick,
                            onFinishedChange = onFinishedChange,
                            onBacklogEditClick = {
                                updateBacklogUiState(backlog)
                                onChangeEditStatus()
                            }
                        )
                        
                    }
                    Spacer(Modifier.height(12.dp))
                }
            
        }
        FloatingActionButton(
            shape = CircleShape,
            onClick = {
                onNewBacklog()
                      },
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
    
//    设置可见与否
//    backlogid -> routinelist
    if(showEditDialog){
        EditDialogModal(
            onDateSelected = onChangeEditStatus,
            onDismiss =onChangeEditStatus,
            clickPart = clickPart,
            routineList = routineList.filter { it.backlogId==backlogUiState.backlog.id },
            backlogUiState=backlogUiState,
            routineUiState = routineUiState,
            updateBacklogUiState = {it
                updateBacklogUiState(it)
                onSaveBacklog()
                                   },
            insertRoutineClick = insertRoutineClick,
            updateRoutineUiState =updateRoutineUiState,
            
            onModelSaveClick = {
                onSaveRoutine()
                onChangeEditStatus()
                            },
            onSaveRoutine = {
                onSaveRoutine()
            },
        )

    }
    
}
val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
