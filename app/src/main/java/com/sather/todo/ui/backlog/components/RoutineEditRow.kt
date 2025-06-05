package com.sather.todo.ui.backlog.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import com.sather.todo.data.Routine
import com.sather.todo.ui.components.EditRowSpacer

@Composable
fun RoutineEditRow(
    modifier: Modifier,
    routine: Routine,
    locationInEnd:Int,
    updateRoutine: (Routine) -> Unit ={ },
    addRoutine: (Int,String,Int) -> Unit,
    deleteRoutine:(String)->Unit,
    onDeleteRoutine:() -> Unit,
    focusClick:(Int) -> Unit,
) {
    
    var animFlag by remember { mutableStateOf(false) }
    
    val animVisibleState = remember {  MutableTransitionState(true).apply {  targetState = true  }  }
    
    var contentFieldValueState by remember(routine.content) {
        mutableStateOf(
            TextFieldValue(
                text = routine.content,
                selection = TextRange(locationInEnd),
            )
        )
    }
    LaunchedEffect(animVisibleState.currentState) {
        
        if (!animVisibleState.targetState &&
            !animVisibleState.currentState
        ) {
            onDeleteRoutine()
        }
    }
    var contentwasFocused by remember { mutableStateOf(false) }
    var selectionwasStarted by remember { mutableStateOf(false) }
    
//        文本输入框
    AnimatedVisibility(
        visibleState = animVisibleState,
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .padding(start = EditRowSpacer),
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
                    .padding(start = EditRowSpacer)
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.key == Key.Delete || keyEvent.key == Key.Backspace) {
                            if (contentFieldValueState.selection.start == 0 && !selectionwasStarted) {
                                selectionwasStarted = true
                                false
                            } else if (contentFieldValueState.selection.start == 0 && selectionwasStarted) {
                                focusClick(routine.sortId - 1)
                                deleteRoutine(contentFieldValueState.text)
                                animVisibleState.targetState = false
                                true
                            }
                        }
//                        不阻止其他键位的默认返回
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
                                lineText.length
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
                        addRoutine(1, newText,0)
                    }
                ),
                textStyle = MaterialTheme.typography.bodyMedium,
            )
            
        }
    }
}