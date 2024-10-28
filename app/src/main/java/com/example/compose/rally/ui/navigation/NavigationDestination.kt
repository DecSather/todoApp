
package com.example.compose.rally.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

interface NavigationDestination {
    val route: String
    val icon: ImageVector
}
