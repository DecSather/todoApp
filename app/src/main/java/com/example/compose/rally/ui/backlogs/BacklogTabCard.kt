package com.example.compose.rally.ui.backlogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.rally.R
import com.example.compose.rally.data.*
import com.example.compose.rally.ui.components.RoutineRow
import com.example.compose.rally.ui.theme.RallyTheme

@Preview(showBackground = true)
@Composable
fun BacklogCardPreview(){
    RallyTheme {
        BacklogCard(
            backlog = BacklogData.getBacklog(0),
            onClickSeeAll = {},
            onRoutineClick = {}
        )
    }
}

@Composable
fun BacklogCard(
    backlog:Backlog,
    onClickSeeAll: () -> Unit,
    onRoutineClick: (String) -> Unit
) {
    val routines:List<Routine> =BacklogData.routines.filter {it.id in fromJsonToList(backlog.routineListJson)}
    val creditTotal=routines.map{routine -> routine.credit}.sum()
    CommonCard(
        timeTitle = backlog.timeTitle,
        creditTotal = creditTotal,
        onClickSeeAll = onClickSeeAll,
        data = routines,
        colors = {it.color},
        values = { it.credit }
    ) { routine ->
        RoutineRow(
            modifier = Modifier.clickable { onRoutineClick(routine.content) },
            content=routine.content,
            subcontent=routine.subcontent,
            credit=routine.credit,
            finished=routine.finished,
            color = routine.color
        )
    }
}

@Composable
private fun <T> CommonCard(
    timeTitle: String,
    creditTotal: Float,
    onClickSeeAll: () -> Unit,
    values: (T) -> Float,
    colors: (T) -> Color,
    data: List<T>,
    row: @Composable (T) -> Unit
) {
    Card {
        Column {
            Column(Modifier.padding(RallyDefaultPadding)) {
                Text(text = timeTitle, style = MaterialTheme.typography.h2)
                val amountText = "$" + creditTotal
                Text(text = amountText, style = MaterialTheme.typography.subtitle2)
            }
            BaseDivider(data, values, colors)
            Column(Modifier.padding(start = 16.dp, top = 4.dp, end = 8.dp)) {
                data.take(SHOWN_ITEMS).forEach { row(it) }
                SeeAllButton(
                    modifier = Modifier.clearAndSetSemantics {
                        contentDescription = "All $timeTitle"
                    },
                    onClick = onClickSeeAll,
                )
            }
        }
    }
}

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
private fun SeeAllButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = modifier
            .height(44.dp)
            .fillMaxWidth()
    ) {
        Text(stringResource(R.string.see_all))
    }
}

private val RallyDefaultPadding = 12.dp

private const val SHOWN_ITEMS = 3
