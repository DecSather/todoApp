package com.sather.todo.ui.backlog

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sather.todo.R
import com.sather.todo.data.Backlog
import com.sather.todo.data.Routine
import com.sather.todo.ui.AppViewModelProvider
import com.sather.todo.ui.backlog.components.BacklogSwipeCard
import com.sather.todo.ui.backlog.components.BaseScreenBody
import com.sather.todo.ui.backlog.components.ThreeColorCircle
import com.sather.todo.ui.components.basePadding
import com.sather.todo.ui.routine.formatedCredit
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


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
    onBacklogDetailClick:(Long)->Unit={},
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
//    Screen2()

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
            susAddBacklog ={ backlog ->
                coroutineScope.launch {
                    viewModel.addBacklog(backlog)
                }
            },
            onExpandChange ={ id,isExpand ->
                coroutineScope.launch {
                    viewModel.onExpandChange(id,isExpand)
                }
            },
            onVisibleChange = {id,finished ->
                coroutineScope.launch {
                    viewModel.onVisibleChange(id,finished)
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
            homeRoutineList = routineHomeUiState.routineList.filter { !it.finished },
            )
    }
}
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BacklogHomeBody(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    
    backlogUiState: BacklogUiState,
    onBacklogDetailClick:(Long)->Unit={},
    onExpandChange:(Long,Boolean)->Unit,
    onVisibleChange:(Long,Boolean)->Unit,
    onFinishedChange:(Long,Boolean)->Unit,
    
    updateBacklogUiState:(Backlog) -> Unit,
    
    susDeleteBacklogById:(Long)->Unit,
    susUpdateBacklog:() -> Unit,
    susAddBacklog:(Backlog) -> Unit,
    
    updateRoutine:(Routine)->Unit,
    insertRoutine:(Routine)->Unit,
    
    backlogList: List<Backlog>,
    finishedRoutineList:List<Routine>,
    homeRoutineList: List<Routine>,
    ){
    
    var showEditDialog by remember { mutableStateOf(false) }
    var clickPart by rememberSaveable { mutableIntStateOf(-1) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val (importTotal, normalTotal, faverTotal) = remember(finishedRoutineList) {
        var import = 0f
        var normal = 0f
        var faver = 0f
        finishedRoutineList.forEach { routine ->
            when (routine.rank) {
                1 -> import += routine.credit
                2 -> normal += routine.credit
                else -> faver += routine.credit
            }
        }
        Triple(import, normal, faver)
    }
    
    val creditsTotal = remember(importTotal, normalTotal, faverTotal) {
        importTotal + normalTotal + faverTotal
    }
    
    val properties = remember(creditsTotal, importTotal, normalTotal, faverTotal) {
        if (creditsTotal > 0f) listOf(0f, importTotal, normalTotal, faverTotal)
        else listOf(1f, 0f, 0f, 0f)
    }
    
    val filteredBacklogList by remember(backlogList) {
        derivedStateOf { backlogList.sortedByDescending { it.id } }
    }
    BaseScreenBody(
        lazyColumnModifier = Modifier
            .semantics { contentDescription = "Backlogs Screen" },
        top = {
            ThreeColorCircle(
                amount = creditsTotal,
                credits = properties.map { it },
            )
            Spacer(Modifier.height(basePadding))
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
        },
        rows = {
            items(
                items = filteredBacklogList,
                key = {it.id},
                contentType = { "backlogItem" }
            ) { backlog ->
                val routineList by remember(backlog.id, homeRoutineList) {
                    derivedStateOf { homeRoutineList.filter { it.backlogId == backlog.id } }
                }
                BacklogSwipeCard(
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope,
                    
                    backlog = backlog,
                    routineList = routineList,
                    
                    onExpandClick = onExpandChange,
                    onVisibleClick = onVisibleChange,
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
                Spacer(Modifier.height(basePadding))
            }
        },
        floatButtonAction = {
            clickPart = -2
            val backlog = Backlog(
                timeTitle = LocalDate.now().format(formatter)
            )
            updateBacklogUiState(backlog)
            selectedDate = LocalDate.now()
            susAddBacklog(backlog)
            showEditDialog = !showEditDialog
            
        },
        floatButtoncontent = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Backlog"
            )
        }
    )
//    编辑卡片
    if(showEditDialog){
        EditCardDialog(
            clickPart = clickPart,
            
            backlogUiState=backlogUiState,
            routineList = homeRoutineList.filter {it.backlogId == backlogUiState.backlog.id
            },
            updateBacklogUiState = updateBacklogUiState,
            onUpdateBacklog=susUpdateBacklog,
            updateRoutine= updateRoutine,
            onDismiss = {
                showEditDialog = !showEditDialog
                        },
            SortAndSave = { tempRoutineList ->
                var index = 0
                var sortId = 0
                while (index < tempRoutineList.size) {
                    val routine = tempRoutineList[index]
                    if(routine.finished) {
                        insertRoutine(routine.copy(sortId = sortId, finished = false ))
                    }
                    else {
                        updateRoutine(routine.copy(sortId = sortId))
                    }
                    if(routine.content.isNotEmpty()) {
                        sortId++
                    }
                    index++
                }
            },
            
        )
    }
}
val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")!!
