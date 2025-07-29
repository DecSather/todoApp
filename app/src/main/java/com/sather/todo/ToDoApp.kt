package com.sather.todo

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sather.todo.ui.navigation.ToDoNavHost
import com.sather.todo.ui.theme.ToDoTheme

// Screens to be displayed in the top RallyTabRow
@Composable
fun ToDoApp(navController: NavHostController = rememberNavController()) {
    ToDoTheme {
            ToDoNavHost(
                navController = navController,
            )
    }
}
