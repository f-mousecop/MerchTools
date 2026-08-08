/**
 * Copyright (C) 2026 Charles Clark
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.merchtools

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.merchtools.ui.components.DetailedNavDrawer
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.DefaultFadingTransitions
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.AuditScreenDestination
import com.ramcosta.composedestinations.generated.destinations.EditAuditItemScreenDestination
import com.ramcosta.composedestinations.generated.destinations.EditSkuScreenDestination
import com.ramcosta.composedestinations.generated.destinations.GenerateReportScreenDestination
import com.ramcosta.composedestinations.generated.destinations.HistoryScreenDestination
import com.ramcosta.composedestinations.generated.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ScanBarCodeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SearchScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.StoreCatalogScreenDestination
import com.ramcosta.composedestinations.utils.currentDestinationAsState


/**
 * The main entry point and root composable for the Merch Tools application.
 *
 * This function sets up the core UI structure, including navigation. It uses a `rememberNavController`
 * to manage the navigation state and determines the current screen's title based on the route.
 *
 * The UI is built around a `DetailedNavDrawer`, which provides the main navigation drawer and top app bar.
 * The content area of the scaffold is populated by `DestinationsNavHost`, which handles the routing
 * and displays the appropriate screen composable based on the current navigation destination.
 *
 * Navigation logic within the drawer ensures a clean back stack, popping up to the start
 * destination and using `launchSingleTop` to avoid creating multiple instances of the same screen.
 *
 * @see DetailedNavDrawer
 * @see DestinationsNavHost
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchToolsApp() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentDestinationAsState()
    val currentDestination by navController.currentDestinationAsState()
    val currentRoute = currentDestination?.route


    // Determine the title based on the current destination
    val title = when (currentDestination?.route) {
        HomeScreenDestination.route -> "Merch Tools"
        SearchScreenDestination.route -> "Search SKU"
        EditSkuScreenDestination.route -> "SKU Entry"
        AuditScreenDestination.route -> "Audit"
        EditAuditItemScreenDestination.route -> "Edit Audit Item"
        HistoryScreenDestination.route -> "Audit History"
        GenerateReportScreenDestination.route -> "Audit Report"
        ScanBarCodeScreenDestination.route -> "Scan Barcode"
        StoreCatalogScreenDestination.route -> "Store Catalog"
        SettingsScreenDestination.route -> "Settings"
        else -> "Merch Tools"
    }

    DetailedNavDrawer(
        title = title,
        currentRoute = currentRoute,
        onDestinationClicked = { route ->
            navController.navigate(route) {
                val startDestination = navController.graph.startDestinationId

                if (route == HomeScreenDestination.route) {
                    popUpTo(startDestination) {
                        inclusive = true
                    }
                    launchSingleTop = true
                } else {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        }
    ) { innerPadding ->
        DestinationsNavHost(
            navGraph = NavGraphs.root,
            defaultTransitions = DefaultFadingTransitions,
            navController = navController,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}