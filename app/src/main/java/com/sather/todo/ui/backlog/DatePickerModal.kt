package com.sather.todo.ui.backlog

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sather.todo.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {
    
    val datePickerState = rememberDatePickerState(
        selectedDate.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(
                        datePickerState.selectedDateMillis?.let {
                            Instant.ofEpochMilli(it)
                                .atZone(ZoneId.systemDefault()) // 使用系统默认时区
                                .toLocalDate()
                        }
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
                    text = stringResource(R.string.copy_action),
                    
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
