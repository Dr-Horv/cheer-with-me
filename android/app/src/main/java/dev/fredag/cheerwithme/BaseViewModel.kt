package dev.fredag.cheerwithme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class BaseViewModel<ViewState, ViewAction, ViewEvent> : ViewModel() {
    /**
     * The state of the view this view model is representing
     */
    private val _viewState by lazy { MutableStateFlow(initialViewState()) }

    /**
     * The readonly state for the view
     */
    val viewState = _viewState.asStateFlow()

    /**
     * Flow for actions coming from the view to the view model.
     * Actions are fired from the sendActions method below
     */
    private val actionsFlow: MutableSharedFlow<ViewAction> = MutableSharedFlow()

    init {
        // Collect actions from the actionsFlow and give them to the
        // handleAction method implemented by the the concrete view model
        viewModelScope.launch(Dispatchers.IO) {
            actionsFlow.collect {
                handleAction(it)
            }
        }
    }

    /**
     * Lets the UI send actions to the view model
     */
    fun sendAction(action: ViewAction) = viewModelScope.launch {
        actionsFlow.emit(action)
    }

    /**
     * Setter for the state only available to inheriting view models
     */
    protected fun setState(state: ViewState) {
        _viewState.value = state
    }

    protected fun setState(updateState: (previousState: ViewState) -> ViewState) =
        setState(updateState(viewState.value))

    /**
     * This function gets called for every action emitted from the view using the sendAction function
     */
    protected abstract fun handleAction(it: ViewAction)

    /**
     * Declare the default value of the view state to initialize the _viewState
     */
    protected abstract fun initialViewState(): ViewState
}