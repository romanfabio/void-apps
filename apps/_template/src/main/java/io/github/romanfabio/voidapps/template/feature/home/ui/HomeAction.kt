package io.github.romanfabio.voidapps.template.feature.home.ui

import androidx.annotation.StringRes

sealed interface HomeAction {
    data class AddItem(val name: String) : HomeAction

    data class DeleteItem(val id: Long) : HomeAction
}

sealed interface HomeEvent {
    data class ShowMessage(@param:StringRes val messageRes: Int) : HomeEvent
}
