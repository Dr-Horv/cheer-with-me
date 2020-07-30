package dev.fredag.cheerwithme.data

import dev.fredag.cheerwithme.data.backend.AcceptFriendRequest
import dev.fredag.cheerwithme.data.backend.BackendService
import dev.fredag.cheerwithme.data.backend.UserFriends
import dev.fredag.cheerwithme.data.backend.UserId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
}