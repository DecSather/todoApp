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
import com.sather.todo.ui.components.basePadding

@Composable
fun  BaseScreenBody(
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    lazyColumnModifier: Modifier = Modifier,
    top:@Composable BoxScope.() ->Unit,
    rows: LazyListScope.() -> Unit,
    floatButtonAction:() ->Unit,
    floatButtoncontent:@Composable () ->Unit,
) {
    
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = lazyColumnModifier,
                verticalArrangement = verticalArrangement
                
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