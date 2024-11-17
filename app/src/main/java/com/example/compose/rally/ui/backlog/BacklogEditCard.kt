package com.example.compose.rally.ui.backlog

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.compose.rally.R
import com.example.compose.rally.data.Backlog
import com.example.compose.rally.data.Routine
import com.example.compose.rally.ui.routine.RoutineUiState
import com.example.compose.rally.ui.theme.unfinishedColor
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


@Composable
fun BacklogEditCard(
    routineList:List<Routine>,
    backlogUiState:BacklogUiState,
    routineUiState : RoutineUiState,
    updateBacklogUiState:(Backlog) -> Unit,
    insertRoutineClick:() ->Unit,
    updateRoutineUiState:(Routine) -> Unit,
    
    ) {
    val coroutineScope = rememberCoroutineScope()
    
    val focusRequester = remember { FocusRequester() }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Gray.copy(alpha = 0.5f))
    )
    {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomCenter)
        ) {
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
                    println("weather backlogId: "+backlogUiState.backlog.id)
//                        时间选择
                    showDatePickerDialog(
                        backlogUiState.backlog.id >= 0,
                        timeTitle = backlogUiState.backlog.timeTitle
                    )
                }
                /*
                * 考虑使用backlog点击编辑删除列编辑*/
                routineList.map { it ->
                    BacklogEditRow(
                    modifier = if (it.id == routineUiState.routine.id) Modifier.focusRequester(focusRequester)
                    else Modifier,
                        content = it.content,
                        color = unfinishedColor,
                        onValueChange = {},
                    )
                }
                
                TextButton(
                    onClick = {},
                    enabled = true,
                    modifier = Modifier
                        .wrapContentWidth()
                        .align(Alignment.End)
                        .padding(bottom = 6.dp,end=10.dp)
                ) {
                    Text(
                        color = MaterialTheme.colors.primary,
                        text = stringResource(R.string.save_action)
                    )
                }
            }
            
        }
    }
}


@Composable
fun showDatePickerDialog(
    onForceShowDate:Boolean=false,
    timeTitle:String="1970-01-01",
) {
    val initialDate = LocalDate.parse(timeTitle, formatter)
    var selectedDate by remember { mutableStateOf(initialDate) }
    
    var showModal by remember { mutableStateOf(onForceShowDate) }
    OutlinedTextField(
        value = selectedDate.format(DateTimeFormatter.ISO_DATE),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            cursorColor = Color.Transparent,
                    focusedBorderColor =MaterialTheme.colors.primary,
            unfocusedBorderColor =  Color.Transparent,
        ),
        onValueChange = {},
        label = { Text(stringResource(R.string.formatter)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .pointerInput(selectedDate) {
                awaitEachGesture {
                    // Modifier.clickable 不适用于文本字段，因此我们使用 Modifier.pointerInput
                    // 在 Initial pass 中，在文本字段使用事件之前观察事件
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showModal = true
                    }
                }
            }
    )
    if (showModal) {
        DatePickerModal(
            selectedDate =selectedDate ,
            onDateSelected = { selectedDate = it },
            onDismiss = { showModal = false }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    selectedDate :LocalDate,
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(selectedDate?.let { convertLocalDateToMillis(it) })
    
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(LocalDate.parse(datePickerState.selectedDateMillis?.let { convertMillisToDate(it) }, formatter) )
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        println("cur time： " + datePickerState.selectedDateMillis?.let { convertMillisToDate(it) })
        DatePicker(
            colors=DatePickerDefaults.colors(
                containerColor = MaterialTheme.colors.surface,
                weekdayContentColor =MaterialTheme.colors.primary,
                todayDateBorderColor =MaterialTheme.colors.primary,
                selectedYearContainerColor =MaterialTheme.colors.primary,
                selectedDayContainerColor = MaterialTheme.colors.primary,
                navigationContentColor =MaterialTheme.colors.primary
            ),
            state = datePickerState
        )
    }
}

@Composable
fun BacklogEditRow(
    modifier: Modifier,
    content:String,
    color: Color,
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
                .background(color = color)
                
        )
        OutlinedTextField(
            value = content,
            onValueChange= {/*onValueChange*/},
            textStyle = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.em
            ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            modifier = modifier
                .fillMaxWidth(),
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
            textStyle = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.em
            ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            modifier = modifier
                .fillMaxWidth(),
        )
        
    }
}
fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat(DateFormatter)
    return formatter.format(Date(millis))
}
fun convertLocalDateToMillis(localDate: LocalDate): Long {
    val zonedDateTime = localDate.atStartOfDay(ZoneOffset.UTC)
    return zonedDateTime.toInstant().toEpochMilli()
}
val DateFormatter ="yyyy-MM-dd"


