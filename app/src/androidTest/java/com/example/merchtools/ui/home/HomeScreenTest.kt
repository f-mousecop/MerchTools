package com.example.merchtools.ui.home

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    // Simple test for clicking Open Audit -> nav to audit screen
    @Test
    fun clickingOpenAudit_emitsOpenAuditEvent() {
        var lastEvent: HomeEvent? = null

        composeRule.setContent {
            HomeScreenContent(
                state = HomeState(),

                onEvent = { event -> lastEvent = event },
                onNavigateToAuditHistory = {}
            )
        }

        composeRule
            .onNodeWithTag("open_audit_button")
            .performClick()

        assertEquals(HomeEvent.OpenAuditClicked, lastEvent)
    }

    // Simple test for clicking Audit History -> nav to history screen
    @Test
    fun clickingAuditHistory_callsNavigateCallback() {
        var historyClicked = false

        composeRule.setContent {
            HomeScreenContent(
                state = HomeState(),
                onEvent = {},
                onNavigateToAuditHistory = { historyClicked = true }
            )
        }

        composeRule
            .onNodeWithTag("audit_history_button")
            .performClick()

        assertTrue(historyClicked)
    }

    // Simple test for simulating clicking Start Audit -> show dialog, user input, and confirm (OK)
    @Test
    fun clickingStartAuditOk_emitsStartAuditClicked() {
        var lastEvent: HomeEvent? = null

        val testState = HomeState(
            userName = "Charles",
            storeName = "Test Store",
            storeId = 1L
        )

        composeRule.setContent {
            // We click the Start Audit button first to show the dialog.
            HomeScreenContent(
                state = testState,
                onEvent = { event -> lastEvent = event },
                onNavigateToAuditHistory = {}
            )
        }

        // Open the dialog
        composeRule.onNodeWithTag("start_audit_button").performClick()
        // Confirm
        composeRule.onNodeWithTag("start_audit_ok_button").performClick()

        assertEquals(HomeEvent.StartAuditClicked, lastEvent)
    }

}