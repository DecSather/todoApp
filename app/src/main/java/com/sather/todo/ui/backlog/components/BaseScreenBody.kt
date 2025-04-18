package com.sather.todo.ui.backlog.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sather.todo.ui.components.basePadding

@Composable
fun  BaseScreenBody(
    state:LazyListState = rememberLazyListState(),
    lazyColumnModifier: Modifier = Modifier,
    top:@Composable BoxScope.() ->Unit,
    rows: LazyListScope.() -> Unit,
    floatButtonAction:() ->Unit,
    floatButtoncontent:@Composable () ->Unit,
) {
    
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = state,
                modifier = lazyColumnModifier,
            ) {
                item(key = "tricolor_circle") {
                    Box(Modifier.padding(basePadding)) {
                        top()
                    }
                }
                rows()
            }
            FloatingActionButton(
                shape = CircleShape,
                onClick = floatButtonAction,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(horizontal = 16.dp, vertical = 32.dp),
                content =floatButtoncontent
            )
        }
}