/*
package com.example.merchtools.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.merchtools.ui.MerchToolsArgs.AUDIT_ITEM_ID_ARG
import com.example.merchtools.ui.MerchToolsArgs.USER_MESSAGE_ARG
import com.example.merchtools.ui.MerchToolsScreens.ADD_ITEMS_SCREEN
import com.example.merchtools.ui.MerchToolsScreens.AUDIT_SCREEN
import com.example.merchtools.ui.MerchToolsScreens.HISTORY_SCREEN
import com.example.merchtools.ui.MerchToolsScreens.HOME_SCREEN
import com.example.merchtools.ui.MerchToolsScreens.REPORT_SCREEN
import com.example.merchtools.ui.MerchToolsScreens.SCAN_SCREEN
import com.example.merchtools.ui.MerchToolsScreens.SEARCH_SCREEN

*/
/**
 * Screens used in Merch Tools
 *//*

private object MerchToolsScreens {
    const val HOME_SCREEN = "home"
    const val SCAN_SCREEN = "scan"
    const val AUDIT_SCREEN = "audit"
    const val SEARCH_SCREEN = "search"
    const val ADD_ITEMS_SCREEN = "add_items"
    const val REPORT_SCREEN = "report"
    const val HISTORY_SCREEN = "history"
}

*/
/**
 * Destination args
 *//*

object MerchToolsArgs {
    const val USER_MESSAGE_ARG = "userMessage"
    const val AUDIT_ID_ARG = "auditId"
    const val TITLE_ARG = "title"
    const val AUDIT_ITEM_ID_ARG = "auditItemId"
}

*/
/**
 * Destinations used in MerchTools navigation
 *//*

object MerchToolsDestinations {
    const val HOME_ROUTE = HOME_SCREEN
    const val SCAN_ROUTE = SCAN_SCREEN
    const val AUDIT_ROUTE = AUDIT_SCREEN
    const val SEARCH_ROUTE = SEARCH_SCREEN
    const val ADD_ITEMS_ROUTE = ADD_ITEMS_SCREEN
    const val REPORT_ROUTE = REPORT_SCREEN
    const val HISTORY_ROUTE = HISTORY_SCREEN
}

// Models navigation
class MerchToolsNavigation(private val navController: NavController) {

    fun navigateToScan(userMessage: Int = 0) {
        val navigatesFromDrawer = userMessage == 0
        navController.navigate(
            SCAN_SCREEN.let {
                if(userMessage != 0) "$it?$USER_MESSAGE_ARG=$userMessage" else it
            }
        ){
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = !navigatesFromDrawer
                saveState = navigatesFromDrawer
            }
            launchSingleTop = true
            restoreState = navigatesFromDrawer
        }
    }

    fun navigateToSearch() {
        navController.navigate(MerchToolsDestinations.SEARCH_ROUTE) {
            // pop up to the start destination of the graph
            // avoid building large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToAudit(auditId: Long) {
        navController.navigate("$AUDIT_SCREEN/$auditId")
    }

    fun navigateToAddItem(auditItemId: Long?) {
        navController.navigate(
            "$ADD_ITEMS_SCREEN/$auditItemId".let {
                if(auditItemId != null) "$it?$AUDIT_ITEM_ID_ARG=$auditItemId" else it
            }
        )
    }
}*/
