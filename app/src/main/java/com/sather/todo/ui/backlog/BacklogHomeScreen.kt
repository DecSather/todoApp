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
    
//    特殊属性-主页面
//      -可优化
//      -routineMap = remember { mutableStateMapOf<Int, Float>() }
    

//    界面
    Box(modifier = Modifier.fillMaxSize()){
        BacklogHomeBody(
            sharedTransitionScope=sharedTransitionScope,
            animatedContentScope=animatedContentScope,
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
            susAddBacklog ={ timeTitle ->
                coroutineScope.launch {
                    viewModel.addBacklog(timeTitle)
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
            onBacklogDetailClick = onBacklogDetailClick,
            
            backlogUiState = viewModel.backlogUiState,
            updateBacklogUiState=viewModel::updatBacklogUiState,
            
            updateRoutine = { routine ->
                coroutineScope.launch {
                    viewModel.updateRoutine(routine)
                }
            },
            insertRoutine = { routine ->
                coroutineScope.launch {
                    viewModel.insertRoutine(routine)
                }
            },
            backlogList=backlogHomeUiState.backlogList,
            finishedRoutineList = routineHomeUiState.routineList.filter { it.finished },
            routineList = routineHomeUiState.routineList.filter { !it.finished },
            )
    }
}
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BacklogHomeBody(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    
    backlogUiState: BacklogUiState,
    onBacklogDetailClick:(Int)->Unit={},
    onExpandChange:(Int,Boolean)->Unit,
    onFinishedChange:(String,Boolean)->Unit,
    
    updateBacklogUiState:(Backlog) -> Unit,
    
    susDeleteBacklogById:(Int)->Unit,
    susUpdateBacklog:() -> Unit,
    susAddBacklog:(String) ->Unit,
    
    updateRoutine:(Routine)->Unit,
    insertRoutine:(Routine)->Unit,
    
    backlogList: List<Backlog>,
    finishedRoutineList:List<Routine>,
    routineList: List<Routine>,
    ){
    
    var showEditDialog by remember { mutableStateOf(false) }
    var showDatePickerModal by remember { mutableStateOf(false) }
    var clickPart by rememberSaveable { mutableIntStateOf(0) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
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
                    modifier = Modifier,
                    
                    backlog = backlog,
                    routineList = routineList,
                    
                    onExpandClick = onExpandChange,
                    onFinishedChange = onFinishedChange,
                    
                    onDelete = susDeleteBacklogById,
                    
                    onBacklogDetailClick = onBacklogDetailClick,
                    onBacklogEditClick = {
                        updateBacklogUiState(backlog)
                        clickPart = it
                        selectedDate = LocalDate.parse(backlog.timeTitle, formatter)
                        showEditDialog = !showEditDialog
                    }
                )
                Spacer(Modifier.height(12.dp))
            }
        }
//        添加按钮
        FloatingActionButton(
            shape = CircleShape,
            onClick = {
                showDatePickerModal = !showDatePickerModal
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
//    编辑卡片
    if(showEditDialog){
        EditCardDialog(
            onDismiss = { tempRoutineList ->
                showEditDialog = !showEditDialog
                var index = 0
                var sortId = 0
                while (index < tempRoutineList.size) {
                    val routine = tempRoutineList[index]
                    if (routine.rank != 3) {
                        if(routine.finished) {
                            insertRoutine(routine.copy(sortId = sortId, finished = false, rank = 1))
                        }
                        else {
                            updateRoutine(routine.copy(sortId = sortId))
                        }
                        if(routine.content.isNotEmpty())
                            sortId++
                    }
                    index++
                }
            },
            clickPart = clickPart,
            
            backlogUiState=backlogUiState,
            routineList = routineList.filter {it.backlogId == backlogUiState.backlog.id
            },
            updateBacklogUiState = updateBacklogUiState,
            onUpdateBacklog=susUpdateBacklog,
            
        )
    }
    if(showDatePickerModal){
        DatePickerModal(
            selectedDate = selectedDate,
            onDateSelected = {
                selectedDate = it
                susAddBacklog(selectedDate.format(DateTimeFormatter.ISO_DATE))
            },
            onDismiss = {showDatePickerModal = !showDatePickerModal},
        )
    }
}
val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
