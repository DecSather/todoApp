package com.sather.todo.ui.backlog.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
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
    SortAndSave: (List<Routine>) -> Unit,
    onDismiss:()->Unit,
//    需要判断点击部位
//      -2- empty routine
//      -1- backlog
//      >=0- routine
    clickPart: Int,
    backlogUiState: BacklogUiState,
    routineList: List<Routine>,
    
    updateBacklogUiState: (Backlog) -> Unit,
    onUpdateBacklog:()->Unit,
    
    updateRoutine:(Routine)->Unit,
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
    var saveFoucsIndex by remember { mutableIntStateOf(if(clickPart < 0)routineList.size else clickPart) }
    
    val focusRequester = remember { FocusRequester()}
    var focusIndex by remember { mutableIntStateOf(saveFoucsIndex) }
    
    val tempRoutineList = remember {
        mutableStateListOf<Routine>().apply {
            addAll(routineList)
            if(clickPart <= 0) {
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
    
    var locationInEnd by remember { mutableStateOf(true) }
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
                                SortAndSave(tempRoutineList)
                                onDismiss()
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
                            BacklogEditRow(
                                modifier = if (index == focusIndex) Modifier.focusRequester(focusRequester) else Modifier,
                                routine = item.copy(sortId = index),
                                locationInEnd = locationInEnd,
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
                                deleteRoutine = {
                                    val deleteRoutine = tempRoutineList[index]
                                    updateRoutine(deleteRoutine)
                                    tempRoutineList.removeAt(index)
                                    if (tempRoutineList.size == 0) tempRoutineList.add(
                                        0,
                                        Routine(
                                            backlogId = backlog.id,
                                            sortId = index + 1,
                                            finished = true,
                                            rank = 0,
                                            content = "",
                                        )
                                    )
                                    
                                },
                                focusClick = { newFocusIndex ->
                                    if (focusIndex != newFocusIndex) {
                                        focusIndex = newFocusIndex
                                        if(!locationInEnd)locationInEnd = true
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
                onDismiss()
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
@Composable
fun BacklogEditRow(
    modifier: Modifier,
    routine: Routine,
    locationInEnd:Boolean,
    updateRoutine: (Routine) -> Unit ={ },
    addRoutine: (Int,String,Boolean) -> Unit,
    deleteRoutine:() -> Unit,
    focusClick:(Int) -> Unit,
) {
    
    var animFlag by remember { mutableStateOf(false) }
    
    val animVisibleState = remember {  MutableTransitionState(true).apply {  targetState = true  }  }
    LaunchedEffect(animVisibleState.currentState) {

        if (!animVisibleState.targetState &&
            !animVisibleState.currentState
        ) {
            deleteRoutine()
        }
    }
    var contentFieldValueState by remember {
        mutableStateOf(
            TextFieldValue(
                text = routine.content,
                selection = TextRange(if(locationInEnd)routine.content.length else 0),
            )
        )
    }
    var creditFieldValueState by remember {
        mutableStateOf(
            TextFieldValue(
                text = routine.credit.toString(),
                selection = TextRange(routine.credit.toString().length),
            )
        )
    }
    var contentwasFocused by remember { mutableStateOf(false) }
    var contentwasEmpty by remember { mutableStateOf(false) }
    var creditwasFocused by remember { mutableStateOf(false) }
//        文本输入框
    AnimatedVisibility(
        visibleState = animVisibleState,
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .padding(start = TabSpacer),
            verticalAlignment = Alignment.CenterVertically
        ) {
            
            RankAnimRow(
                text = "Routine No.${routine.sortId}",
                rank = routine.rank,
                selected = animFlag,
                onSelected = { animFlag = !animFlag },
                onClicked = { updateRoutine(routine.copy(rank = it)) }
            )
            BasicTextField(
                modifier = modifier
                    .weight(1f)
                    .padding(start = TabSpacer)
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.key == Key.Delete || keyEvent.key == Key.Backspace) {
                            if (contentFieldValueState.text.isEmpty() && !contentwasEmpty) {
                                contentwasEmpty = true
                                false
                            } else if (contentFieldValueState.text.isEmpty() && contentwasEmpty) {
                                focusClick(routine.sortId - 1)
                                animVisibleState.targetState = false
                                true
                            }
                        }
                        false
                    }
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            if(!contentwasFocused) {
                                contentwasFocused = true
                                focusClick(routine.sortId)
                            }
                        } else if (contentwasFocused) {
                            if (routine.rank == 0)
                                updateRoutine(routine.copy(content = contentFieldValueState.text, rank = 1))
                            else updateRoutine(routine.copy(content = contentFieldValueState.text))
                            contentwasFocused = false
                        }
                        
                    },
                singleLine = true,
                value = contentFieldValueState,
                onValueChange = { textFieldValue ->
                    contentFieldValueState = textFieldValue
                    if (textFieldValue.text.contains("\n")) {
                        val lines = textFieldValue.text.split("\n")
                        lines.forEachIndexed { index, lineText ->
                            if (index == 0) contentFieldValueState = contentFieldValueState.copy(text = lineText)
                            else addRoutine(
                                index,
                                lineText,
                                true
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        val oldText = contentFieldValueState.text.subSequence(0,contentFieldValueState.selection.end)
                        val newText = contentFieldValueState.text.substring(contentFieldValueState.selection.end)
                        contentFieldValueState = TextFieldValue(text = oldText.toString())
                        addRoutine(1, newText,false)
                    }
                ),
                textStyle = MaterialTheme.typography.bodyMedium,
            )
//        分数输入框
            Text(
                text = stringResource(R.string.dollarSign),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            BasicTextField(
                modifier = Modifier.width(68.dp)
                    .padding(start = TabSpacer)
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            creditwasFocused = true
                        } else if (creditwasFocused) {
                            val credit = creditFieldValueState.text.toFloatOrNull()
                            if (credit != null) updateRoutine(routine.copy(credit = credit))
                            else updateRoutine(routine.copy(credit = 1f))
                            creditwasFocused = false
                        }
                    },
                singleLine = true,
                value = creditFieldValueState,
                onValueChange = { creditFieldValue ->
                    creditFieldValueState = creditFieldValue
                },
                
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        addRoutine(1, "",true)
                    }
                ),
                textStyle = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun RankAnimRow(
    text: String,
    rank:Int,
    selected: Boolean,
    onSelected: () -> Unit,
    onClicked: (Int) -> Unit,
) {
    
    Row(
        modifier = Modifier
            .animateContentSize()
            .height(TabHeight)
            .clearAndSetSemantics { contentDescription = text },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (selected) {
            RoutineColors.forEachIndexed{
                index,color ->
                if(index>0)
                    RankColorBox(
                        {
                            onClicked(index)
                            onSelected()
                        },
                        color,
                    )
            }
        }else{
            RankColorBox(
                onSelected,
                RoutineColors[rank]
            )
        }
    }
}

@Composable
private fun RankColorBox(
    onClicked: () -> Unit,
    color: Color,
){
    Row(
        modifier = Modifier
            .height(BoxSize + TabSpacer *2 )
            .clip(RoundedCornerShape(percent = 50))
            .clickable{ onClicked() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(TabSpacer))
        Box(
            modifier = Modifier
                .size(BoxSize) // 设置圆的大小
                .clip(RoundedCornerShape(roundCornerShape))
//                设置颜色改展开列动画或弹出卡片
                .background(color = color)
            ,
        )
        Spacer(modifier = Modifier.width(TabSpacer))
    }
}
private val TabHeight = 42.dp
private val TabSpacer = 10.dp
private val BoxSize = 16.dp
private val roundCornerShape = 2.dp

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