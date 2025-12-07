package com.example.merchtools

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.merchtools.components.DetailedNavDrawer
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.AuditScreenDestination
import com.ramcosta.composedestinations.generated.destinations.EditAuditItemScreenDestination
import com.ramcosta.composedestinations.generated.destinations.HistoryScreenDestination
import com.ramcosta.composedestinations.generated.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SearchScreenDestination
import com.ramcosta.composedestinations.utils.currentDestinationAsState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchToolsApp() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentDestinationAsState()
    val currentDestination by navController.currentDestinationAsState()
    val currentRoute = currentDestination?.route


    val title = when (currentDestination?.route) {
        HomeScreenDestination.route -> "Home"
        SearchScreenDestination.route -> "Search SKU"
        AuditScreenDestination.route -> "Audit"
        EditAuditItemScreenDestination.route -> "Edit Audit Item"
        HistoryScreenDestination.route -> "Audit History"
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
                        saveState = false
                    }
                    launchSingleTop = true
                    restoreState = false
                } else {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = false
                }
            }
        }
    ) { innerPadding ->
        DestinationsNavHost(
            navGraph = NavGraphs.root,
            navController = navController,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}