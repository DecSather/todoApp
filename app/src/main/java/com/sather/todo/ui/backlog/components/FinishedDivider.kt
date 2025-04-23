package com.sather.todo.ui.backlog.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sather.todo.R
import com.sather.todo.ui.components.basePadding
import com.sather.todo.ui.components.iconSmallSize

@Composable
fun FinishedDivider(
    expanded:Boolean,
    onClick:()->Unit,
){
    Row(
        Modifier.padding(start = basePadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier.size(iconSmallSize),
            onClick = onClick
        ) {
            Icon(
                tint = MaterialTheme.colorScheme.secondary,
                imageVector =
                if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (expanded) {
                    stringResource(R.string.show_less)
                } else {
                    stringResource(R.string.show_more)
                }
            )
        }
        Text(
            color = MaterialTheme.colorScheme.secondary,
            text = stringResource(R.string.finished_string),
            style = MaterialTheme.typography.titleMedium
        )
    }
    Spacer(Modifier.height(basePadding))
}