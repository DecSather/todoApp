/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.rally

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

/**
 * Contract for information needed on every Rally navigation destination
 */

sealed interface RallyDestination {
    val icon: ImageVector
    val route: String
}

//BacklogDestination
data object Backlogs : RallyDestination{
    override val icon =Icons.Filled.Timer
    override val route ="backlogs"
}

data object SingleBacklog : RallyDestination {
    override val icon = Icons.Filled.Timer
    override val route = "single_backlog"
    const val backlogTypeArg = "backlog_type"
    val routeWithArgs = "$route/{$backlogTypeArg}"
    val arguments = listOf(
        navArgument(backlogTypeArg) { type = NavType.StringType }
    )
    val deepLinks = listOf(
        navDeepLink { uriPattern = "rally://$route/{$backlogTypeArg}" }
    )
}

/**
 * Rally app navigation destinations
 */
data object Overview : RallyDestination {
    override val icon = Icons.Filled.Timer
    override val route = "overview"
}

data object Accounts : RallyDestination {
    override val icon = Icons.Filled.AttachMoney
    override val route = "accounts"
}

data object Bills : RallyDestination {
    override val icon = Icons.Filled.MoneyOff
    override val route = "bills"
}

data object SingleAccount : RallyDestination {
    override val icon = Icons.Filled.Money
    override val route = "single_account"
    const val accountTypeArg = "account_type"
    val routeWithArgs = "$route/{$accountTypeArg}"
    val arguments = listOf(
        navArgument(accountTypeArg) { type = NavType.StringType }
    )
    val deepLinks = listOf(
        navDeepLink { uriPattern = "rally://$route/{$accountTypeArg}" }
    )
}

data object SingleBill : RallyDestination {
    override val icon = Icons.Filled.MoneyOff
    override val route = "single_bill"
    const val billTypeArg = "bill_type"
    val routeWithArgs = "$route/{$billTypeArg}"
    val arguments = listOf(
        navArgument(billTypeArg) { type = NavType.StringType }
    )
    val deepLinks = listOf(
        navDeepLink { uriPattern = "rally://$route/{$billTypeArg}" }
    )
}

// Screens to be displayed in the top RallyTabRow
val rallyTabRowScreens = listOf(Backlogs,Overview, Accounts, Bills)
