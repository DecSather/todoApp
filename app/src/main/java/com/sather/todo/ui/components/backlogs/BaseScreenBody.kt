package com.sather.todo.ui.components.backlogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun  BaseScreenBody(
    lazyColumnModifier: Modifier = Modifier,
    top:@Composable BoxScope.() ->Unit,
    rows: @Composable LazyListScope.() -> Unit,
    floatButtonAction:() ->Unit,
    floatButtoncontent:@Composable () ->Unit,
) {
    
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = lazyColumnModifier
        ) {
//        三色转圈
            item(key = 0) {
                Box(Modifier.padding(16.dp)) {
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