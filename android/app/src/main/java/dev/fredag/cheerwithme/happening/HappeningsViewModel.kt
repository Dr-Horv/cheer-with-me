package dev.fredag.cheerwithme.happening

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.fredag.cheerwithme.BaseViewModel
import dev.fredag.cheerwithme.FetchStatus
import dev.fredag.cheerwithme.data.backend.Happening
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class HappeningsViewState(
    val happenings: List<Happening> = listOf(),
    val showError: Boolean = false,
    val fetchStatus: FetchStatus = FetchStatus.Default,
)

sealed class HappeningsViewActions {
    object RefreshHappnings : HappeningsViewActions()
    object ClearErrorMessage : HappeningsViewActions()
}

@HiltViewModel
class HappeningsViewModel @Inject constructor(private val happeningsRepository: HappeningsRepository) :
    BaseViewModel<HappeningsViewState, HappeningsViewActions, Nothing>() {

    private fun loadHappenings() {
        viewModelScope.launch {
            setState(
                viewState.value.copy(
                    fetchStatus = FetchStatus.Loading
                )
            )
            happeningsRepository.getHappenings()
                .collect { res ->
                    res.fold(
                        onSuccess = {
                            setState { prev ->
                                prev.copy(
                                    happenings = it,
                                    fetchStatus = FetchStatus.Default
                                )
                            }
                        },
                        onFailure = { err ->
                            setState {
                                it.copy(
                                    showError = true,
                                    fetchStatus = FetchStatus.Error(err.toString())
                                )
                            }
                        }

                    )
                }
        }
    }

    override fun handleAction(it: HappeningsViewActions) {
        when (it) {
            HappeningsViewActions.RefreshHappnings -> loadHappenings()
            HappeningsViewActions.ClearErrorMessage -> setState { it.copy(showError = false) }
        }
    }

    override fun initialViewState() = HappeningsViewState()
}