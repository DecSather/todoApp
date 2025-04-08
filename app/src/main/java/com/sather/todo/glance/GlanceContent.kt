package com.sather.todo.glance

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.appwidget.CheckBox
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.sather.todo.R
import com.sather.todo.data.Routine
import com.sather.todo.glance.data.BacklogWidgetRepository.Companion.getBacklogRepo
import com.sather.todo.glance.data.RoutineWidgetRepository.Companion.getRoutineRepo
import com.sather.todo.ui.backlog.formatter
import com.sather.todo.ui.theme.MyAppWidgetGlanceColorScheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class MyAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MyAppWidget()
}
class MyAppWidget : GlanceAppWidget() {
    
    override suspend fun provideGlance(context: Context, glanceId: GlanceId) {
        val backlogRepo = getBacklogRepo(context,glanceId)
        val routineRepo = getRoutineRepo(context,glanceId)
        val initTimeTitle = LocalDate.now().format(formatter)
        
        val initRoutines = withContext(Dispatchers.Default) {
            routineRepo.load(backlogRepo.load(initTimeTitle))
        }
        provideContent{
            val coroutineScope = rememberCoroutineScope()
            
            val routines by routineRepo.routines().collectAsState(initial = initRoutines)
            val finishedIds by routineRepo.finishedIds().collectAsState(initial = emptySet())
            var timeTitle by remember { mutableStateOf(initTimeTitle) }
            var currentTime by remember { mutableStateOf(LocalDate.now()) }
            GlanceTheme(colors = MyAppWidgetGlanceColorScheme.colors) {
                MyWidgetContent(
                    timeTitle = timeTitle+"-${routines.size-finishedIds.size}",
                    items = routines,
                    refreshClick = {
                        coroutineScope.launch {
                            routineRepo.load(backlogRepo.load(timeTitle))
                        }
                    },
                    beforeClick = {
                        currentTime = currentTime.minusDays(1)
                        timeTitle = currentTime.format(formatter)
                    },
                    afterClick = {
                        currentTime = currentTime.plusDays(1)
                        timeTitle = currentTime.format(formatter)
//                    refresh = true
                    },
                    isFinished = {
                        finishedIds.contains(it)
                                 },
                    onFinishedChange = { id->
                        coroutineScope.launch {
                            routineRepo.toggleFinished(id)
                        }
                    }
                )
            }
        }
    }
    
    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview
    @Composable
    fun MyWidgetContent(
        timeTitle: String,
        items:List<Routine>,
        refreshClick :() ->Unit,
        beforeClick:() ->Unit,
        afterClick :()->Unit,
        isFinished:(Long)->Boolean,
        onFinishedChange:(Long) ->Unit,
    ) {
        Scaffold(
            titleBar = titleBar(
                timeTitle,
                refreshClick = refreshClick,
                beforeClick = {
                    beforeClick()
                    refreshClick()
                },
                afterClick = {
                    afterClick()
                    refreshClick()
                }
            )
        ){
            Column(
                modifier = GlanceModifier.fillMaxSize().padding(8.dp)
            ) {
                // List
                if (items.isEmpty()) {
                    Box(
                        modifier = GlanceModifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "_(:з」∠)_",
                            style = TextStyle(
                                fontSize = 24.sp
                            )
                        )
                    }
                }else {
                    LazyColumn(GlanceModifier.fillMaxWidth()) {
                        items(
                            items = items,
                            itemId = { item -> item.id},
                        ){item ->
                            ListRow(
                                item,
                                isFinished = isFinished(item.id),
                                onFinishedChange = {onFinishedChange(item.id)}
                            )
                            
                        }
                    }
                }
            }
        }
    }
    private fun titleBar(
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
        routine: Routine,
        isFinished:Boolean,
        onFinishedChange:() ->Unit,
    ) {
        Column {
            Row(
                modifier = GlanceModifier.fillMaxWidth().wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if(!isFinished) {
                    CheckBox(
                        checked = isFinished,
                        onCheckedChange = onFinishedChange
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
}