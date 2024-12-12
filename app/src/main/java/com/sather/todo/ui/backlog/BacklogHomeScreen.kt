package com.sather.todo.ui.backlog

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sather.todo.R
import com.sather.todo.ui.AppViewModelProvider
import com.sather.todo.ui.navigation.BaseDestination
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
            onNewAndUpdateBackUi={
                viewModel.updatBacklogUiState(
                    Backlog(
                        id=-1,
                        timeTitle = formattedDate
                    )
                )
                clickPart = -1
                showEditDialog =!showEditDialog
            } ,
            susDeleteBacklogById ={
                coroutineScope.launch {
                    viewModel.deleteBacklogById(it)
                }
            },
            
            susUpdateBacklog={
                coroutineScope.launch {
                    viewModel.updateBacklog()
                }
            },
            susAddBacklogAndUpdate ={ timeTitle ->
                coroutineScope.launch {
                    viewModel.updatBacklogUiState(
                        Backlog(
                            id =viewModel.addBacklog(timeTitle),
                            timeTitle = timeTitle,
                        )
                    )
                }
            },
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
            clickPart=clickPart,
            showEditDialog =showEditDialog,
            onChangeEditStatus ={showEditDialog = !showEditDialog},
            onChangeClickPart = {clickPart=it},
            
            formattedDate=formattedDate,
            onBacklogDetailClick = onBacklogDetailClick,
            
            backlogUiState = viewModel.backlogUiState,
            updateBacklogUiState=viewModel::updatBacklogUiState,
            
            updateRoutine = { routine ->
                coroutineScope.launch {
                    viewModel.updateRoutine(routine)
                }
            },
            insertRoutine ={ routine ->
                coroutineScope.launch {
                    viewModel.insertRoutine(routine)
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
    clickPart:Int,
    showEditDialog :Boolean,
    onChangeEditStatus:()->Unit,
    onChangeClickPart:(Int)->Unit,
    
    onBacklogDetailClick:(Int)->Unit={},
    onExpandChange:(Int,Boolean)->Unit,
    onFinishedChange:(Int,Boolean)->Unit,
    
    backlogUiState: BacklogUiState,
    
    updateBacklogUiState:(Backlog) -> Unit,
    onNewAndUpdateBackUi:() -> Unit,
    
    susDeleteBacklogById:(Int)->Unit,
    susUpdateBacklog:() -> Unit,
    susAddBacklogAndUpdate:(String) ->Unit,
    
    updateRoutine:(Routine)->Unit,
    insertRoutine:(Routine)->Unit,
    
    creditsTotal:Float,
    importTotal:Float,
    normalTotal:Float,
    faverTotal:Float,
    backlogList: List<Backlog>,
    routineList: List<Routine>,
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
            items(items = backlogList,key = {it.id}) { backlog ->
                val routineList =
                    routineList.filter { it ->it.backlogId == backlog.id}
                BacklogDetailCard(
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope,
                    modifier = Modifier.animateItem(),
                    
                    backlog = backlog,
                    routineList = routineList,
                    
                    onExpandClick = onExpandChange,
                    onFinishedChange = onFinishedChange,
                    
                    onDelete = susDeleteBacklogById,
                    
                    onBacklogDetailClick = onBacklogDetailClick,
                    onBacklogEditClick = {
                        updateBacklogUiState(backlog)
                        onChangeClickPart(it)
                        onChangeEditStatus()
                    }
                )
                Spacer(Modifier.height(12.dp))
            }
        }
        FloatingActionButton(
            shape = CircleShape,
            onClick = onNewAndUpdateBackUi,
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
//    编辑卡片
    if(showEditDialog){
        EditCardDialog(
            onDismiss =onChangeEditStatus,
            clickPart = clickPart,
            onChangeClickPart=onChangeClickPart,
            
            backlogUiState=backlogUiState,
            routineList = routineList.filter { it.backlogId == backlogUiState.backlog.id },
            
            addBacklog = susAddBacklogAndUpdate,
            updateBacklogUiState = updateBacklogUiState,
            onUpdateBacklog=susUpdateBacklog,
            
            updateRoutine = updateRoutine,
            insertRoutine = insertRoutine,
            
        )
    }
    
}
val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
