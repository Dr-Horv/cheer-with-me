package dev.fredag.cheerwithme.data

import androidx.lifecycle.MutableLiveData

object UserState {
    var loggedIn: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
}