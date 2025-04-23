package com.sather.todo.ui.backlog.components

import androidx.compose.foundation.layout.Box
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
import com.sather.todo.ui.components.basePadding

@Composable
fun  BaseScreenBody(
    state:LazyListState = rememberLazyListState(),
    lazyColumnModifier: Modifier = Modifier,
    top: LazyListScope.() ->Unit,
    underside: LazyListScope.() -> Unit,
    floatButtonAction:() ->Unit,
    floatButtoncontent:@Composable () ->Unit,
) {
//    box-悬浮按钮布局用
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = state,
            modifier = lazyColumnModifier,
        ) {
            top()
            underside()
        }
        FloatingActionButton(
            shape = CircleShape,
            onClick = floatButtonAction,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = basePadding, vertical = basePadding * 2),
            content =floatButtoncontent
        )
    }
}