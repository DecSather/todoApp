package com.example.compose.rally.ui.components

import android.icu.text.ListFormatter.Width
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.rally.R

private val RallyDefaultPadding = 12.dp

private const val SHOWN_ITEMS = 3
@Composable
fun <T> CommonCard(
    modifier: Modifier=Modifier,
    timeTitle: String,
    creditTotal: Float,
    values: (T) -> Float,
    colors: (T) -> Color,
    data: List<T>,
    row: @Composable (T) -> Unit
) {
    Card {
        Column {
            Column(modifier
                .fillMaxWidth()
                .padding(RallyDefaultPadding)
            ) {
                Text(text = timeTitle, style = MaterialTheme.typography.h2)
                val amountText = "$" + creditTotal
                Text(text = amountText, style = MaterialTheme.typography.subtitle2)
            }
            BaseDivider(data, values, colors)
            Column(Modifier
                .padding(start = 16.dp, top = 4.dp, end = 8.dp)
            ) {
                data.take(SHOWN_ITEMS).forEach { row(it) }
                SeeAllButton(
                    modifier = modifier.clearAndSetSemantics {
                        contentDescription = "All $timeTitle"
                    }
                )
            }
        }
    }
}


@Composable
fun RoutineRow(
    modifier: Modifier = Modifier,
    content: String,
    subcontent:String,
    credit: Float,
    finished:Boolean,
    color: Color
) {
    BaseRow(
        modifier = modifier,
        color = color,
        title = content,
        subtitle = subcontent,
        amount = credit,
        negative = finished
    )
}

@Composable
private fun BaseRow(
    modifier: Modifier = Modifier,
    color: Color,
    title: String,
    subtitle: String,
    amount: Float,
    negative: Boolean
) {
    val dollarSign = if (negative) "–$ " else "$ "
    val formattedAmount = formatAmount(amount)
    Row(
        modifier = modifier
            .height(68.dp)
            .clearAndSetSemantics {
                contentDescription =
                    "$title account ending in ${subtitle.takeLast(4)}, current balance $dollarSign$formattedAmount"
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val typography = MaterialTheme.typography
        RowIndicator(
            color = color,
            modifier = Modifier
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier) {
            Text(text = title, style = typography.body1)
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(text = subtitle, style = typography.subtitle1)
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
                text = formattedAmount,
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
//列区分竖线
@Composable
private fun RowIndicator(color: Color, modifier: Modifier = Modifier) {
    Spacer(
        modifier
            .size(4.dp, 36.dp)
            .background(color = color)
    )
}
//列
@Composable
private fun <T> BaseDivider(
    data: List<T>,
    values: (T) -> Float,
    colors: (T) -> Color
) {
    Row(Modifier.fillMaxWidth()) {
        data.forEach { item: T ->
            Spacer(
                modifier = Modifier
                    .weight(values(item))
                    .height(1.dp)
                    .background(colors(item))
            )
        }
    }
}


@Composable
private fun SeeAllButton(modifier: Modifier = Modifier) {
    Box(modifier=modifier
        .padding(16.dp) // 设置内边距，和 TextButton 一致
        .fillMaxWidth()
        .wrapContentSize(Alignment.Center)
    ){
        Text(
            fontSize = 16.sp,
            text= stringResource(R.string.see_all),
            color = MaterialTheme.colors.primary
        )
    }
}