import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import java.util.*
import kotlin.math.roundToInt
@Composable
fun Screen2(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    
    val list = remember { mutableStateListOf(
        "Kotlin", "Java", "C++", "Python", "C", "Assembly"
    ) }
    var draggedItemIndex by remember { mutableIntStateOf(-1) }
    val heightDp = remember { 80.dp }
    val heightPx = remember { with(density) { heightDp.toPx() } }
    
    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(list, key = { _, k -> k }) { index, text ->
            
            var dragOffsetY by remember { mutableFloatStateOf(0F) }
            var isDragged by remember { mutableStateOf(false) }
            
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .graphicsLayer {
                        translationY = dragOffsetY
                        shadowElevation = if (isDragged) 8F else 0F
                        alpha = if (isDragged) .9F else 1F
                    }
                    .zIndex(if (isDragged) 2F else 0F)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text)
                    
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        draggedItemIndex = index
                                        isDragged = true
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragOffsetY += dragAmount.y
                                        
                                        val indexOffset = (dragOffsetY / heightPx).toInt()
                                        val newIndex = indexOffset + draggedItemIndex
                                        
                                        if (newIndex in list.indices && newIndex != draggedItemIndex) {
                                            Collections.swap(list, draggedItemIndex, newIndex)
                                            draggedItemIndex = newIndex
                                            dragOffsetY -= indexOffset * heightPx
                                        }
                                    },
                                    onDragEnd = {
                                        dragOffsetY = 0F
                                        draggedItemIndex = -1
                                        isDragged = false
                                    },
                                    onDragCancel = {
                                        dragOffsetY = 0F
                                        draggedItemIndex = -1
                                        isDragged = false
                                    }
                                )
                            }
                    
                    ) {
                        Icon(Icons.Rounded.Menu, "Drag Handle")
                    }
                }
            }
            
        }
    }
}
