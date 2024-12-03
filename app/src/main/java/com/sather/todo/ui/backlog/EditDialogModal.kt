package com.sather.todo.ui.backlog

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.compose.sather.R
import com.sather.todo.data.Backlog
import com.sather.todo.data.Routine
import com.sather.todo.ui.components.RoutineColors
import com.sather.todo.ui.routine.RoutineUiState
import com.sather.todo.ui.theme.unfinishedColor
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*




@Composable
fun EditDialogModal(
    onDateSelected: () -> Unit,
    onDismiss: () -> Unit,
//    需要判断点击部位
//      -2- empty routine
//      -1- backlog
//      >=0- routine
    clickPart:Int,
    routineList:List<Routine>,
    backlogUiState: BacklogUiState,
    routineUiState : RoutineUiState,
    updateBacklogUiState:(Backlog) -> Unit,
    insertRoutineClick:() ->Unit,
    updateRoutineUiState:(Routine) -> Unit,
    onModelSaveClick :() ->Unit,
    onSaveRoutine: () -> Unit
) {
    AlertDialog(
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        ),
        modifier = Modifier
            .padding(16.dp),
        text = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Backlog Edit Card" }
                    .animateContentSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
//                        时间选择
                    showDatePickerDialog(
                        modifier =Modifier,
                        clickPart == -1,
                        backlog = backlogUiState.backlog,
                        updateBacklogUiState = updateBacklogUiState,
                    )
                }
                /*
                * 考虑使用backlog点击编辑删除列编辑*/
                routineList.map { it ->
                    BacklogEditRow(
                        modifier =Modifier,
                        routine =it,
                        routineUiState =routineUiState,
                        onRoutineValueChange = updateRoutineUiState,
                        onSaveRoutine = onSaveRoutine,
                    
                    )
                    if (it.id==clickPart)
                        println("cur focused: "+it.id)
                }
                BacklogEmptyRow(
                    modifier =Modifier,
                    onValueChange = {},
                )
            }
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                onDateSelected()
                onModelSaveClick()
                onDismiss()
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
            TextButton(onClick = onDismiss) {
                Text(
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineMedium,
                    text = stringResource(R.string.cancel_action),
                    
                    )
            }
        }
    )
}
@Composable
fun showDatePickerDialog(
    modifier: Modifier,
    onForceShowDate:Boolean=false,
    backlog: Backlog,
    updateBacklogUiState:(Backlog) -> Unit,
) {
    println("edit backlog: "+backlog)
    val initialDate =
        if(backlog.timeTitle.isNotEmpty())
            LocalDate.parse(backlog.timeTitle, formatter)
        else LocalDate.now()
    
    var selectedDate by remember { mutableStateOf(initialDate) }
    
    var showModal by remember { mutableStateOf(onForceShowDate) }
    OutlinedTextField(
        value = selectedDate.format(DateTimeFormatter.ISO_DATE),
        onValueChange = {},
        label = { Text(stringResource(R.string.formatter)) },
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = Color.Transparent,
            focusedBorderColor =MaterialTheme.colorScheme.primary,
            unfocusedBorderColor =  MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp)
            .pointerInput(selectedDate) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showModal = true
                    }
                }
            },
    )
    if (showModal) {
        DatePickerModal(
            selectedDate =selectedDate ,
            onDateSelected = {
                selectedDate = it
                updateBacklogUiState(backlog.copy(timeTitle = selectedDate.format(DateTimeFormatter.ISO_DATE)))
            },
            onDismiss = { showModal = false },
            
            )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    selectedDate : LocalDate,
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(selectedDate?.let { convertLocalDateToMillis(it) })
    val datePickerColors = DatePickerDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surface,
        weekdayContentColor =MaterialTheme.colorScheme.primary,
        todayDateBorderColor =MaterialTheme.colorScheme.primary,
        selectedYearContainerColor =MaterialTheme.colorScheme.primary,
        selectedDayContainerColor = MaterialTheme.colorScheme.primary,
        navigationContentColor =MaterialTheme.colorScheme.primary,
        dividerColor = MaterialTheme.colorScheme.primary,
        todayContentColor = MaterialTheme.colorScheme.primary,
        currentYearContentColor = MaterialTheme.colorScheme.primary,
    )
    DatePickerDialog(
        colors= datePickerColors,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(LocalDate.parse(datePickerState.selectedDateMillis?.let { convertMillisToDate(it) }, formatter) )
                    onDismiss()
                }
            ) {
                Text(
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineMedium,
                    text = stringResource(R.string.save_action),
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineMedium,
                    text = stringResource(R.string.cancel_action),
                    
                    )
            }
        }
    ) {
        DatePicker(
            colors=datePickerColors,
            state = datePickerState,
            title = {},
            dateFormatter = DatePickerDefaults.dateFormatter("yyyy-MM","yyyy-MM-dd","yyyy-MM-dd")
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
    routineUiState : RoutineUiState,
    onRoutineValueChange:(Routine)->Unit,
    onSaveRoutine:() ->Unit,
){
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
            placeholder = { Text(text= stringResource(R.string.routine_empty_error), style = MaterialTheme.typography.titleMedium) },
            value = if(routineUiState.routine.id==routine.id )routineUiState.routine.content else routine.content,
            onValueChange= {it ->
                if(routineUiState.routine.id!=routine.id )
                    onRoutineValueChange(routine.copy(id = routine.id) )
                onRoutineValueChange(routine.copy(content = it) )
                           },
            
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
            ),
            textStyle =  MaterialTheme.typography.bodyMedium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            modifier = modifier
                .fillMaxWidth()
                .onFocusChanged {
                    if(it.hasFocus) {
                        onSaveRoutine()
                    }
                                },
        )

    }
}
@Composable
fun BacklogEmptyRow(
    modifier: Modifier,
    onValueChange:(Routine)->Unit
){
    Row(
        modifier = Modifier
            .height(68.dp)
            .padding(start = 10.dp)
            .focusRequester(focusRequester = FocusRequester()),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(20.dp) // 设置圆的大小
                .clip(RoundedCornerShape(percent = 50))
                .background(color = unfinishedColor)

        )
        OutlinedTextField(
            value = stringResource(R.string.click_to_add),
            onValueChange= {/*onValueChange*/},
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            modifier = modifier
                .fillMaxWidth(),
        )

    }
}