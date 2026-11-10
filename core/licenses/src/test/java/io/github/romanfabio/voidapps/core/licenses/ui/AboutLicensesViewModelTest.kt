package io.github.romanfabio.voidapps.core.licenses.ui

import androidx.lifecycle.SavedStateHandle
import io.github.romanfabio.voidapps.core.common.result.VoidResult
import io.github.romanfabio.voidapps.core.licenses.data.AboutInfo
import io.github.romanfabio.voidapps.core.licenses.data.AboutRepository
import io.github.romanfabio.voidapps.core.testing.MainDispatcherRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertIs

// Robolectric: AboutLibraries' JSON parser logs through android.util.Log.
@RunWith(RobolectricTestRunner::class)
class AboutLicensesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeAboutRepository()

    private fun createViewModel() = AboutLicensesViewModel(
        savedStateHandle = SavedStateHandle(mapOf(AboutLicensesDestination.ARG_LIBRARIES_RES to LIBRARIES_RES)),
        repository = repository,
    )

    @Test
    fun `successful load exposes app info and libraries`() {
        repository.result = VoidResult.Success(INFO)

        val state = createViewModel().uiState.value

        val success = assertIs<AboutLicensesUiState.Success>(state)
        assertEquals("Void Test", success.appName)
        assertEquals("1.2.3", success.versionName)
        assertEquals(1_002_003L, success.versionCode)
        assertEquals(LIBRARIES_RES, repository.requestedRes)
    }

    @Test
    fun `repository failure exposes error`() {
        repository.result = VoidResult.Failure(IllegalStateException())

        assertEquals(AboutLicensesUiState.Error, createViewModel().uiState.value)
    }

    @Test
    fun `retry reloads after a failure`() {
        repository.result = VoidResult.Failure(IllegalStateException())
        val viewModel = createViewModel()

        repository.result = VoidResult.Success(INFO)
        viewModel.onAction(AboutLicensesAction.Retry)

        assertIs<AboutLicensesUiState.Success>(viewModel.uiState.value)
    }

    private class FakeAboutRepository : AboutRepository {
        var result: VoidResult<AboutInfo> = VoidResult.Failure(IllegalStateException("not set"))
        var requestedRes: Int? = null

        override suspend fun load(librariesRes: Int): VoidResult<AboutInfo> {
            requestedRes = librariesRes
            return result
        }
    }

    private companion object {
        const val LIBRARIES_RES = 42
        val INFO = AboutInfo(
            appName = "Void Test",
            versionName = "1.2.3",
            versionCode = 1_002_003L,
            licenseText = "MIT License",
            librariesJson = """{"libraries":[],"licenses":{}}""",
        )
    }
}
