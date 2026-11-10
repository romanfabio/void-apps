package io.github.romanfabio.voidapps.template

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Mandatory per-app smoke test (docs/TESTING.md §3). Run before every release. */
@RunWith(AndroidJUnit4::class)
class AppSmokeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appLaunches_aboutAndLicensesIsReachable() {
        composeRule.onNodeWithText("Items").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("About and licenses").performClick()

        composeRule.onNodeWithText("About & licenses").assertIsDisplayed()
    }
}
