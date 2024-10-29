package com.example.compose.rally.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.compose.rally.RallyApplication
import com.example.compose.rally.ui.backlog.BacklogHomeViewModel
import com.example.compose.rally.ui.backlog.SingleBacklogViewModel
import com.example.compose.rally.ui.routine.RoutineHomeViewModel


object AppViewModelProvider {
    val Factory = viewModelFactory {
        
//            RoutineHome
        initializer {
            RoutineHomeViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.routinesRepository
            )
        }
//            SingleBacklog
        initializer {
            SingleBacklogViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.backlogsRepository
            )
        }
        
//            BacklogHome
        initializer {
            BacklogHomeViewModel(inventoryApplication().container.backlogsRepository)
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [InventoryApplication].
 */
fun CreationExtras.inventoryApplication(): RallyApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as RallyApplication)
