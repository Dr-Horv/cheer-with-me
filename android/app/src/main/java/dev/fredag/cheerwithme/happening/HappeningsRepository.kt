package dev.fredag.cheerwithme.happening

import dev.fredag.cheerwithme.data.backend.BackendService
import dev.fredag.cheerwithme.data.backend.Happening
import dev.fredag.cheerwithme.data.backend.HappeningId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class HappeningsRepository @Inject constructor(
    private val backendService: BackendService
) {
    private val happenings = MutableStateFlow<Map<HappeningId, Happening>>(emptyMap())

    suspend fun getHappenings(): Flow<List<Happening>> = flow {

        val happeningsResp = backendService.getHappenings()
        if (happeningsResp.isSuccessful && happeningsResp.body() != null) {
            happeningsResp.body()?.let { newHappenings ->
                happenings.value = newHappenings.toMapByKey { it.happeningId }
                emit(newHappenings)
            }

        }

    }.onStart { emit(happenings.value.values.toList()) }

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

