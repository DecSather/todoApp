package com.example.compose.rally

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.compose.rally.ui.AppViewModelProvider
import com.example.compose.rally.ui.components.RallyTabRow
import com.example.compose.rally.ui.navigation.*
import com.example.compose.rally.ui.theme.RallyTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RallyApp() {
    RallyTheme {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination
        val currentScreen = rallyTabRowScreens.find {
            it.route == currentDestination?.route
                    || (it == Backlogs && currentDestination?.route?.startsWith( SingleBacklog.route ) == true)
                    || (it == Accounts && currentDestination?.route?.startsWith( SingleAccount.route ) == true)
        } ?: Backlogs
        
        Scaffold(
            containerColor = if(isSystemInDarkTheme()) Color(0xFF26282F) else Color(0xFFD4E5EF),
            topBar = {
                RallyTabRow(
                    allScreens = rallyTabRowScreens,
                    onTabSelected = { newScreen ->
                        navController.navigateSingleTopTo(newScreen.route)
                    },
                    currentScreen = currentScreen
                )
            }
        ) { innerPadding ->
            RallyNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
