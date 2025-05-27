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
    
    var contentwasFocused by remember { mutableStateOf(false) }
    var contentwasEmpty by remember { mutableStateOf(false) }
    
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
            
        }
    }
}