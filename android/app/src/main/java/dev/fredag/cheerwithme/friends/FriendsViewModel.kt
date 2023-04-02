package dev.fredag.cheerwithme.friends

import android.util.Log
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.fredag.cheerwithme.BaseViewModel
import dev.fredag.cheerwithme.FetchStatus
import dev.fredag.cheerwithme.data.FriendsRepository
import dev.fredag.cheerwithme.data.UserRepository
import dev.fredag.cheerwithme.data.backend.User
import dev.fredag.cheerwithme.data.backend.UserFriends
import dev.fredag.cheerwithme.data.backend.UserId
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

enum class FriendStatus {
    IsFriend, SentRequest, ReceivedRequest, Unknown
}

fun getFriendStatus(user: User, friends: UserFriends): FriendStatus {
    return when (user) {
        in friends.friends -> {
            FriendStatus.IsFriend
        }
        in friends.outgoingFriendRequests -> {
            FriendStatus.SentRequest
        }
        in friends.incomingFriendRequests -> {
            FriendStatus.ReceivedRequest
        }
        else -> {
            FriendStatus.Unknown
        }
    }
}

data class UserWithFriendStatus(
    val user: User,
    val status: FriendStatus,
    val loading: Boolean
)

sealed class FriendsViewActions {
    object ClearFetchError : FriendsViewActions()
    object FetchFriends : FriendsViewActions()
    class AcceptFriendRequest(val userId: UserId) : FriendsViewActions()
    class SendFriendRequest(val userid: UserId) : FriendsViewActions()
}

data class FriendsViewState(
    val showError: Boolean = false,
    val fetchStatus: FetchStatus = FetchStatus.Default,
    val friendsModel: UserFriends = UserFriends(),
    val usersMatchingSearch: List<UserWithFriendStatus> = emptyList(),
    val usersSearchErrorMessage: String? = null
)

sealed class FriendsViewModelEvent {
    object AddFriendFailure : FriendsViewModelEvent()
    object FetchFriendsFailed : FriendsViewModelEvent()
}

@FlowPreview
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val friendsRepository: FriendsRepository,
    private val userRepository: UserRepository
) : BaseViewModel<FriendsViewState, FriendsViewActions, FriendsViewModelEvent>() {
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
            is FriendsViewActions.SendFriendRequest -> {
                viewModelScope.launch { sendFriendRequest(it) }
            }
        }
    }

    private suspend fun sendFriendRequest(it: FriendsViewActions.SendFriendRequest) {
        setState { prev ->
            prev.copy(usersMatchingSearch = prev.usersMatchingSearch.map { u ->
                if (u.user.id == it.userid) u.copy(
                    loading = true
                ) else u
            })
        }
        friendsRepository.sendFriendRequest(it.userid).fold(onSuccess = {
            updateFriends()

            setState { prev ->
                prev.copy(usersMatchingSearch = prev.usersMatchingSearch.map { u ->
                    u.copy(
                        status = getFriendStatus(u.user, prev.friendsModel)
                    )
                })
            }
        },
            onFailure = {
                Timber.i("Add friend request failed $it")
                it.printStackTrace()
                sendEvent(FriendsViewModelEvent.AddFriendFailure)
            })

        setState { prev ->
            prev.copy(usersMatchingSearch = prev.usersMatchingSearch.map { u ->
                u.copy(
                    loading = false,
                )
            })
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

    private suspend fun updateFriends() {
        friendsRepository.getFriends().collect { res ->

            res.fold(onSuccess = {
                setState { prev ->
                    prev.copy(
                        friendsModel = it
                    )
                }
            },
                onFailure = {
                    sendEvent(FriendsViewModelEvent.FetchFriendsFailed)
                })

        }
    }

    fun searchUserByNick(nick: String) {
        viewModelScope.launch {

            val getUsersByNickFlow = userRepository.getUserByNick(nick).map {
                it.fold(onSuccess = { users ->
                    users
                }, onFailure = {
                    setState { prev ->
                        prev.copy(
                            usersSearchErrorMessage = "Failed to fetch new users"
                        )
                    }
                    emptyList()
                })
            }

            val getFriendsFlow = friendsRepository.getFriends().map {
                it.fold(onSuccess = { friends ->
                    friends
                }, onFailure = {
                    setState { prev ->
                        prev.copy(
                            usersSearchErrorMessage = "Failed to fetch friends"
                        )
                    }
                    viewState.value.friendsModel
                })
            }

            combine(getFriendsFlow, getUsersByNickFlow) { friends, users ->
                users.map { user ->
                    UserWithFriendStatus(
                        user,
                        getFriendStatus(user, friends),
                        false
                    )
                }
            }.collect {
                setState { prev ->
                    prev.copy(
                        usersMatchingSearch = it,
                        usersSearchErrorMessage = null
                    )
                }
            }
        }
    }

    private fun acceptFriendRequest(userId: UserId) {
        viewModelScope.launch {
            friendsRepository.acceptFriendRequest(userId)
            fetchFriends()
        }
    }

    override fun initialViewState() = FriendsViewState()
}
