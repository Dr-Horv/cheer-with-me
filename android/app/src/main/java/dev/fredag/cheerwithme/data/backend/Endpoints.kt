package dev.fredag.cheerwithme.data.backend

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BackendService {
    @POST("/login/google")
    suspend fun loginGoogle(@Body googleUserSignInRequest: GoogleUserSignInRequest): GoogleUserSignInResponse

    @GET("/friends/")
    suspend fun friends(): UserFriends

    @POST("/friends/sendFriendRequest/")
    suspend fun sendFriendRequest(@Body acceptFriendRequest: SendFriendRequest): Response<Void>

    @POST("/friends/acceptFriendRequest/")
    suspend fun acceptFriendRequest(@Body acceptFriendRequest: AcceptFriendRequest): Response<Void>
}