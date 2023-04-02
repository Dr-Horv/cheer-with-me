package dev.fredag.cheerwithme.repository

import com.fasterxml.jackson.module.kotlin.readValue
import dev.fredag.cheerwithme.model.UserFriendsEvent
import dev.fredag.cheerwithme.model.UserFriendsEvents
import dev.fredag.cheerwithme.model.UserId
import dev.fredag.cheerwithme.objectMapper
import dev.fredag.cheerwithme.service.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.select
import org.joda.time.DateTime

class UserFriendsEventsRepository {

    suspend fun addEvents(userFriendsEvents: List<UserFriendsEvent>) = Database.dbQuery {
        UserFriendsEvents.batchInsert(userFriendsEvents) {
            this[UserFriendsEvents.userId] = it.userId
            this[UserFriendsEvents.timestamp] = DateTime(it.timestamp.toEpochMilli())
            this[UserFriendsEvents.eventData] = objectMapper.writeValueAsString(it)
        }.size
    }

    suspend fun readEvents(userId: UserId): List<UserFriendsEvent> = Database.dbQuery {
        UserFriendsEvents.select { UserFriendsEvents.userId.eq(userId) }
            .orderBy(UserFriendsEvents.timestamp to SortOrder.ASC)
            .map { row: ResultRow ->
                objectMapper.readValue<UserFriendsEvent>(row[UserFriendsEvents.eventData])
            }
    }
}