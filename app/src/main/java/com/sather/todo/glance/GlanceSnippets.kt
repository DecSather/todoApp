package com.sather.todo.glance

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.appwidget.*
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.color.ColorProviders
import androidx.glance.layout.*
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.sather.todo.R
import com.sather.todo.data.Routine
import com.sather.todo.data.RoutinesRepository
import com.sather.todo.ui.theme.MyAppWidgetGlanceColorScheme
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
}
@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
fun MyWidgetContent() {
    val timeTile = "2022-01-01"
    val items = listOf(
        Routine(
            sortId = 0,
            content = "nothing",
            backlogId = -1
        )
    )
    Scaffold(
//                    backgroundColor = GlanceTheme.colors.secondaryContainer,
        titleBar = titleBar(timeTile)
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize().padding(8.dp)
        ) {
            // List
            
            LazyColumn {
                items(items) { item ->
                    ListItem(item)
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
    Spacer(GlanceModifier.height(1.dp).background(GlanceTheme.colors.secondary))
}
@Composable
fun ListItem(
    routine: Routine,
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CheckBox(checked = false, onCheckedChange = { /* Handle checkbox state change */ })
        Spacer(modifier = GlanceModifier.width(8.dp))
        Text(text = routine.content)
    }
}
