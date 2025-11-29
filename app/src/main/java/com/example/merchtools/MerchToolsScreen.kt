package com.example.merchtools

//import com.ramcosta.composedestinations.generated.NavGraphs
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.merchtools.components.DetailedNavDrawer
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.AuditScreenDestination
import com.ramcosta.composedestinations.generated.destinations.EditAuditItemScreenDestination
import com.ramcosta.composedestinations.generated.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SearchScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.currentDestinationFlow
import com.ramcosta.composedestinations.utils.startDestination


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchToolsApp() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentDestinationAsState()
//    val title = currentBackStackEntry?.startDestination?.route ?: "Merch Tools"
    val currentDestination by navController.currentDestinationAsState()
    val title = when (currentDestination?.route) {
        HomeScreenDestination.route -> "Home"
        SearchScreenDestination.route -> "Search SKU"
        AuditScreenDestination.route -> "Audit"
        EditAuditItemScreenDestination.route -> "Edit Audit Item"
        else -> "Merch Tools"
    }

    DetailedNavDrawer(
        title = title,
        onNavigateHome = {
            navController.navigate(HomeScreenDestination.route) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = false
                }
                launchSingleTop = true
            }
        },
        onNavigateSearch = {
            navController.navigate(SearchScreenDestination.route) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = false
                }
                launchSingleTop = true
            }
        },
        /*onNavigateAudit = {
            navController.navigate(AuditScreenDestination(auditId = 0L).route) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = false
                }
                launchSingleTop = true
            }
        }*/
    ) { innerPadding ->
        DestinationsNavHost(
            navGraph = NavGraphs.root,
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}