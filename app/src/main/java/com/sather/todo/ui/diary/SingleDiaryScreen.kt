package com.sather.todo.ui.diary

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.sather.todo.ui.AppViewModelProvider
import com.sather.todo.ui.components.basePadding
import com.sather.todo.ui.components.cardSize
import com.sather.todo.ui.diary.components.KeyboardAwareBottomBarWithTimeInsertion
import com.sather.todo.ui.navigation.BaseDestination
import kotlinx.coroutines.launch
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
    viewModel: SingleDiaryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateBack: () -> Unit,
    
    ) {
    val diaryUiState by viewModel.diaryUiState.collectAsState()
    val diary = diaryUiState.diary
    val coroutineScope = rememberCoroutineScope()
    
    
    val textFieldValue = remember(diary.content) { mutableStateOf(TextFieldValue(diary.content)) }
    
    val focusManager = LocalFocusManager.current
    var hasFocus by remember { mutableStateOf(false) }
    // 处理所有保存触发点
    DisposableEffect(hasFocus) {
        onDispose {
            if (!hasFocus) {
                coroutineScope.launch {
                    viewModel.triggerSave(textFieldValue.value.text)
                }
            }
        }
    }
//    处理栈返回
    BackHandler {
            coroutineScope.launch {
                viewModel.triggerSave(textFieldValue.value.text)
                navigateBack()
            }
    }
    Column(
        Modifier.fillMaxSize()
            .imePadding()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { focusManager.clearFocus() }
                )
            }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(basePadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            标题栏
            Text(
                text = diary.timeTitle,
                style = MaterialTheme.typography.headlineLarge
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onBackground,
                thickness = 2.dp,
                modifier = Modifier.width(cardSize)
            )
        }
        
        Spacer(Modifier.height(basePadding))
        // 主内容区域
        BasicTextField(
            value = textFieldValue.value,
            onValueChange = {
                textFieldValue.value = it
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = basePadding * 2)
                .onFocusChanged { hasFocus = it.hasFocus },
            textStyle = MaterialTheme.typography.bodyMedium
        )
//        功能栏
        KeyboardAwareBottomBarWithTimeInsertion(
            onTimeClick = {
                val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())+' '
                val currentCursorPosition = textFieldValue.value.selection.start
                
                val newText = StringBuilder(textFieldValue.value.text).insert(currentCursorPosition, currentTime).toString()
                textFieldValue.value = TextFieldValue(
                    text = newText,
                    selection = TextRange(currentCursorPosition + currentTime.length)
                )
            },
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.triggerSave(textFieldValue.value.text)
                    navigateBack()
                }
            }
        )
        
    }
}
