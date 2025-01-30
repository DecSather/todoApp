package com.sather.todo.ui.backlog

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.sather.todo.R
import com.sather.todo.data.Backlog
import com.sather.todo.data.Routine
import com.sather.todo.ui.components.RoutineColors
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.absoluteValue


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun EditCardDialog(
    onDismiss: (List<Routine>) -> Unit,
//    需要判断点击部位
//      -2- empty routine
//      -1- backlog
//      >=0- routine
    clickPart: Int,
    
    backlogUiState: BacklogUiState,
    routineList: List<Routine>,
    
    updateBacklogUiState: (Backlog) -> Unit,
    onUpdateBacklog:()->Unit,
    
    
    ) {
    
    val backlog = backlogUiState.backlog
    val initialDate =LocalDate.parse(backlog.timeTitle, formatter)
    var selectedDate by remember { mutableStateOf(initialDate) }
    var showDatePickerModal by remember { mutableStateOf((clickPart == -1)) }
    
    val tempRoutineList = remember {
        mutableStateListOf<Routine>().apply {
            addAll(routineList)
            if(clickPart == -2) {
                if(routineList.size == 0) {
                    add(
                        Routine(
                            id = clickPart,
                            backlogId = backlog.id,
                            sortId = 0,
                            rank = 3,
                            content = "",
                        )
                    )
                }else{
                    add(
                        Routine(
                            id = -routineList.last().id-3,
                            backlogId = backlog.id,
                            sortId = routineList.size,
                            rank = 3,
                            content = "",
                        )
                    )
                }
            }
        }
    }
    
    
    val listState = rememberLazyListState()
    var targetIndex by remember { mutableStateOf(if(clickPart !=-2)clickPart else routineList.size) }
    
    val focusRequester = remember { FocusRequester() }
    
    if(clickPart != -1) {
        LaunchedEffect(targetIndex) {
            if(targetIndex>=0) {
                listState.animateScrollToItem(targetIndex)
                delay(1)
                focusRequester.requestFocus()
            }else{
                listState.animateScrollToItem(0)
                delay(1)
                focusRequester.requestFocus()
                onDismiss(tempRoutineList)
            }
        }
        AlertDialog(
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
            ),
            
            modifier = Modifier
                .padding(16.dp)
                .heightIn(max = 480.dp)
                .imePadding(),
            text = {
                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Backlog Edit Card" }
                        .heightIn(max =300.dp)
                        .animateContentSize()
                        .focusGroup(),
                    state = listState,

                    ) {
                    item {
                        Row(
                            modifier =if(targetIndex == -1) Modifier.focusRequester(focusRequester) else Modifier,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
//                        时间选择
                            OutlinedTextField(
                                value = selectedDate.format(DateTimeFormatter.ISO_DATE),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.formatter)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp)
                                    .pointerInput(selectedDate) {
                                        awaitEachGesture {
                                            awaitFirstDown(pass = PointerEventPass.Initial)
                                            val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                            if (upEvent != null) {
                                                showDatePickerModal = true
                                            }
                                        }
                                    },
                            )
                        }
                    }
                    itemsIndexed(items = tempRoutineList, key = { _,item -> item.id}) {
                        index,item ->
                        BacklogEditRow(
                            modifier =if(index == targetIndex) Modifier.focusRequester(focusRequester) else Modifier,
                            routine = item.copy(sortId = index),
                            updateRoutine = { routine ->
                                tempRoutineList[index]=routine
                                              },
                            addRoutine = { newKey ->
                                tempRoutineList.add(index+1,
                                    Routine(
                                        id = -newKey-3,
                                        backlogId = backlog.id,
                                        sortId = index+1,
                                        rank = 3,
                                        content = "",
                                    )
                                )
                            },
                            changeTarget = {newIndex ->
                                targetIndex = newIndex
                            },
                        )
                    }
                }
                
            },
            onDismissRequest = { onDismiss(tempRoutineList.toList()) },
            confirmButton = {
                TextButton(
                    onClick = {
                        /*
                        * 三个操作：
                        * * 更新Backlog标题
                        */
                        targetIndex = -1
                        onUpdateBacklog()
                    },
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineMedium,
                        text = stringResource(R.string.save_action),
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onDismiss(tempRoutineList.toList()) },
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineMedium,
                        text = stringResource(R.string.cancel_action),
                        
                        )
                }
            }
        )
    }
    if (showDatePickerModal) {
        DatePickerModal(
            selectedDate = selectedDate,
            onDateSelected = {
                selectedDate = it
                updateBacklogUiState(
                    backlog.copy(
                        timeTitle = selectedDate.format(DateTimeFormatter.ISO_DATE)
                    )
                )
            },
            onDismiss = {
                showDatePickerModal = false
                if(clickPart == -1) {
                    onUpdateBacklog()
                    onDismiss(listOf())
                }
                        },
        )
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd")
    return formatter.format(Date(millis))
}

fun convertLocalDateToMillis(localDate: LocalDate): Long {
    val zonedDateTime = localDate.atStartOfDay(ZoneOffset.UTC)
    return zonedDateTime.toInstant().toEpochMilli()
}

@Composable
fun BacklogEditRow(
    modifier: Modifier,
    routine: Routine,
    updateRoutine: (Routine) -> Unit ={ },
    addRoutine: (Int) -> Unit = {},
    changeTarget: (Int) -> Unit,
) {
    var textFieldValueState by remember {
        mutableStateOf(
            TextFieldValue(
                text = routine.content,
                selection = TextRange(routine.content.length),
            )
        )
    }
    var wasFocused by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .height(68.dp)
            .padding(start = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        
        Box(
            modifier = Modifier
                .size(20.dp) // 设置圆的大小
                .clip(RoundedCornerShape(percent = 50))
//                设置颜色改展开列动画或弹出卡片
                .background(color = RoutineColors[routine.rank])
        )
        OutlinedTextField(
            modifier = modifier
                .fillMaxWidth()
                .onFocusEvent { focusState ->
                    if(focusState.isFocused) {
                        wasFocused = true
                    }
                    if(!focusState.isFocused && wasFocused){
                        println("update from focusState: "+routine.sortId)
                        if(textFieldValueState.text.isNotEmpty() && routine.rank == 3){
                            updateRoutine(routine.copy(content = textFieldValueState.text, rank = 1))
                        }else{
                            updateRoutine(routine.copy(content = textFieldValueState.text))
                        }
                        wasFocused = false
                    }
                },
            placeholder = {
                Text(
                    text = stringResource(R.string.routine_empty_error),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            value = textFieldValueState,
            onValueChange = {
                textFieldValueState = it
            },
            
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    addRoutine(routine.id.absoluteValue)
                    changeTarget(routine.sortId+1)
                }
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
        )
        
    }
}
