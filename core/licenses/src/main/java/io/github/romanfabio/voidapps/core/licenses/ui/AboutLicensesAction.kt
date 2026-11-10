package io.github.romanfabio.voidapps.core.licenses.ui

sealed interface AboutLicensesAction {
    data object Retry : AboutLicensesAction
}
