package dev.fredag.cheerwithme.data

import androidx.lifecycle.MutableLiveData
import dev.fredag.cheerwithme.data.backend.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

object UserState {
    var loggedIn: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var user: MutableStateFlow<User> = MutableStateFlow(User(0L, "", null))
}