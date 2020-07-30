package dev.fredag.cheerwithme.friends

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dev.fredag.cheerwithme.data.FriendsRepository
import dev.fredag.cheerwithme.data.backend.UserFriends
import dev.fredag.cheerwithme.data.backend.UserId
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapLatest

@FlowPreview
@OptIn(ExperimentalCoroutinesApi::class)
class FriendsViewModel @ViewModelInject constructor(private val friendsRepository: FriendsRepository) :
    ViewModel() {
    private val friendsChannel = ConflatedBroadcastChannel(UserFriends())
    private var _currentRefresh: Job? = null

    init {
        fetchFriends()
    }

    private fun fetchFriends() {
        _currentRefresh?.cancel()
        _currentRefresh = viewModelScope.launch(Dispatchers.IO) {
            friendsRepository.getFriends().collect {
                Log.d("FriendsViewModel", it.toString())
                friendsChannel.offer(it)
                Log.d("FriendsViewModel", "Offer done")
            }
        }
    }

    val friends: LiveData<UserFriends> = friendsChannel.asFlow().mapLatest {
        Log.d("FriendsViewModel", "Inside friends flow $it")
        it
    }.asLiveData()

    fun acceptFriendRequest(userId: UserId) {
        Log.d("FriendsViewModel", "acceptFriendRequest $userId")
        viewModelScope.launch {
            friendsRepository.acceptFriendRequest(userId)
            fetchFriends()
        }
    }
}