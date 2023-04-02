package dev.fredag.cheerwithme.data

import dev.fredag.cheerwithme.data.backend.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FriendsRepository @Inject constructor(
    private val backendService: BackendService
) {
    fun getFriends(): Flow<Result<UserFriends>> = flow {
        try {
            val request = backendService.friends()
            if (request.isSuccessful) {
                request.body()?.let {
                    emit(Result.success(it))
                } ?: emit(Result.failure(Throwable("Friends list is empty")))
            } else {
                emit(Result.failure(Throwable(request.toString())))
            }
        } catch (e: Exception) {
            emit(Result.failure(Throwable("Could not access server $e")))
        }
    }

    suspend fun acceptFriendRequest(userId: UserId) {
        backendService.acceptFriendRequest(AcceptFriendRequest(userId))
    }

    suspend fun sendFriendRequest(userId: UserId): Result<Unit> {
        val request = backendService.sendFriendRequest(SendFriendRequest(userId = userId))

        return if (request.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Throwable(request.toString()))
        }
    }
}
