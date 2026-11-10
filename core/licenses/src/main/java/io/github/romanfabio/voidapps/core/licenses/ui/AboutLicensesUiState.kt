package io.github.romanfabio.voidapps.core.licenses.ui

import com.mikepenz.aboutlibraries.Libs

sealed interface AboutLicensesUiState {
    data object Loading : AboutLicensesUiState

    data class Success(
        val appName: String,
        val versionName: String,
        val versionCode: Long,
        val licenseText: String,
        val libraries: Libs,
    ) : AboutLicensesUiState

    data object Error : AboutLicensesUiState
}
