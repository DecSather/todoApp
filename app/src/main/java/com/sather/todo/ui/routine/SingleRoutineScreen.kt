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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.sather.todo.R
import com.sather.todo.data.Routine
import com.sather.todo.ui.AppViewModelProvider
import com.sather.todo.ui.components.RoutineColors
import com.sather.todo.ui.navigation.BaseDestination
import kotlinx.coroutines.launch
//new Routine Entry-预添加类设计，非数据
object SingleRoutineDestination : BaseDestination {
    override val route = "single_routine"
    override val icon = Icons.Filled.Check
    const val routineIdArg = "routineId"
    val routeWithArgs = "$route/{$routineIdArg}"
    val arguments = listOf(
        navArgument(routineIdArg) { type = NavType.StringType }
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
        onSaveClick = {
            coroutineScope.launch {
                routineViewModel.updateRoutine()
                navigateBack()
            }
        },
    )
}

@Composable
fun SingleRoutineBody(
    routineUiState: RoutineUiState,
    navigateBack:()->Unit,
    onRoutineValueChange:(Routine) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val routine = routineUiState.routine
    var routineRank by remember { mutableIntStateOf(routine.rank) }
    var creditText by remember { mutableStateOf(routine.credit.toString()) }
    
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
                        fontSize = 15.sp,
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
                    fontSize = 15.sp,
                    modifier = Modifier.padding(horizontal = startPadding),
                    color = MaterialTheme.colorScheme.secondary,
                )
                Row(
                    modifier = Modifier.width(248.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    rankIndexList.forEach { index->
                        rankSwitch(
                            rank = index,
                            checked = (routineRank == index),
                            onCheckedChange = {
                                routineRank = index
                                onRoutineValueChange(routineUiState.routine.copy(rank = index))
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
                    if(newText.isEmpty()) {
                        onRoutineValueChange(routineUiState.routine.copy(credit = 0f))
                    }
                    else if(newText.toFloatOrNull()!=null) {
                        onRoutineValueChange(routineUiState.routine.copy(credit = creditText.toFloat()))
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = {
                    Text(
                        stringResource(R.string.rontine_credit_req),
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.secondary,
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
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                        },
                modifier = Modifier.fillMaxWidth(),
            )
            if (!routineUiState.isEntryValid) {
                Text(
                    text = stringResource(R.string.required_fields),
                    modifier = Modifier.padding(start = startPadding),
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }
        Button(
            onClick = onSaveClick,
            enabled = routineUiState.isEntryValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
}
private val startPadding = 16.dp
@Composable
private fun rankSwitch(
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
