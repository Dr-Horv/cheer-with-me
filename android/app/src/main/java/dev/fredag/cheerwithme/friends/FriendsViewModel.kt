package dev.fredag.cheerwithme.friends

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.fredag.cheerwithme.data.FriendsRepository
import dev.fredag.cheerwithme.data.Result
import dev.fredag.cheerwithme.data.UserRepository
import dev.fredag.cheerwithme.data.backend.UserFriends
import dev.fredag.cheerwithme.data.backend.UserId
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

sealed class FriendsViewActions {
    object ClearFetchError : FriendsViewActions()
}

sealed class FetchStatus {
    object Default : FetchStatus()
    object Loading : FetchStatus()
    class Error(val message: String) : FetchStatus()
}

class FriendsViewState(
    val showError: Boolean = false,
    val fetchStatus: FetchStatus = FetchStatus.Default,
    val friendsModel: UserFriends = UserFriends()
) {
    private fun modify(
        showError: Boolean = this.showError,
        fetchStatus: FetchStatus = this.fetchStatus,
        friendsModel: UserFriends = this.friendsModel,
    ): FriendsViewState = FriendsViewState(showError, fetchStatus, friendsModel)

    fun showError(showError: Boolean) = modify(showError = showError)

    fun fetchStatus(fetchStatus: FetchStatus) = modify(fetchStatus = fetchStatus)

    fun friendsModel(friendsModel: UserFriends) = modify(friendsModel = friendsModel)
}

@FlowPreview
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val friendsRepository: FriendsRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private var _currentRefresh: Job? = null
    private val _friendsViewState = MutableStateFlow(FriendsViewState())
    val friendsViewState = _friendsViewState.asStateFlow()

    private val actionsFlow: MutableSharedFlow<FriendsViewActions> = MutableSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            actionsFlow.collect {
                when (it) {
                    is FriendsViewActions.ClearFetchError -> {
                        _friendsViewState.value =
                            _friendsViewState.value.showError(false)
                    }
                }
            }
        }
    }

    fun sendAction(action: FriendsViewActions) = viewModelScope.launch {
        actionsFlow.emit(action)
    }

    fun fetchFriends() {
        _currentRefresh?.cancel()
        _currentRefresh = viewModelScope.launch(Dispatchers.IO) {
            _friendsViewState.value = _friendsViewState.value.fetchStatus(FetchStatus.Loading)
            friendsRepository.getFriends().collect {
                _friendsViewState.value = when (it) {
                    is Result.Err -> _friendsViewState.value
                        .showError(true)
                        .fetchStatus(FetchStatus.Error(it.e))
                    is Result.Ok -> _friendsViewState.value
                        .showError(false)
                        .fetchStatus(FetchStatus.Default)
                        .friendsModel(it.r)

                }
            }
        }
    }

    val usersMatchingSearch = userRepository.usersMatchingSearch.asLiveData()

    fun searchUserByNick(nick: String) {
        viewModelScope.launch {
            userRepository.searchUserByNick(nick)
        }
    }


    fun acceptFriendRequest(userId: UserId) {
        Log.d("FriendsViewModel", "acceptFriendRequest $userId")
        viewModelScope.launch {
            friendsRepository.acceptFriendRequest(userId)
            fetchFriends()
        }
    }

    fun sendFriendRequest(userId: UserId) {
        viewModelScope.launch {
            friendsRepository.sendFriendRequest(userId)
            fetchFriends()
        }
    }
}