package com.sather.todo.ui.backlog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.sather.todo.ui.routine.RoutineUiState
import com.sather.todo.ui.theme.ToDoTheme

@Composable
fun EditCardTextField(
    modifier: Modifier,
) {
    var text by remember { mutableStateOf("<p>hello\nworld</p>") }
    TextField(
        modifier = modifier,
        value = text,
        onValueChange = {
            text = it
                        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = AlertDialogDefaults.containerColor,
            focusedIndicatorColor = AlertDialogDefaults.containerColor,
            unfocusedContainerColor = AlertDialogDefaults.containerColor,
            unfocusedIndicatorColor = AlertDialogDefaults.containerColor,
        ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            lineHeight = 40.sp,
        ),
        maxLines = 5,
    )
}

@Preview
@Composable
fun EditCardTextFieldPreview(){
    ToDoTheme {
        AlertDialog(
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
            ),
            modifier = Modifier
                .padding(16.dp)
                .imePadding(),
            text = {
                EditCardTextField(Modifier)
            },
            onDismissRequest = {},
            confirmButton = {},
            dismissButton = {}
        )
        
    }
}