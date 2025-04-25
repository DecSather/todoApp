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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sather.todo.data.Diary
import com.sather.todo.rallyTabRowScreens
import com.sather.todo.ui.backlog.components.BaseScreenBody
import com.sather.todo.ui.components.RowIndicator
import com.sather.todo.ui.components.TopTabRow
import com.sather.todo.ui.components.basePadding
import com.sather.todo.ui.components.iconMediumSize
import com.sather.todo.ui.diary.components.DiaryDisplaysRow
import com.sather.todo.ui.diary.components.DiaryEditRow
import com.sather.todo.ui.diary.components.DiaryTabMode
import com.sather.todo.ui.diary.components.TimeStringSelectionRow
import com.sather.todo.ui.navigation.BaseDestination
import com.sather.todo.ui.theme.ToDoTheme
import java.time.LocalDate

data object DiaryHome : BaseDestination {
    override val icon = Icons.Filled.EditCalendar
    override val route = "diaries"
}
@Composable
fun DiaryHomeScreen(
    onDiaryDetailClick:(Long)->Unit={},
    
) {
    val diaries = listOf(
        Diary(
            timeTitle = "2025-01-01",
            content = "hello 世界!"
        ),
        Diary(
            timeTitle = "2025-01-01",
            content = " "
        ),
        Diary(
            timeTitle = "2025-01-01",
            content = ""
        ),
        Diary(
            timeTitle = "2025-01-01",
            content = ""
        ),
        Diary(
            timeTitle = "2025-01-01",
            content = "123"
        ),
        Diary(
            timeTitle = "2025-02-01",
            content = "hello 世界!"
        ),
        Diary(
            timeTitle = "2025-03-01",
            content = "hello 世界!"
        ),
        Diary(
            timeTitle = "2025-04-01",
            content = "hello 世界!"
        ),
        Diary(
            timeTitle = "2025-05-01",
            content = "hello 世界!"
        ),
    )
    
//    状态值
    var tabMode by remember { mutableStateOf(DiaryTabMode.DEFAULT) }
    var rememberMode by remember { mutableStateOf(tabMode) }
    var selectedYear by remember { mutableStateOf(LocalDate.now().year.toString()) }
    var selectedMouth by remember { mutableStateOf(String.format("%02d",LocalDate.now().monthValue)) }
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
                    items = diaries,
                    key = { _,it -> it.id}
                ) {index,diary ->
                    if(rememberMode == DiaryTabMode.DEFAULT) {
                        DiaryDisplaysRow(
                            onDetailClick =  {
                                onDiaryDetailClick(diary.id)
                            },
                            timeTitle = diary.timeTitle,
                            content = diary.content,
                            onNewClick = {}
                            
                        )
                    }else{
                        DiaryEditRow(
                            timeTitle = diary.timeTitle,
                            content = diary.content
                        )
                    }
                }
        },
        floatButtonAction = {},
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

@Preview
@Composable
fun diaryScreenPreview(){
    ToDoTheme {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
//            导航栏样式
            topBar = {
                TopTabRow(
                    allScreens = rallyTabRowScreens,
                    onTabSelected = { newScreen ->
                    },
                    currentScreen= DiaryHome
                )
            }
        ) { innerPadding ->
            val i = innerPadding
            DiaryHomeScreen( )
        }
    }

}