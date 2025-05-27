package com.sather.todo.ui.routine

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.sather.todo.R
import com.sather.todo.data.Routine
import com.sather.todo.ui.AppViewModelProvider
import com.sather.todo.ui.backlog.components.RoutineColors
import com.sather.todo.ui.components.cardSize
import com.sather.todo.ui.components.startPadding
import com.sather.todo.ui.navigation.BaseDestination
import kotlinx.coroutines.launch

//new Routine Entry-预添加类设计，非数据
object SingleRoutineDestination : BaseDestination {
    override val route = "single_routine"
    override val icon = Icons.Filled.Check
    const val routineIdArg = "routineId"
    val routeWithArgs = "$route/{$routineIdArg}"
    val arguments = listOf(
        navArgument(routineIdArg) { type = NavType.LongType }
    )
}

@Composable
fun SingleRoutineScreen(
    navigateBack: () -> Unit,
    routineViewModel: SingleRoutineViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()
    
    SingleRoutineBody(
        routineUiState = routineViewModel.routineUiState,
        navigateBack=navigateBack,
        onRoutineValueChange= routineViewModel::updateRoutineUiState,
        onSave = {
            coroutineScope.launch {
                routineViewModel.updateRoutine()
            }
        }
    )
}

@Composable
fun SingleRoutineBody(
    routineUiState: RoutineUiState,
    navigateBack:()->Unit,
    onRoutineValueChange:(Routine) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val routine = routineUiState.routine
    
    val rankIndexList = listOf(1,2,3)
    Column(
        modifier = modifier.padding(startPadding),
        verticalArrangement = Arrangement.spacedBy(startPadding)
    ) {
        
        IconButton(onClick = navigateBack) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = stringResource(R.string.back_button)
            )
        }
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(startPadding)
        ) {
//        content
            OutlinedTextField(
                value = routineUiState.routine.content,
                onValueChange = {
                    onRoutineValueChange(routineUiState.routine.copy(content = it ))
                },
                label = {
                    Text(
                        stringResource(R.string.rontine_content_req),
                        style = MaterialTheme.typography.bodyMedium
                    
                    )
                        },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
//        rank
           
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.padding(horizontal = startPadding),
                    text = stringResource(R.string.rontine_rank_req),
                    style = MaterialTheme.typography.bodyMedium
                
                )
                Row(
                    modifier = Modifier.width(cardSize),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    rankIndexList.forEach { rank->
                        rankSwitch(
                            rank = rank,
                            checked = (routine.rank == rank),
                            onCheckedChange = {
                                onRoutineValueChange(routineUiState.routine.copy(rank = rank))
                                onSave()
                            }
                        )
                    }
                }
                
            }
            
//        subcontent
            OutlinedTextField(
                value = routineUiState.routine.subcontent,
                onValueChange = {
                    onRoutineValueChange(routineUiState.routine.copy(subcontent = it ))
                },
                label = {
                    Text(
                        stringResource(R.string.rontine_subcontent_req),
                        style = MaterialTheme.typography.bodyMedium
                    )
                        },
                modifier = Modifier.fillMaxWidth(),
            )
            if (!routineUiState.isEntryValid) {
                Text(
                    modifier = Modifier.padding(start = startPadding),
                    text = stringResource(R.string.required_fields),
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }
        Button(
            onClick = {
                onSave()
                navigateBack()
            },
            enabled = routineUiState.isEntryValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
}
@Composable
fun rankSwitch(
    rank:Int,
    checked:Boolean,
    onCheckedChange:(Boolean) -> Unit,
){
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        thumbContent = {
            if (checked) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                )
                
            } else {
                Text(text = rank.toString())
            }
        },
        colors = SwitchDefaults.colors(
//                勾选颜色
            checkedTrackColor = RoutineColors[rank],
            uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
            uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
        )
    )
}
