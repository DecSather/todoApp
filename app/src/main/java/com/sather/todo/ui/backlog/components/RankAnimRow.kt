package com.sather.todo.ui.backlog.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import com.sather.todo.ui.components.CheckBoxSize
import com.sather.todo.ui.components.EditRowHeight
import com.sather.todo.ui.components.EditRowSpacer
import com.sather.todo.ui.components.roundCornerShape
import com.sather.todo.ui.theme.RoutineColors

@Composable
fun RankAnimRow(
    text: String,
    rank:Int,
    selected: Boolean,
    onSelected: () -> Unit,
    onClicked: (Int) -> Unit,
) {
    
    Row(
        modifier = Modifier
            .animateContentSize()
            .height(EditRowHeight)
            .clearAndSetSemantics { contentDescription = text },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (selected) {
            RoutineColors.forEachIndexed{
                    index,color ->
                if(index>0)
                    RankColorBox(
                        {
                            onClicked(index)
                            onSelected()
                        },
                        color,
                    )
            }
        }else{
            RankColorBox(
                onSelected,
                RoutineColors[rank]
            )
        }
    }
}

@Composable
private fun RankColorBox(
    onClicked: () -> Unit,
    color: Color,
){
    Row(
        modifier = Modifier
            .height(CheckBoxSize + EditRowSpacer *2 )
            .clip(RoundedCornerShape(percent = 50))
            .clickable{ onClicked() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(EditRowSpacer))
        Box(
            modifier = Modifier
                .size(CheckBoxSize) // 设置圆的大小
                .clip(RoundedCornerShape(roundCornerShape))
//                设置颜色改展开列动画或弹出卡片
                .background(color = color)
            ,
        )
        Spacer(modifier = Modifier.width(EditRowSpacer))
    }
}