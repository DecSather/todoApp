package com.sather.todo.glance

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.appwidget.*
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.layout.*
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.work.*
import com.sather.todo.R
import com.sather.todo.data.BacklogDatabase
import com.sather.todo.data.Routine
import com.sather.todo.glance.workmanager.*
import com.sather.todo.ui.backlog.formatter
import com.sather.todo.ui.theme.MyAppWidgetGlanceColorScheme
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.util.concurrent.TimeUnit

class MyAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MyAppWidget()
}
class MyAppWidget : GlanceAppWidget() {
    
    
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent{
            
            GlanceTheme(colors = MyAppWidgetGlanceColorScheme.colors) {
                
                MyWidgetContent()
            }
        }
    }
    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview
    @Composable
    fun MyWidgetContent() {
        val context = LocalContext.current

        
        var currentTime by remember { mutableStateOf(LocalDate.now()) }
        var timeTitle by remember { mutableStateOf(currentTime.format(formatter)) }
//        刷新逻辑
        var refresh by remember { mutableStateOf(false) }
        LaunchedEffect(refresh) {
            if(refresh) {
                delay(300)
                triggerUpdateWidgetWorker(context, timeTitle)
                refresh = false
            }
        }
        
        // 从 SharedPreferences 中读取数据
        var items = getRoutinesFromDataStore(context).collectAsState(initial = emptyList()).value
        
        Scaffold(
            titleBar = titleBar(
                timeTitle,
                refreshClick = {
                    refresh = true
                },
                beforeClick = {
                    currentTime = currentTime.minusDays(1)
                    timeTitle = currentTime.format(formatter)
                    refresh = true
                              },
                afterClick = {
                    currentTime = currentTime.plusDays(1)
                    timeTitle = currentTime.format(formatter)
                    refresh = true
                }
            )
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize().padding(8.dp)
            ) {
                // List
                if (items.isEmpty()) {
                    Box(
                        modifier = GlanceModifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Loading...")
                    }
                }else {
                    LazyColumn(GlanceModifier.fillMaxWidth()) {
                        items(
                            items = items,
                            itemId = {item -> item.id},
                        ){item ->
                            ListRow(
                                context,
                                item
                            )
                            
                        }
                    }
                }
            }
        }
        
    }
    fun titleBar(
        timeTile:String,
        refreshClick :() ->Unit,
        beforeClick:() ->Unit,
        afterClick :()->Unit
    ): @Composable (() -> Unit) = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            
            ) {
            Spacer(modifier = GlanceModifier.width(16.dp))
            
            Text(
                text = timeTile,
                style = TextStyle(
                    color =GlanceTheme.colors.onSurface,
                    fontSize = 20.sp,
                ),
            )
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                CircleIconButton(
                    imageProvider = ImageProvider(R.drawable.sample_refresh_icon),
                    contentDescription = "圆按钮",
                    contentColor = GlanceTheme.colors.secondary,
                    backgroundColor = null, // transparent
                    onClick = refreshClick
                )
                CircleIconButton(
                    imageProvider = ImageProvider(R.drawable.sample_arrow_left_icon),
                    contentDescription = "圆按钮",
                    contentColor = GlanceTheme.colors.secondary,
                    backgroundColor = null, // transparent
                    onClick = beforeClick
                )
                CircleIconButton(
                    imageProvider = ImageProvider(R.drawable.sample_arrow_right_icon),
                    contentDescription = "圆按钮",
                    contentColor = GlanceTheme.colors.secondary,
                    backgroundColor = null, // transparent
                    onClick = afterClick
                )
            }
        }
        Spacer(GlanceModifier.height(1.dp).background(GlanceTheme.colors.secondary))
    }
    @Composable
    fun ListRow(
        context:Context,
        routine: Routine,
    ) {
        
        var isFinished by remember { mutableStateOf(routine.finished) }
        
        LaunchedEffect(isFinished) {
            if(isFinished) {
                triggerUpdateRoutine(
                    context = context,
                    id = routine.id,
                    finished = true
                )
            }
        }
            Column {
                Row(
                    modifier = GlanceModifier.fillMaxWidth().wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if(!isFinished) {
                        CheckBox(
                            checked = false,
                            onCheckedChange = {
                                isFinished = true
                            }
                        )
                    }
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    Text(
                        text = routine.content,
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface
                        )
                    )
                }
                Spacer(GlanceModifier.fillMaxWidth().height(1.dp).background(GlanceTheme.colors.secondaryContainer))
            }
        
    }
    
    // routine完成更新：一次性workmanager存数据，
    fun triggerUpdateRoutine(context: Context, id: Long, finished: Boolean) {
        val inputData = workDataOf(
            "id" to id,
            "finished" to finished
            
        )
        val updateRequest = OneTimeWorkRequestBuilder<UpdateRoutineWorker>()
            .setInputData(inputData)
            .build()
        
        // 并行执行多个任务
        WorkManager.getInstance(context).enqueueUniqueWork(
            "update_routine_${id}", // 唯一任务名称
            ExistingWorkPolicy.REPLACE ,
            updateRequest // 任务请求
        )
    }
    suspend fun triggerUpdateWidgetWorker(context: Context, timeTile: String) {
        // 存timeTitle
        saveTimeTitleToDataStore(context, timeTile)
        // 通过timeTitle存新routineList
        val updateRequest = OneTimeWorkRequestBuilder<UpdateWidgetWorker>()
            .build()
        
        // 执行更新命令-一次性的
        WorkManager.getInstance(context).enqueueUniqueWork(
            "update_time_title_${timeTile}", // 唯一任务名称
            ExistingWorkPolicy.REPLACE, // 替换已有任务
            updateRequest // 任务请求
        )
    }
}