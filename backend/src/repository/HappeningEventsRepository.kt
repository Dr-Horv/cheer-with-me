package dev.fredag.cheerwithme.repository

import com.fasterxml.jackson.module.kotlin.readValue
import dev.fredag.cheerwithme.model.*
import dev.fredag.cheerwithme.objectMapper
import dev.fredag.cheerwithme.service.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.select
import org.joda.time.DateTime

class HappeningEventsRepository {
    suspend fun addEvents(events: List<HappeningEvent>) = Database.dbQuery {
        HappeningEvents.batchInsert(events) {
            this[HappeningEvents.userId] = it.userId
            this[HappeningEvents.happeningId] = it.happeningId
            this[HappeningEvents.timestamp] = DateTime(it.timestamp.toEpochMilli())
            this[HappeningEvents.eventData] = objectMapper.writeValueAsString(it)
        }
    }

    suspend fun readEvents(happeningId: HappeningId): List<HappeningEvent> = Database.dbQuery {
        HappeningEvents.select { HappeningEvents.happeningId.eq(happeningId) }
            .orderBy(HappeningEvents.timestamp to SortOrder.ASC)
            .map { row: ResultRow ->
                objectMapper.readValue<HappeningEvent>(row[HappeningEvents.eventData])
            }
    }
}