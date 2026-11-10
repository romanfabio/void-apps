package io.github.romanfabio.voidapps.template.feature.home.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.romanfabio.voidapps.core.designsystem.component.VOID_LOADING_STATE_TAG
import io.github.romanfabio.voidapps.core.designsystem.theme.VoidTheme
import io.github.romanfabio.voidapps.template.feature.home.data.model.Item
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class HomeScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val actions = mutableListOf<HomeAction>()
    private var aboutOpened = false

    private fun setContent(uiState: HomeUiState) {
        composeRule.setContent {
            VoidTheme {
                HomeScreen(
                    uiState = uiState,
                    onAction = { actions += it },
                    onOpenAbout = { aboutOpened = true },
                    horizontalPadding = VoidTheme.spacing.lg,
                )
            }
        }
    }

    @Test
    fun `loading state shows the loading indicator`() {
        setContent(HomeUiState.Loading)

        composeRule.onNodeWithTag(VOID_LOADING_STATE_TAG).assertIsDisplayed()
    }

    @Test
    fun `error state shows the error message`() {
        setContent(HomeUiState.Error)

        composeRule.onNodeWithText("Couldn't load your items.").assertIsDisplayed()
    }

    @Test
    fun `empty success shows the empty state`() {
        setContent(HomeUiState.Success(emptyList()))

        composeRule.onNodeWithText("No items yet").assertIsDisplayed()
    }

    @Test
    fun `items are listed and can be deleted`() {
        setContent(HomeUiState.Success(listOf(Item(7, "Item 7"))))

        composeRule.onNodeWithText("Item 7").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Delete Item 7").performClick()

        assertEquals(listOf<HomeAction>(HomeAction.DeleteItem(7)), actions)
    }

    @Test
    fun `add button adds an item named after the next index`() {
        setContent(HomeUiState.Success(listOf(Item(1, "Item 1"))))

        composeRule.onNodeWithContentDescription("Add item").performClick()

        assertEquals(listOf<HomeAction>(HomeAction.AddItem("Item 2")), actions)
    }

    @Test
    fun `about action opens about and licenses`() {
        setContent(HomeUiState.Success(emptyList()))

        composeRule.onNodeWithContentDescription("About and licenses").performClick()

        assertEquals(true, aboutOpened)
    }
}
