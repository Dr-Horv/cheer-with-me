package dev.fredag.cheerwithme

sealed class FetchStatus {
    object Default : FetchStatus()
    object Loading : FetchStatus()
    class Error(val message: String) : FetchStatus()
}