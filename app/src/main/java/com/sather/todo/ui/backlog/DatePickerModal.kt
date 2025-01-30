package com.sather.todo.ui.backlog

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sather.todo.R
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(selectedDate?.let { convertLocalDateToMillis(it) })
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(
                        LocalDate.parse(
                            datePickerState.selectedDateMillis?.let { convertMillisToDate(it) },
                            formatter
                        )
                    )
                    onDismiss()
                }
            ) {
                Text(
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineMedium,
                    text = stringResource(R.string.save_action),
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineMedium,
                    text = stringResource(R.string.cancel_action),
                    
                    )
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            title = {},
            dateFormatter = DatePickerDefaults.dateFormatter("yyyy-MM", "yyyy-MM-dd", "yyyy-MM-dd")
        )
    }
}
