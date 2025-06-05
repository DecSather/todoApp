package com.sather.todo.ui.backlog.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sather.todo.R

//警示对话框-删除
@Composable
fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit, onDeleteCancel: () -> Unit, modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = { /* Do nothing */ },
        title = { Text(stringResource(R.string.attention)) },
        text = { Text(stringResource(R.string.delete_question)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(
                    text = stringResource(R.string.no),
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(
                    text = stringResource(R.string.yes),
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
        })
}
