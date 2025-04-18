package com.sather.todo.ui.backlog.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.sather.todo.R
import com.sather.todo.ui.components.basePadding

@Composable
fun SeeAllButton(modifier: Modifier = Modifier) {
    Box(
        modifier=modifier
            .padding(basePadding)
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
    ){
        Text(
            fontSize = 16.sp,
            text= stringResource(R.string.see_all),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}