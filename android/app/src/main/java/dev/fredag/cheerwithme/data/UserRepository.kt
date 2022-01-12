package dev.fredag.cheerwithme.data

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.fredag.cheerwithme.data.backend.BackendModule
import dev.fredag.cheerwithme.data.backend.BackendService
import dev.fredag.cheerwithme.data.backend.GoogleUserSignInRequest
import dev.fredag.cheerwithme.data.backend.User
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val backendService: BackendService
) {
    val usersMatchingSearch = MutableStateFlow<List<User>>(emptyList())

    suspend fun loginWithGoogle(serverAuthCode: String, token: String) {
        BackendModule.setAccessKey(context, token)
        val resp = backendService.loginGoogle(GoogleUserSignInRequest(serverAuthCode))
        BackendModule.setAccessKey(context, resp.accessToken)
        Log.d("UserRepository", "Access token ${resp.accessToken} set")
    }

    suspend fun searchUserByNick(searchString: String) {
        val resp = backendService.searchUsersByNick(searchString)
        if (resp.isSuccessful) {
            resp.body()?.let {
                usersMatchingSearch.value = it
            }
        }
    }
}