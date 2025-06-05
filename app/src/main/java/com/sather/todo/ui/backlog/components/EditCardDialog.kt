package com.sather.todo.ui.backlog.components

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.sather.todo.R
import com.sather.todo.data.Backlog
import com.sather.todo.data.Routine
import com.sather.todo.ui.backlog.BacklogUiState
import com.sather.todo.ui.backlog.formatter
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun EditCardDialog(
    onSortAndSave: (List<Routine>) -> Unit,
    dismissDialog:()->Unit,
//    需要判断点击部位
//      -2- empty routine
//      -1- backlog
//      >=0- routine
    clickPart: Int,
    backlogUiState: BacklogUiState,
    routineList: List<Routine>,
    
    updateBacklogUiState: (Backlog) -> Unit,
    onUpdateBacklog:()->Unit,
    
    onUpdateRoutine:(Routine)->Unit,
    ) {
    
    val clipboardManager = LocalClipboardManager.current
    
    
    val backlog = backlogUiState.backlog
    var selectedDate by remember { mutableStateOf(if(backlog.timeTitle.isNotEmpty())LocalDate.parse(backlog.timeTitle, formatter) else LocalDate.now()) }
    var showDatePickerModal by remember { mutableStateOf((clickPart == -1)) }
    
    val listState = rememberLazyListState()
    /*init
    *   初始化焦点-sortId或末尾-
    *   回收利用来保存数据组
    */
    var saveFoucsIndex by remember { mutableIntStateOf(if(clickPart < 0 )routineList.size else clickPart) }
    
    val focusRequester = remember { FocusRequester()}
    var focusIndex by remember { mutableIntStateOf(saveFoucsIndex) }
    
    val tempRoutineList = remember {
        mutableStateListOf<Routine>().apply {
            addAll(routineList)
            if(clickPart < 0) {
                add(
                    Routine(
                        backlogId = backlog.id,
                        sortId = routineList.size,
                        finished = true,
                        rank = 0,
                        content = "",
                    )
                )
            }
        }
    }
    
    var locationInEnd by remember { mutableStateOf(tempRoutineList[focusIndex].content.length) }
    LaunchedEffect(focusIndex) {
        when(isItemVisible(focusIndex, listState)) {
            -1 -> listState.animateScrollToItem(maxOf(0, focusIndex))
            1 -> listState.animateScrollToItem(maxOf(0, focusIndex - 4))
            
        }
    }
    
    if(clickPart != -1) {
        AlertDialog(
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
            ),
            
            modifier = Modifier
                .padding(16.dp)
                .imePadding(),
            text = {
                Column {
                    val titleFocusRequester = remember { FocusRequester() }
                    
                    Row(
//                        点击修改/失焦保存
                        modifier = Modifier.focusRequester(titleFocusRequester),
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
                    LaunchedEffect(saveFoucsIndex) {
                        if (saveFoucsIndex <= 0) {
                            titleFocusRequester.requestFocus()
                            delay(1)
                            if (saveFoucsIndex == -3) {
                                onSortAndSave(tempRoutineList)
                                dismissDialog()
                            }
                        }
                    }
                    
                    
                    LazyColumn(
                        Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "Backlog Edit Card" }
                            .heightIn(max = 250.dp)
                            .animateContentSize()
                            .focusGroup(),
                        state = listState,
                    ) {
                        itemsIndexed(
                            items = tempRoutineList,
                            key = { _, item -> item.id }
                        ) { index, item ->
                            RoutineEditRow(
                                modifier = if (index == focusIndex) Modifier.focusRequester(focusRequester) else Modifier,
                                routine = item.copy(sortId = index),
                                locationInEnd = if (index == focusIndex)locationInEnd else item.content.length,
                                updateRoutine = { routine ->
                                    tempRoutineList[index] = routine
                                },
                                addRoutine = { sortIndex, it, location ->
                                        tempRoutineList.add(
                                            index + sortIndex,
                                            Routine(
                                                backlogId = backlog.id,
                                                sortId = index + 1,
                                                finished = true,
                                                rank =if(it.isEmpty()) 0 else 1 ,
                                                content = it,
                                            )
                                        )
                                    focusIndex = index + sortIndex
                                    locationInEnd = location
                                },
                                deleteRoutine = { legacyTexts ->
                                    if(index != 0) {
                                        locationInEnd = tempRoutineList[index - 1].content.length
                                        tempRoutineList[index-1] = tempRoutineList[index - 1].copy(content = tempRoutineList[index - 1].content+legacyTexts)
                                        println("temp routine:${tempRoutineList[index-1]}\t$locationInEnd")
                                        focusIndex = index - 1
                                    }
                                },
                                onDeleteRoutine = {
                                    val deleteRoutine = tempRoutineList[index].copy(content = "")
                                    tempRoutineList.removeAt(index)
                                    onUpdateRoutine(deleteRoutine)
                                    if(index == 0) {
                                        tempRoutineList.add(
                                            0,
                                            Routine(
                                                backlogId = backlog.id,
                                                sortId = 0,
                                                finished = true,
                                                rank =0 ,
                                                content = "",
                                            )
                                        )
                                    }
                                },
                                focusClick = { newFocusIndex ->
                                    if (focusIndex != newFocusIndex) {
                                        focusIndex = newFocusIndex
                                    }
                                },
                            )
                            LaunchedEffect(index == focusIndex) {
                                if (index == focusIndex) {
                                    focusRequester.requestFocus()
                                }
                            }
                            
                        }
                    }
                }
            },
            onDismissRequest = {
                dismissDialog()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        saveFoucsIndex = -3
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
                CopyToClipboardButton(
                    onClick = {
                        var textToCopy= ""
                        tempRoutineList.filter { it.content.isNotEmpty() }.forEach { textToCopy += it.content+"\n" }
                        clipboardManager.setText(AnnotatedString(textToCopy))
                    }
                )
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
                    saveFoucsIndex = -1
                }
                        },
        )
    }
}
@Composable
fun CopyToClipboardButton(
    onClick: () -> Unit
) {
    
    // 按钮状态
    var isButtonEnabled by remember { mutableStateOf(true) }
    
    LaunchedEffect(isButtonEnabled) {
        if(!isButtonEnabled) {
            delay(3000) // 3 秒延迟
            isButtonEnabled = true
        }
    }
    TextButton(
        modifier = Modifier.animateContentSize(),
        enabled = isButtonEnabled,
        onClick = {
            onClick()
            isButtonEnabled = false
            
        },
    ) {
        Text(
            color = if(isButtonEnabled)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.headlineMedium,
            text = if(isButtonEnabled)stringResource(R.string.copy_action) else stringResource(R.string.copy_done_action)
        )
    }
}

private fun isItemVisible(index: Int, listState: LazyListState): Int {
    val visibleItemsInfo = listState.layoutInfo.visibleItemsInfo
    
    if (visibleItemsInfo.isEmpty()) {
        return -1 // 如果没有可见项，返回-1
    }
    
    val firstVisibleIndex = visibleItemsInfo.first().index
    
    return when {
        index in visibleItemsInfo.map { it.index } -> 0 // index 在可见范围内
        index < firstVisibleIndex -> -1 // index 小于第一个可见索引
        else -> 1 // index 大于最后一个可见索引
    }
}