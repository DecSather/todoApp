package com.example.compose.rally.ui.routine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
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
import com.example.compose.rally.data.Routine
import com.example.compose.rally.ui.AppViewModelProvider
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
//waiting implement: 色圈有问题，需要同步routine数据
@Composable
fun SingleRoutineScreen(
    navigateBack: () -> Unit,
    viewModel: SingleRoutineViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    SingleRoutineBody(
        itemUiState = viewModel.routineUiState,
        onRoutineValueChange= viewModel::updateUiState,
        onSaveClick = {
            coroutineScope.launch {
                viewModel.updateRoutine()
                navigateBack()
            }
        },
    )
}

@Composable
fun SingleRoutineBody(
    itemUiState: RoutineUiState,
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
        
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
//        content
            OutlinedTextField(
                value = itemUiState.routine.content,
                onValueChange = {
                    onRoutineValueChange(itemUiState.routine.copy(content = it ))
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
                value = if(rankText.equals("-1"))itemUiState.routine.rank.toString() else rankText,
                onValueChange ={
                        newText ->
                    rankText = newText
                    if(rankText.isNotEmpty())
                        onRoutineValueChange(itemUiState.routine.copy(rank = rankText.toInt() ))
                    else onRoutineValueChange(itemUiState.routine.copy(rank = -1))
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
                value = if(creditText.equals("0.0"))itemUiState.routine.credit.toString() else creditText,
                onValueChange = {
                        newText ->
                    creditText = newText
                    if(creditText.isNotEmpty())
                        onRoutineValueChange(itemUiState.routine.copy(credit = creditText.toFloat()))
                    else onRoutineValueChange(itemUiState.routine.copy(credit = 0f))
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
                value = itemUiState.routine.subcontent,
                onValueChange = {
                    onRoutineValueChange(itemUiState.routine.copy(subcontent = it ))
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
            enabled = itemUiState.isEntryValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
}
