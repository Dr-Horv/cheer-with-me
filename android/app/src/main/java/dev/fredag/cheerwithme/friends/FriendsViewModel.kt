package dev.fredag.cheerwithme.friends

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.fredag.cheerwithme.data.FriendsRepository
import dev.fredag.cheerwithme.data.backend.UserFriends
import dev.fredag.cheerwithme.data.backend.UserId
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@FlowPreview
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FriendsViewModel @Inject constructor(private val friendsRepository: FriendsRepository) :
    ViewModel() {
    private val friendsChannel = MutableStateFlow(UserFriends())
    private var _currentRefresh: Job? = null

    init {
        fetchFriends()
    }

    private fun fetchFriends() {
        _currentRefresh?.cancel()
        _currentRefresh = viewModelScope.launch(Dispatchers.IO) {
            friendsRepository.getFriends().collect {
                Log.d("FriendsViewModel", it.toString())
                friendsChannel.value = it
                Log.d("FriendsViewModel", "Offer done")
            }
        }
    }

    val friends: LiveData<UserFriends> = friendsChannel.mapLatest {
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