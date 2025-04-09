package com.sather.todo.ui.routine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sather.todo.R
import com.sather.todo.data.Routine
import com.sather.todo.ui.AppViewModelProvider
import com.sather.todo.ui.backlog.components.RoutineColors
import com.sather.todo.ui.components.TextLimitedWidth
import com.sather.todo.ui.components.basePadding
import kotlinx.coroutines.launch

//new Routine Entry-预添加类设计，非数据


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
    println("rank :${routine.rank}")
    var creditText by remember { mutableStateOf(routine.credit.toString()) }
    
    val rankIndexList = listOf(1,2,3)
    Column(
        modifier = modifier.padding(basePadding),
        verticalArrangement = Arrangement.spacedBy(basePadding)
    ) {
        
        IconButton(onClick = navigateBack) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = stringResource(R.string.back_button)
            )
        }
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(basePadding)
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
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                        },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
//        rank
           
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.rontine_rank_req),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = basePadding),
                    color = MaterialTheme.colorScheme.secondary,
                )
                Row(
                    modifier = Modifier.width(TextLimitedWidth),
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
//        credit
            OutlinedTextField(
                value = creditText,
                onValueChange = {
                    newText ->
                    creditText = newText
                    if(newText.toFloatOrNull()==null) {
                        onRoutineValueChange(routineUiState.routine.copy(credit = 0f))
                    }
                    else{
                        onRoutineValueChange(routineUiState.routine.copy(credit = creditText.toFloat()))
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = {
                    Text(
                        stringResource(R.string.rontine_credit_req),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                        },
                leadingIcon = { Text(stringResource(R.string.dollarSign)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
//        subcontent
            OutlinedTextField(
                value = routineUiState.routine.subcontent,
                onValueChange = {
                    onRoutineValueChange(routineUiState.routine.copy(subcontent = it ))
                },
                label = {
                    Text(
                        stringResource(R.string.rontine_subcontent_req),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                        },
                modifier = Modifier.fillMaxWidth(),
            )
            if (!routineUiState.isEntryValid) {
                Text(
                    text = stringResource(R.string.required_fields),
                    modifier = Modifier.padding(start = basePadding),
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
