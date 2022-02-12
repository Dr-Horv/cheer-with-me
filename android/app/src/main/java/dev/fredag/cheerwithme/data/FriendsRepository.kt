package dev.fredag.cheerwithme.data

import dev.fredag.cheerwithme.data.backend.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

sealed class Result<T> {
    class Ok<T>(val r: T) : Result<T>()
    class Err<T>(val e: String) : Result<T>()
}

class FriendsRepository @Inject constructor(
    private val backendService: BackendService
) {
    fun getFriends(): Flow<Result<UserFriends>> = flow {
        try {
            val request = backendService.friends()
            if (request.isSuccessful) {
                request.body()?.let {
                    emit(Result.Ok(it))
                } ?: emit(Result.Err("Friends list is empty"))
            } else {
                emit(Result.Err(request.toString()))
            }
        } catch (e: Exception) {
            emit(Result.Err("Could not access server $e"))
        }


    }

    suspend fun acceptFriendRequest(userId: UserId) {
        backendService.acceptFriendRequest(AcceptFriendRequest(userId))
    }

    suspend fun sendFriendRequest(userId: UserId) {
        backendService.sendFriendRequest(SendFriendRequest(userId = userId))
    }
}