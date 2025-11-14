package com.example.merchtools.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.merchtools.ui.MerchToolsDestinations.SCAN_ROUTE
import com.example.merchtools.ui.home.HomeScreen
import com.example.merchtools.ui.scansearch.ScanSearchScreen
import com.example.merchtools.ui.searchsku.SearchScreen
import kotlinx.coroutines.CoroutineScope

@Composable
fun MerchToolsNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    startDestination: String = MerchToolsDestinations.HOME_ROUTE,
//    navActions:
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = MerchToolsDestinations.HOME_ROUTE,
        modifier = modifier
    ) {
        composable(route = MerchToolsDestinations.HOME_ROUTE) {
            HomeScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                navigateToItemUpdate =  { navController.navigate(SCAN_ROUTE)},
                navigateToItemEntry = {
                    navController.navigate(SCAN_ROUTE)
                }
            )
        }
        composable(route = SCAN_ROUTE) {
            ScanSearchScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                navigateToItemUpdate =  { navController.navigate(MerchToolsDestinations.HOME_ROUTE)},
                navigateToItemEntry = {
                    navController.navigate(MerchToolsDestinations.SEARCH_ROUTE)
                }
            )
        }
        composable(route = MerchToolsDestinations.SEARCH_ROUTE) {
            SearchScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                navigateForth = { navController.navigate(MerchToolsDestinations.HOME_ROUTE)}

            )

        }
        composable(route = MerchToolsDestinations.AUDIT_ROUTE) {

        }
        composable(route = MerchToolsDestinations.ADD_ITEMS_ROUTE) {

        }
        composable(route = MerchToolsDestinations.REPORT_ROUTE) {

        }
        composable(route = MerchToolsDestinations.HISTORY_ROUTE) {

        }
    }
}