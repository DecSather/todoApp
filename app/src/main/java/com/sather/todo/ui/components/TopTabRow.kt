package com.sather.todo.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.Dp
import com.sather.todo.R
import com.sather.todo.ui.backlog.BacklogHome
import com.sather.todo.ui.diary.DiaryHome
import com.sather.todo.ui.navigation.BaseDestination
import java.util.*

//            导航栏样式
@Composable
fun TopTabRow(
    allScreens: List<BaseDestination>,
    onTabSelected: (BaseDestination) -> Unit,
    currentScreen: BaseDestination
) {
    Surface(
        Modifier
            .height(TabHeight)
            .fillMaxWidth()
    ) {
        Row(
            Modifier
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
                .selectableGroup(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            allScreens.forEach { screen ->
                ToDoAnimTab(
                    text =
                        if(screen  == BacklogHome) stringResource(R.string.backlog_route)
                        else if(screen == DiaryHome) stringResource(R.string.diary_route)
                        else stringResource(R.string.come_soon_route),
                    icon =  screen.icon,
                    onSelected = { onTabSelected(screen) },
                    selected = currentScreen == screen
                )
            }
        }
    }
}

@Composable
private fun ToDoAnimTab(
    text: String,
    icon: ImageVector,
    onSelected: () -> Unit,
    selected: Boolean
) {
    val color = MaterialTheme.colorScheme.primary
    val durationMillis = if (selected) TabFadeInAnimationDuration else TabFadeOutAnimationDuration
    val animSpec = remember {
        tween<Color>(
            durationMillis = durationMillis,
            easing = LinearEasing,
            delayMillis = TabFadeInAnimationDelay
        )
    }
    val tabTintColor by animateColorAsState(
        targetValue = if (selected) color else color.copy(alpha = InactiveTabOpacity),
        animationSpec = animSpec
    )
    Row(
        modifier = Modifier
            .padding(basePadding )
            .animateContentSize()
            .selectable(
                selected = selected,
                onClick = onSelected,
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
//                    bounded = false,
                    radius = Dp.Unspecified,
                    color = Color.Unspecified
                )
            )
            .clearAndSetSemantics { contentDescription = text },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            modifier = Modifier.size(iconMediumSize),
            contentDescription = text,
            tint = tabTintColor
        )
        if (selected) {
            Spacer(Modifier.width(startPadding))
            Text(
                text.uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.titleLarge,
                color = tabTintColor
            )
        }
    }
}


