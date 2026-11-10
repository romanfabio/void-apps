package io.github.romanfabio.voidapps.core.designsystem.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.romanfabio.voidapps.core.designsystem.theme.VoidTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class VoidErrorStateTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `shows message and invokes retry on click`() {
        var retries = 0
        composeRule.setContent {
            VoidTheme { VoidErrorState(message = "Failed", onRetry = { retries++ }) }
        }

        composeRule.onNodeWithText("Failed").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").performClick()

        assertEquals(1, retries)
    }

    @Test
    fun `hides retry button when no retry callback is given`() {
        composeRule.setContent {
            VoidTheme { VoidErrorState(message = "Failed") }
        }

        composeRule.onNodeWithText("Retry").assertDoesNotExist()
    }
}
