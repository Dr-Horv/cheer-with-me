package dev.fredag.cheerwithme.happening

import dev.fredag.cheerwithme.data.backend.BackendService
import dev.fredag.cheerwithme.data.backend.CreateHappening
import dev.fredag.cheerwithme.data.backend.Happening
import dev.fredag.cheerwithme.data.backend.HappeningId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class UnauthorizedException : Exception()

class HappeningsRepository @Inject constructor(
    private val backendService: BackendService
) {
    private val happenings = MutableStateFlow<Map<HappeningId, Happening>>(emptyMap())

    suspend fun createHappening(happening: CreateHappening): Response<Happening> {
        val resp = backendService.createHappening(
            happening
        )
        if (resp.isSuccessful && resp.body() != null) {
            resp.body()?.let {
                val hs = happenings.value.toMutableMap()
                hs[it.happeningId] = it
                happenings.emit(hs.toMap())
            }
        }

        return resp
    }

    suspend fun getHappenings(): Flow<Result<List<Happening>>> = flow {
        try {
            val happeningsResp = backendService.getHappenings()
            if (happeningsResp.isSuccessful && happeningsResp.body() != null) {
                happeningsResp.body()?.let { newHappenings ->
                    happenings.value = newHappenings.toMapByKey { it.happeningId }
                    emit(Result.success(happenings.value.values.toList()))
                } ?: emit(Result.failure(Exception("Getting happenings failed")))

            } else {
                emit(Result.failure(UnauthorizedException()))
            }

        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getHappening(id: HappeningId): Flow<Happening> = flow {
        val happening = backendService.getHappening(id)
        if (happening.isSuccessful) {
            happening.body()?.let { newHappning ->
                emit(newHappning)
                val hs = happenings.value.toMutableMap()
                hs[newHappning.happeningId] = newHappning
                happenings.value = hs

            }
        }
    }
}

