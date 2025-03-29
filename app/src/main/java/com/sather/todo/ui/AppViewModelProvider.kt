package com.sather.todo.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sather.todo.ToDoApplication
import com.sather.todo.ui.backlog.BacklogHomeViewModel
import com.sather.todo.ui.backlog.SingleBacklogViewModel
import com.sather.todo.ui.routine.SingleRoutineViewModel


object AppViewModelProvider {
    val Factory = viewModelFactory {

//            SingleRoutine
        initializer {
            SingleRoutineViewModel(
                this.createSavedStateHandle(),
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
