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
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.sather.todo.R
import com.sather.todo.data.Routine
import com.sather.todo.glance.workmanager.*
import com.sather.todo.ui.backlog.formatter
import com.sather.todo.ui.theme.MyAppWidgetGlanceColorScheme
import java.time.LocalDate

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

//        结束应用又进入退出应用也会重新创建widget，不确定要不要改bug
        var currentTime by remember { mutableStateOf(LocalDate.now()) }
        var timeTitle by remember { mutableStateOf(currentTime.format(formatter)) }
        // 从 SharedPreferences 中读取数据
        var items = getRoutinesFromDataStore(context).collectAsState(initial = emptyList()).value
        LaunchedEffect(timeTitle) {
            triggerUpdateWidgetWorker(context, timeTitle)
        }
        Scaffold(
            titleBar = titleBar(
                timeTitle,
                beforeClick = {
                    currentTime = currentTime.minusDays(1)
                    timeTitle = currentTime.format(formatter)
                },
                afterClick = {
                    currentTime = currentTime.plusDays(1)
                    timeTitle = currentTime.format(formatter)
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
                            ListRow(item)
                            
                        }
                    }
                }
            }
        }
        
    }
    fun titleBar(
        timeTile:String,
        beforeClick:()->Unit,
        afterClick :()->Unit
    ): @Composable (() -> Unit) = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            
            ) {
            Spacer(modifier = GlanceModifier.width(16.dp))
            
            Text(
                text = timeTile,
                style = TextStyle(
                    fontSize = 24.sp,
                ),
            )
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {CircleIconButton(
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
        routine: Routine,
    ) {
        var isFinished by remember { mutableStateOf(routine.finished) }
        val context = LocalContext.current
        LaunchedEffect(isFinished) {
            if(isFinished) {
                triggerUpdateRoutine(
                    context = context,
                    id = routine.id,
                    finished = true
                )
            }
        }
        if(!isFinished) {
            Column {
                Row(
                    modifier = GlanceModifier.fillMaxWidth().wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CheckBox(checked = isFinished,
                        onCheckedChange = {
                            isFinished = true
                            
                        }
                    )
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    Text(text = routine.content)
                }
                Spacer(GlanceModifier.fillMaxWidth().height(1.dp).background(GlanceTheme.colors.secondaryContainer))
            }
        }
    }
    
    // routine完成更新：一次性workmanager存数据，
    fun triggerUpdateRoutine(context: Context, id: Long, finished: Boolean) {
        println("triggerUpdateRoutine")
        val inputData = workDataOf(
            "id" to id,
            "finished" to finished
            
        )
        val updateRequest = OneTimeWorkRequestBuilder<UpdateRoutineWorker>()
            .setInputData(inputData)
            .build()
        WorkManager.getInstance(context).enqueue(updateRequest)
    }
    suspend fun triggerUpdateWidgetWorker(context: Context, timeTile: String  ) {
        // 存timeTitle
        saveTimeTitleToDataStore(context, timeTile)
        // 通过timeTitle存新routineList
        val updateRequest = OneTimeWorkRequestBuilder<UpdateWidgetWorker>()
            .build()
        
        // 执行更新命令-一次性的
        WorkManager.getInstance(context).enqueue(updateRequest)
    }
}