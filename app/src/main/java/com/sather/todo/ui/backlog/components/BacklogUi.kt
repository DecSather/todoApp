package com.sather.todo.ui.backlog.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sather.todo.R
import com.sather.todo.ui.components.basePadding
import com.sather.todo.ui.theme.faverColor
import com.sather.todo.ui.theme.importColor
import com.sather.todo.ui.theme.normalColor
import com.sather.todo.ui.theme.unfinishedColor

/*
* Backlog Detail Card
    -主页用
 */

@Composable
fun SeeAllButton(modifier: Modifier = Modifier) {
    Box(modifier=modifier
        .padding(basePadding) // 设置内边距，和 TextButton 一致
        .fillMaxWidth()
        .wrapContentSize(Alignment.Center)
    ){
        Text(
            text= stringResource(R.string.see_all),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/*
* Single Backlog
*/
//警示对话框-删除
@Composable
fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
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

val RoutineColors= listOf(unfinishedColor,importColor, normalColor, faverColor)

