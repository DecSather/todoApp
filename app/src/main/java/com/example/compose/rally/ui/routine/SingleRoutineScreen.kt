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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.compose.rally.R
import com.example.compose.rally.data.Routine
import com.example.compose.rally.ui.AppViewModelProvider
import com.example.compose.rally.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch
//new Routine Entry-预添加类设计，非数据
object SingleRoutineDestination : NavigationDestination {
    override val route = "single_routine"
    override val icon = Icons.Filled.Check
    const val routineIdArg = "routineId"
    val routeWithArgs = "${route}/{${routineIdArg}}"
    val arguments = listOf(
        navArgument(routineIdArg) { type = NavType.IntType }
    )
    val deepLinks = listOf(
        navDeepLink { uriPattern = "rally://${route}/{${routineIdArg}}" }
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
    
    var contentText by remember { mutableStateOf("loading...") }
    var rankText by remember { mutableStateOf("-1") }
    var creditText by remember { mutableStateOf("0.0") }
    var subcontText by remember { mutableStateOf("empty") }
    
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
                value = if(contentText.equals("loading..."))itemUiState.routine.content else contentText,
                onValueChange = {
                        newText ->
                    contentText = newText
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
                value = if(subcontText.equals("empty"))itemUiState.routine.subcontent else subcontText,
                onValueChange = {
                        newText ->
                    subcontText = newText
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
                if (!contentText.equals("loading...")) {
                    onRoutineValueChange(itemUiState.routine.copy(content = contentText ))
                }
                if (!rankText.equals("-1")) {
                    onRoutineValueChange(itemUiState.routine.copy(rank=rankText.toInt()))
                }
                if (!creditText.equals("0.0")) {
                    onRoutineValueChange(itemUiState.routine.copy(credit = creditText.toFloat()))
                }
                if (!subcontText.equals("empty")) {
                    onRoutineValueChange(itemUiState.routine.copy(subcontent = subcontText ))
                }
                onSaveClick()
                      },
            enabled = itemUiState.isEntryValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
}
