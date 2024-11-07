package com.example.compose.rally.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.example.compose.rally.ui.theme.RallyTheme
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.rally.R
import com.example.compose.rally.data.Backlog
import com.example.compose.rally.data.Routine
import com.example.compose.rally.ui.routine.formatedCredit
import com.example.compose.rally.ui.theme.BackgroudBlue
import com.example.compose.rally.ui.theme.Blue900
import com.example.compose.rally.ui.theme.importColor
import java.time.format.DateTimeFormatter
@Composable
fun CheckboxMinimalExample() {
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.background(MaterialTheme.colors.surface)
        
    ) {
    
        
    }
    
}
@Composable
private fun Routinerow(
    modifier: Modifier = Modifier,
    content: String,
    subcontent:String,
    credit: Float,
    finished:Boolean,
    color: Color
) {
    var checked by remember { mutableStateOf(finished) }
    val dollarSign ="$ "
    val customColors = CheckboxDefaults.colors(
        checkedColor =MaterialTheme.colors.primary, // 选中时的颜色
    )
    Row(
        modifier = modifier
            .height(68.dp)
            .clearAndSetSemantics {
                contentDescription =
                    "$content account ending in ${subcontent.takeLast(4)}, current balance $dollarSign$credit"
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val typography = MaterialTheme.typography
        Rowindicator(
            color = color,
            modifier = Modifier
        )
        Spacer(Modifier.width(12.dp))
        Checkbox(
            colors = customColors,
            checked = checked,
            onCheckedChange = { checked = it }
        )
        Column(Modifier) {
            
            Text(text = content, style = typography.body1)
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(text = subcontent, style = typography.subtitle1)
            }
        }
        Spacer(Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            
            Text(
                text = dollarSign,
                style = typography.h6,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                text = credit.toString(),
                style = typography.h6,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        Spacer(Modifier.width(16.dp))
        
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(24.dp)
            )
        }
    }
    RallyDivider()
}

//列竖线
@Composable
private fun Rowindicator(color: Color, modifier: Modifier = Modifier) {
    Spacer(
        modifier
            .size(4.dp, 36.dp)
            .background(color = color)
    )
}
//进度线
@Composable
fun Basedivider(
    total:Float,
    data: List<Float>,
    colors: List<Color>
) {
    var index=0
    if(total>0.0) {
        Row(Modifier.fillMaxWidth()) {
            data.forEach { item ->
                if(item>0.0){
                    Spacer(
                        modifier = Modifier
                            .weight(item/total)
                            .height(1.dp)
                            .background(colors.get(index++))
                    )
                    
                }
            }
        }
    }else{
        Row(Modifier.fillMaxWidth()) {
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(BackgroudBlue)
            )
        }
    }
}


@Preview
@Composable
fun UiPreview(){
    RallyTheme {
        Routinerow(
            content="123",
            subcontent="123",
            credit=1f,
            finished=true,
            color= importColor
        )
    }
}

