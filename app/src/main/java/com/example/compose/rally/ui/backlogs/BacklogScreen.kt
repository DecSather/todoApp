package com.example.compose.rally.ui.backlogs

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.rally.R
import com.example.compose.rally.data.BacklogData
import com.example.compose.rally.data.UserData
import com.example.compose.rally.ui.components.AccountRow
import com.example.compose.rally.ui.components.CommonBody
import com.example.compose.rally.ui.components.StatementBody
import com.example.compose.rally.ui.theme.RallyTheme

//全部日志
@Composable
fun BacklogScreen(
    onRoutineClick: (String) -> Unit = {},
) {
    val importTotal = remember { BacklogData.backlogs.map { backlog -> backlog.importCredit}.sum() }
    val normalTotal = remember { BacklogData.backlogs.map { backlog -> backlog.normalCredit}.sum() }
    val faverTotal = remember { BacklogData.backlogs.map { backlog -> backlog.faverCredit}.sum() }
    
    val creditsTotal = importTotal+normalTotal+faverTotal
    CommonBody(
        modifier = Modifier.semantics { contentDescription = "Backlogs Screen" },
        items = BacklogData.backlogs,
        creditRatios = listOf(importTotal/creditsTotal,normalTotal/creditsTotal,faverTotal/creditsTotal,),
        amountsTotal = creditsTotal,
        circleLabel = stringResource(R.string.credit),
        rows = { backlog ->
            BacklogCard(
                backlog = backlog,
                onClickSeeAll = {},
                onRoutineClick = onRoutineClick
            )
            
            Spacer(Modifier.height(12.dp))
        }
    )
}
@Preview
@Composable
fun BacklogSCreen(){
    RallyTheme {
        BacklogScreen()
    }
}

