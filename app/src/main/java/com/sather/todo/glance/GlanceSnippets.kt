package com.sather.todo.glance

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.appwidget.*
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.layout.*
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.sather.todo.R
import com.sather.todo.data.Routine
import com.sather.todo.glance.workmanager.UpdateWidgetWorker
import com.sather.todo.glance.workmanager.getDataFromDataStore
import com.sather.todo.ui.theme.MyAppWidgetGlanceColorScheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

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
        val workManager = WorkManager.getInstance(context)
        val updateRequest = OneTimeWorkRequestBuilder<UpdateWidgetWorker>().build()
        workManager.enqueue(updateRequest)
        val items =  runBlocking {
            getDataFromDataStore(context).first()
        }
        // 从 SharedPreferences 中读取数据
        val timeTitle = "2025-03-13"
        
//        val timeTile = "2022-01-01"
//        val items = listOf(
//            Routine(
//                sortId = 0,
//                content = "nothing",
//                backlogId = -1
//            )
//        )
        Scaffold(
//                    backgroundColor = GlanceTheme.colors.secondaryContainer,
            titleBar = titleBar(timeTitle)
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
                        items(items) { item ->
                            ListRow(item)
                        }
                    }
                }
            }
        }
        
    }
    
}
fun titleBar( timeTile:String): @Composable (() -> Unit) = {
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
            onClick = {}
        )
            CircleIconButton(
                imageProvider = ImageProvider(R.drawable.sample_arrow_right_icon),
                contentDescription = "圆按钮",
                contentColor = GlanceTheme.colors.secondary,
                backgroundColor = null, // transparent
                onClick = {}
            )
        }
    }
    Spacer(GlanceModifier.height(1.dp).background(GlanceTheme.colors.secondaryContainer))
}
@Composable
fun ListRow(
    routine: Routine,
) {
    Column {
        Row(
            modifier = GlanceModifier.fillMaxWidth().wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CheckBox(checked = routine.finished, onCheckedChange = { /* Handle checkbox state change */ })
            Spacer(modifier = GlanceModifier.width(8.dp))
            Text(text = routine.content)
        }
        Spacer(GlanceModifier.fillMaxWidth().height(1.dp).background(GlanceTheme.colors.secondary))
    }
}
