package com.sather.todo.ui.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.sather.todo.ui.components.LargeHeight
import com.sather.todo.ui.components.basePadding
import com.sather.todo.ui.components.cardSize
import com.sather.todo.ui.diary.components.KeyboardAwareBottomBarWithTimeInsertion
import com.sather.todo.ui.navigation.BaseDestination
import com.sather.todo.ui.theme.ToDoTheme
import java.text.SimpleDateFormat
import java.util.*

object SingleDiaryDestination : BaseDestination {
    override val route = "single_diary"
    override val icon = Icons.Filled.ModeEdit
    const val diaryIdArg = "diaryId"
    val routeWithArgs = "$route/{$diaryIdArg}"
    val arguments = listOf(navArgument(diaryIdArg) {
        type = NavType.LongType
    })
}
@Composable
fun SingleDiaryScreen(
    timeTitle:String = "",
    content:String = ""
) {
    var text by remember { mutableStateOf(content) }
    val textFieldValue = remember { mutableStateOf(TextFieldValue(text)) }
//        上半部分
    Column(
        modifier = Modifier.fillMaxSize().height(LargeHeight).imePadding()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    
    ) {
//            标题栏
        Text(
            text = timeTitle,
            style = MaterialTheme.typography.headlineLarge
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.onBackground,
            thickness = 2.dp,
            modifier = Modifier.width(cardSize)
        )
        Spacer(Modifier.height(basePadding))
        // 主内容区域
        BasicTextField(
            value = textFieldValue.value,
            onValueChange = {
                textFieldValue.value = it
                text = it.text
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = basePadding * 2),
            textStyle = MaterialTheme.typography.bodyMedium
        )
//        功能栏
        KeyboardAwareBottomBarWithTimeInsertion {
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())+' '
            val currentCursorPosition = textFieldValue.value.selection.start
            
            val newText = StringBuilder(text).insert(currentCursorPosition, currentTime).toString()
            text = newText
            textFieldValue.value = TextFieldValue(
                text = newText,
                selection = TextRange(currentCursorPosition + currentTime.length)
            )
        }
    }

}

@Preview(showSystemUi = true)
@Composable
fun SingleDiaryScreenPreview(){
    ToDoTheme {
        SingleDiaryScreen(
            "2020-02-02",
            "hello world!"
        )
    }
}