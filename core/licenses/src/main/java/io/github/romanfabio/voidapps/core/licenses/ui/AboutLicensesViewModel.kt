package io.github.romanfabio.voidapps.core.licenses.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikepenz.aboutlibraries.Libs
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.romanfabio.voidapps.core.common.result.VoidResult
import io.github.romanfabio.voidapps.core.common.result.runSuspendCatching
import io.github.romanfabio.voidapps.core.licenses.data.AboutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutLicensesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: AboutRepository,
) : ViewModel() {

    private val librariesRes: Int =
        checkNotNull(savedStateHandle[AboutLicensesDestination.ARG_LIBRARIES_RES]) {
            "Missing ${AboutLicensesDestination.ARG_LIBRARIES_RES} argument"
        }

    private val _uiState = MutableStateFlow<AboutLicensesUiState>(AboutLicensesUiState.Loading)
    val uiState: StateFlow<AboutLicensesUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun onAction(action: AboutLicensesAction) {
        when (action) {
            AboutLicensesAction.Retry -> load()
        }
    }

    private fun load() {
        _uiState.value = AboutLicensesUiState.Loading
        viewModelScope.launch {
            val info = repository.load(librariesRes)
            val libraries = (info as? VoidResult.Success)?.let {
                runSuspendCatching { Libs.Builder().withJson(it.value.librariesJson).build() }
            }
            _uiState.value = if (info is VoidResult.Success && libraries is VoidResult.Success) {
                AboutLicensesUiState.Success(
                    appName = info.value.appName,
                    versionName = info.value.versionName,
                    versionCode = info.value.versionCode,
                    licenseText = info.value.licenseText,
                    libraries = libraries.value,
                )
            } else {
                AboutLicensesUiState.Error
            }
        }
    }
}
