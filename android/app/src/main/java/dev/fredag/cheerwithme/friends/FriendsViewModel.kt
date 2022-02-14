package dev.fredag.cheerwithme.friends

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.fredag.cheerwithme.BaseViewModel
import dev.fredag.cheerwithme.FetchStatus
import dev.fredag.cheerwithme.data.FriendsRepository
import dev.fredag.cheerwithme.data.UserRepository
import dev.fredag.cheerwithme.data.backend.UserFriends
import dev.fredag.cheerwithme.data.backend.UserId
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

sealed class FriendsViewActions {
    object ClearFetchError : FriendsViewActions()
    object FetchFriends : FriendsViewActions()
    class AcceptFriendRequest(val userId: UserId) : FriendsViewActions()
}

data class FriendsViewState(
    val showError: Boolean = false,
    val fetchStatus: FetchStatus = FetchStatus.Default,
    val friendsModel: UserFriends = UserFriends()
)

@FlowPreview
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val friendsRepository: FriendsRepository,
    private val userRepository: UserRepository
) : BaseViewModel<FriendsViewState, FriendsViewActions, Nothing>() {
    private var _currentRefresh: Job? = null

    override fun handleAction(it: FriendsViewActions) {
        when (it) {
            FriendsViewActions.ClearFetchError -> {
                setState {
                    it.copy(showError = false)
                }
            }
            FriendsViewActions.FetchFriends -> fetchFriends()
            is FriendsViewActions.AcceptFriendRequest -> acceptFriendRequest(it.userId)
        }
    }

    private fun fetchFriends() {
        _currentRefresh?.cancel()
        _currentRefresh = viewModelScope.launch(Dispatchers.IO) {
            setState {
                it.copy(fetchStatus = FetchStatus.Loading)
            }
            friendsRepository.getFriends().collect { res ->
                setState { prev ->
                    res.fold(onSuccess = {
                        prev.copy(
                            showError = false,
                            fetchStatus = FetchStatus.Default,
                            friendsModel = it
                        )
                    },
                        onFailure = {
                            prev.copy(
                                showError = true,
                                fetchStatus = FetchStatus.Error(it.toString())
                            )
                        })
                }
            }
        }
    }

    // Todo extract this to another view model only used for friend search
    val usersMatchingSearch = userRepository.usersMatchingSearch.asLiveData()

    fun searchUserByNick(nick: String) {
        viewModelScope.launch {
            userRepository.searchUserByNick(nick)
        }
    }


    private fun acceptFriendRequest(userId: UserId) {
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

    override fun initialViewState() = FriendsViewState()
}