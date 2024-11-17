package com.example.compose.rally.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.compose.rally.ToDoApplication
import com.example.compose.rally.ui.backlog.BacklogHomeViewModel
import com.example.compose.rally.ui.backlog.SingleBacklogViewModel
import com.example.compose.rally.ui.routine.RoutineEntryViewModel
import com.example.compose.rally.ui.routine.SingleRoutineViewModel


object AppViewModelProvider {
    val Factory = viewModelFactory {

//            SingleRoutine
        initializer {
            SingleRoutineViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.routinesRepository
            )
        }
//            RoutineEntry
        initializer {
            RoutineEntryViewModel(
                inventoryApplication().container.routinesRepository
            )
        }
//            SingleBacklog
        initializer {
            SingleBacklogViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.backlogsRepository,
                inventoryApplication().container.routinesRepository
            )
        }

//            BacklogHome
        initializer {
            BacklogHomeViewModel(
                inventoryApplication().container.backlogsRepository,
                inventoryApplication().container.routinesRepository,
                )
        }
    }
}

fun CreationExtras.inventoryApplication(): ToDoApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ToDoApplication)
