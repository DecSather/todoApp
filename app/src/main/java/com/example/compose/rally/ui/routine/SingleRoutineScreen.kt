package com.example.compose.rally.ui.routine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.compose.rally.R
import com.example.compose.rally.data.Backlog
import com.example.compose.rally.data.Routine
import com.example.compose.rally.ui.AppViewModelProvider
import com.example.compose.rally.ui.backlog.BacklogUiState
import com.example.compose.rally.ui.navigation.RallyDestination
import kotlinx.coroutines.launch
//new Routine Entry-预添加类设计，非数据
object SingleRoutineDestination : RallyDestination {
    override val route = "single_routine"
    override val icon = Icons.Filled.Check
    const val routineIdArg = "routineId"
    val routeWithArgs = "${route}/{${routineIdArg}}"
    val arguments = listOf(
        navArgument(routineIdArg) { type = NavType.IntType }
    )
}

@Composable
fun SingleRoutineScreen(
    navigateBack: () -> Unit,
    routineViewModel: SingleRoutineViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()
    val routineUiState = routineViewModel.routineUiState
    val backlogUiState=routineViewModel.backlogUiState
    SingleRoutineBody(
        navigateBack=navigateBack,
        routineUiState = routineUiState,
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
    navigateBack:()->Unit,
    routineUiState: RoutineUiState,
    onRoutineValueChange:(Routine) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var rankText by remember { mutableStateOf("-1") }
    var creditText by remember { mutableStateOf("0.0") }
    val enabled=true
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        IconButton(onClick = navigateBack) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = stringResource(R.string.back_button)
            )
        }
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
//        content
            OutlinedTextField(
                value = routineUiState.routine.content,
                onValueChange = {
                    onRoutineValueChange(routineUiState.routine.copy(content = it ))
                    
                },
                label = { Text(stringResource(R.string.rontine_content_req)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colors.surface,
                    unfocusedContainerColor = MaterialTheme.colors.surface,
                    disabledContainerColor = MaterialTheme.colors.surface,
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                singleLine = true
            )
//        rank
            OutlinedTextField(
                value = if(rankText.equals("-1"))routineUiState.routine.rank.toString() else rankText,
                onValueChange ={
                        newText ->
                    rankText = newText
                    if(rankText.isNotEmpty()&&!rankText.equals("-1"))
                        onRoutineValueChange(routineUiState.routine.copy(rank = rankText.toInt() ))
                    onRoutineValueChange(routineUiState.routine.copy(rank = -1 ))
                        
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = { Text(stringResource(R.string.rontine_rank_req)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colors.surface,
                    unfocusedContainerColor = MaterialTheme.colors.surface,
                    disabledContainerColor = MaterialTheme.colors.surface,
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                singleLine = true
            )
//        credit
            OutlinedTextField(
                value = if(creditText.equals("0.0"))routineUiState.routine.credit.toString() else creditText,
                onValueChange = {
                        newText ->
                    creditText = newText
                    if(creditText.isNotEmpty()&&!creditText.equals("0.0"))
                        onRoutineValueChange(routineUiState.routine.copy(credit=creditText.toFloat()))
                    else onRoutineValueChange(routineUiState.routine.copy(credit=0f))
                    
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = { Text(stringResource(R.string.rontine_credit_req)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colors.surface,
                    unfocusedContainerColor = MaterialTheme.colors.surface,
                    disabledContainerColor = MaterialTheme.colors.surface,
                ),
                leadingIcon = { Text("$") },
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                singleLine = true
            )
//        subcontent
            OutlinedTextField(
                value = routineUiState.routine.subcontent,
                onValueChange = {
                    onRoutineValueChange(routineUiState.routine.copy(subcontent = it ))
                },
                label = { Text(stringResource(R.string.rontine_subcontent_req)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colors.surface,
                    unfocusedContainerColor = MaterialTheme.colors.surface,
                    disabledContainerColor = MaterialTheme.colors.surface,
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled
            )
            if (enabled) {
                Text(
                    text = stringResource(R.string.required_fields),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
        Button(
            onClick = {
                onSaveClick()
                      },
            enabled = routineUiState.isEntryValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
}
