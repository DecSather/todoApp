package com.sather.todo.ui.diary
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sather.todo.data.Diary
import com.sather.todo.ui.AppViewModelProvider
import com.sather.todo.ui.backlog.components.BaseScreenBody
import com.sather.todo.ui.backlog.formatter
import com.sather.todo.ui.components.RowIndicator
import com.sather.todo.ui.components.basePadding
import com.sather.todo.ui.components.iconMediumSize
import com.sather.todo.ui.diary.components.*
import com.sather.todo.ui.navigation.BaseDestination
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.util.*

data object DiaryHome : BaseDestination {
    override val icon = Icons.Filled.EditNote
    override val route = "diaries"
}
@Composable
fun DiaryHomeScreen(
    onDiaryDetailClick:(Long)->Unit={},
    viewModel:DiaryHomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
    
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedYear by remember { mutableStateOf(LocalDate.now().year.toString()) }
    var selectedMouth by remember { mutableStateOf(String.format("%02d",LocalDate.now().monthValue)) }
    
    var selectedMouthNum by remember(selectedYear,selectedMouth) { mutableIntStateOf(getDaysInMonth(selectedYear,selectedMouth)) }
    LaunchedEffect(selectedYear,selectedMouth) {
        viewModel.loadMonthDiaries("$selectedYear-$selectedMouth")
    }
    val dateStrings = remember(selectedYear,selectedMouth) { generateMonthDateStrings(selectedYear.toInt(),selectedMouth.toInt()) }
    val diaries by viewModel.currentMonthDiaries.collectAsState()
//    状态值
    var tabMode by remember { mutableStateOf(DiaryTabMode.DEFAULT) }
    var rememberMode by remember { mutableStateOf(tabMode) }
    
    
    
    BaseScreenBody(
        top = {
            item {
                Row(
                    Modifier.padding(basePadding),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    when(tabMode){
//                        年份选择
                        DiaryTabMode.SELECT_YEAR ->{
                            TimeStringSelectionRow(
                                selectedText = selectedYear,
                                selectedList = (2003 .. LocalDate.now().year).toList(),
                                selectedAction = {
                                    selectedYear = it
                                    if(
                                        selectedYear == LocalDate.now().year.toString()
                                        && selectedMouth > String.format("%02d",LocalDate.now().monthValue)
                                        ){
                                        selectedMouth = String.format("%02d", LocalDate.now().monthValue)
                                    }
                                    tabMode = rememberMode
                                    
                                }
                            )
                        }
//                        月份选择
                        DiaryTabMode.SELECT_MOUTH ->{
                            TimeStringSelectionRow(
                                selectedText = selectedMouth,
                                selectedList =if(selectedYear == LocalDate.now().year.toString())(1 .. LocalDate.now().monthValue).toList()
                                else (1 .. 12).toList(),
                                selectedAction = {
                                    selectedMouth = it
                                    tabMode = rememberMode
                                }
                            )
                        }
                        else ->{
//                            编辑Icon
                            if(tabMode == DiaryTabMode.EDIT) {
                                Icon(
                                    imageVector = Icons.Filled.ModeEdit,
                                    contentDescription = "Add toady's diary",
                                    modifier = Modifier
                                        .size(iconMediumSize)
                                        .clickable {
                                            rememberMode = DiaryTabMode.DEFAULT
                                            tabMode = rememberMode
                                        },
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                
                                )
                            }
                            else {
//                                展示Icon
                                Icon(
                                    imageVector = Icons.Filled.HorizontalSplit,
                                    contentDescription = "Add toady's diary",
                                    modifier = Modifier
                                        .size(iconMediumSize)
                                        .clickable {
                                            rememberMode = DiaryTabMode.EDIT
                                            tabMode = rememberMode
                                        },
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
//                            字符串
                            Text(
                                text = selectedYear,
                                modifier = Modifier
                                    .padding(horizontal = basePadding)
                                    .clickable {
                                        tabMode = DiaryTabMode.SELECT_YEAR
                                    },
                                style =MaterialTheme.typography.headlineLarge,
                            )
                            
                            RowIndicator(
                                color = if(rememberMode == DiaryTabMode.DEFAULT)MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = selectedMouth,
                                modifier = Modifier
                                    .padding(horizontal = basePadding)
                                    .clickable {
                                        tabMode = DiaryTabMode.SELECT_MOUTH
                                    },
                                style =MaterialTheme.typography.headlineLarge,
                            )
                            
                        }
                    }
                }
            }
              },
        underside = {
            itemsIndexed(
                items = dateStrings.subList(0,selectedMouthNum),
                key = { index,_ -> index}
            ) { index,date ->
                val hasDiary = diaries.containsKey(date)
                
                if(rememberMode == DiaryTabMode.DEFAULT) {
                    if(hasDiary){
                        DiaryDisplaysRow(
                            timeTitle = diaries[date]!!.timeTitle,
                            content = diaries[date]!!.content,
                            modifier = Modifier.clickable {
                                onDiaryDetailClick(diaries[date]!!.id)
                            }
                        )
                    }else{
                        BlueDotPlaceholder(
                            modifier = Modifier.clickable {
                                val newDiary = Diary(
                                    timeTitle = date
                                )
                                coroutineScope.launch {
                                    viewModel.insertDiary(newDiary)
                                    viewModel.loadMonthDiaries("${selectedYear}-${selectedMouth}")
                                }
                                onDiaryDetailClick(newDiary.id)
                            }
                        )
                    }
                    
                }else{
                    if(hasDiary) {
                        DiaryEditRow(
                            modifier = Modifier.clickable {
                                onDiaryDetailClick(diaries[date]!!.id)
                            },
                            timeTitle = diaries[date]!!.timeTitle,
                            content = diaries[date]!!.content
                        )
                    }
                }
            }
        },
        floatButtonAction = {
            if(rememberMode == DiaryTabMode.DEFAULT) {
                val currentTime = LocalDate.now().format(formatter)
                if(diaries.containsKey(currentTime)) {
                    
                    onDiaryDetailClick(diaries[currentTime]!!.id)
                }else{
                    val newDiary = Diary(
                        timeTitle = currentTime
                    )
                    coroutineScope.launch {
                        viewModel.insertDiary(newDiary)
                    }
                    onDiaryDetailClick(newDiary.id)
                }
            }/*else{
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Save Edited diary"
                )
            }*/
        },
        floatButtoncontent = {
            if(rememberMode == DiaryTabMode.DEFAULT) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add toady's diary"
                )
            }else{
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Save Edited diary"
                )
            }
        }
    )
}
fun getDaysInMonth(year: String, month: String): Int {
    val yearInt = year.toInt()
    val monthInt = month.toInt()
    if(yearInt == LocalDate.now().year && monthInt == LocalDate.now().monthValue)
        return LocalDate.now().dayOfMonth
    return YearMonth.of(yearInt, monthInt).lengthOfMonth()
}

// 生成当月所有日期字符串的列表
fun generateMonthDateStrings(year: Int,month:Int): List<String> {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    
    return buildList {
        while (calendar.get(Calendar.MONTH) == month - 1) {
            add(
                "%04d-%02d-%02d".format(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            )
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }
}
