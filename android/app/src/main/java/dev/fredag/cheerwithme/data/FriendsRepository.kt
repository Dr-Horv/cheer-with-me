package dev.fredag.cheerwithme.data

import dev.fredag.cheerwithme.data.backend.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class FriendsRepository @Inject constructor(
    private val backendService: BackendService
) {
    fun getFriends(): Flow<UserFriends> = flow {
        val friends = backendService.friends()
        emit(friends)
    }

    suspend fun acceptFriendRequest(userId: UserId) {
        backendService.acceptFriendRequest(AcceptFriendRequest(userId))
    }

    suspend fun sendFriendRequest(userId: UserId) {
        backendService.sendFriendRequest(SendFriendRequest(userId = userId))
    }
}