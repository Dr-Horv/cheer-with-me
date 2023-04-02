package dev.fredag.cheerwithme.service

import dev.fredag.cheerwithme.logger
import dev.fredag.cheerwithme.model.*
import dev.fredag.cheerwithme.repository.HappeningEventsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*

data class HappeningAggregate(
    val happeningId: HappeningId,
    val adminId: UserId,
    val name: String,
    val description: String,
    val time: Instant,
    val location: Location?,
    val attendees: List<UserId>,
    val awaiting: List<UserId>,
    val cancelled: Boolean,
    val cancelReason: String?
)

data class MutableHappeningAggregate(
    var happeningId: HappeningId,
    var adminId: UserId,
    var name: String,
    var description: String,
    var time: Instant,
    var location: Location? = null,
    val attendees: MutableList<UserId> = mutableListOf(),
    val awaiting: MutableList<UserId> = mutableListOf(),
    var cancelled: Boolean = false,
    var cancelReason: String? = null
)

fun MutableHappeningAggregate.toHappeningAggregate() =
    HappeningAggregate(happeningId, adminId, name, description, time, location, attendees, awaiting, cancelled, cancelReason)

class HappeningService(
    private val happeningEventsRepository: HappeningEventsRepository = HappeningEventsRepository(),
    private val userService: UserService,
    private val pushService: PushService
) {
    private val log by logger()

    suspend fun createHappening(
        userId: UserId,
        createHappeningRequest: CreateHappening
    ): Happening {
        val happeningId = UUID.randomUUID().toString()
        val happeningCreated = HappeningCreated(
            Instant.now(),
            userId,
            happeningId,
            createHappeningRequest.name,
            createHappeningRequest.description,
            createHappeningRequest.time,
            createHappeningRequest.location
        )

        val inviteEvents =
            createHappeningRequest.usersToInvite.map { UserInvitedToHappening(it, Instant.now(), happeningId, it) }
                .toList()
        
        happeningEventsRepository.addEvents(listOf(happeningCreated) + inviteEvents)
        val happening = getHappening(happeningId)!!
        sendInvitePushToUsers(createHappeningRequest.usersToInvite, happening)

        return happening
    }

    private suspend fun sendInvitePushToUsers(
        usersToInvite: List<UserId>,
        happening: Happening
    ) {
        withContext(Dispatchers.IO) {
            for (invitedId in usersToInvite) {
                launch { notifyUserOfInvite(happening, invitedId) }
            }
        }
    }

    suspend fun getHappenings(userId: UserId): List<Happening> {
        val aggregates = happeningEventsRepository.readUserEvents(userId)
            .groupBy { it.happeningId }
            .map { eventsToAggregate(it.key, it.value) }

        val userIds = aggregates.flatMap {
            listOf(it.adminId) + it.attendees + it.awaiting
        }.toSet()

        val users = userService.findUsersWithIds(userIds).groupBy { it.id }

        return aggregates.map { toHappening(it, users) }
    }

    suspend fun getHappening(happeningId: HappeningId): Happening? =
        getHappeningAggregate(happeningId)?.let { aggregate ->
            val users = userService.findUsersWithIds(
                listOf(aggregate.adminId) + aggregate.attendees + aggregate.awaiting
            ).groupBy { it.id }

            toHappening(aggregate, users)
        }

    private fun toHappening(
        aggregate: HappeningAggregate,
        users: Map<Long, List<User>>
    ): Happening {
        val attendees = aggregate.attendees.flatMap { users.getValue(it) }
        val awaiting = aggregate.awaiting.flatMap { users.getValue(it) }
        val admin = users.getValue(aggregate.adminId).first()

        return Happening(
            aggregate.happeningId,
            admin,
            aggregate.name,
            aggregate.description,
            aggregate.time,
            aggregate.location,
            attendees,
            awaiting,
            aggregate.cancelled,
            aggregate.cancelReason
        )
    }

    private suspend fun getHappeningAggregate(happeningId: HappeningId): HappeningAggregate? {
        val events = happeningEventsRepository.readEvents(happeningId)
        if(events.isEmpty()) {
            return null
        }
        return eventsToAggregate(happeningId, events)
    }

    private fun eventsToAggregate(
        happeningId: HappeningId,
        events: List<HappeningEvent>
    ): HappeningAggregate {
        val aggregate = MutableHappeningAggregate(happeningId, -1L, "", "", Instant.now())

        for (e in events) when (e) {
            is HappeningCreated -> {
                aggregate.adminId = e.userId
                aggregate.name = e.name
                aggregate.description = e.description
                aggregate.time = e.time
                aggregate.location = e.location
            }
            is HappeningNameChanged -> aggregate.name = e.name
            is HappeningDescriptionChanged -> aggregate.description = e.description
            is HappeningTimeChanged -> aggregate.time = e.time
            is HappeningLocationChanged -> aggregate.location = e.location
            is UserInvitedToHappening -> aggregate.awaiting.add(e.invited)
            is UserAcceptedHappeningInvite -> {
                aggregate.awaiting.remove(e.userId)
                aggregate.attendees.add(e.userId)
            }
            is UserRejectedHappeningInvite -> {
                aggregate.attendees.remove(e.userId)
                aggregate.awaiting.remove(e.userId)
            }
            is HappeningCancelled -> {
                aggregate.cancelled = true
                aggregate.cancelReason = e.reason
            }
        }

        return aggregate.toHappeningAggregate()
    }

    private suspend fun notifyUserOfInvite(happening: Happening, invited: UserId) {
        pushService.push(
            invited,
            "${happening.admin.nick} would like you to tag along on " +
                    "${happening.name} on ${happening.time.atZone(ZoneId.of("UTC")).dayOfWeek.getDisplayName(
                        TextStyle.FULL,
                        Locale.ENGLISH
                    )}"
        )
    }

    suspend fun updateHappening(
        userId: UserId,
        updateHappening: UpdateHappening
    ): Happening? {
        return getHappening(updateHappening.happeningId)?.let { originalHappening ->
            val updates = mutableListOf<HappeningEvent>()
            val updatedProperties = mutableListOf<String>()
            val now = Instant.now()
            updateHappening.name?.apply {
                updates.add(
                    HappeningNameChanged(
                        userId,
                        now,
                        updateHappening.happeningId,
                        name = this
                    )
                )
                updatedProperties.add("name")
            }
            updateHappening.description?.apply {
                updates.add(
                    HappeningDescriptionChanged(
                        userId,
                        now,
                        updateHappening.happeningId,
                        description = this
                    )
                )
                updatedProperties.add("description")
            }
            updateHappening.time?.apply {
                updates.add(
                    HappeningTimeChanged(
                        userId,
                        now,
                        updateHappening.happeningId,
                        time = this
                    )
                )
                updatedProperties.add("time")
            }
            updateHappening.location?.apply {
                updates.add(
                    HappeningLocationChanged(
                        userId,
                        now,
                        updateHappening.happeningId,
                        location = this
                    )
                )
                updatedProperties.add("location")
            }

            happeningEventsRepository.addEvents(updates)
            val happening = getHappening(updateHappening.happeningId)!!
            withContext(Dispatchers.IO) {
                val updateMsg = "${originalHappening.name} got " + when {
                    updates.size > 2 -> {
                        "updates for " + updatedProperties.subList(0, updatedProperties.lastIndex).joinToString(", ") + " and " + updatedProperties.last()
                    }
                    updates.size == 2 -> {
                        "updates for " + updatedProperties.joinToString(" and ")
                    }
                    else -> {
                        "updated " + updatedProperties.first()
                    }
                }

                for (user in happening.attendees) {
                    launch {
                        pushService.push(user.id, updateMsg)
                    }
                }
            }
            happening
        }
    }

    suspend fun cancelHappening(
        userId: UserId,
        cancelHappeningRequest: CancelHappening
    ) {
        val aggregate = getHappeningAggregate(cancelHappeningRequest.happeningId)
        if (aggregate?.adminId == userId) {
            val cancelHappening = HappeningCancelled(
                userId,
                Instant.now(),
                cancelHappeningRequest.happeningId,
                cancelHappeningRequest.reason ?: ""
            )
            happeningEventsRepository.addEvents(listOf(cancelHappening))
        }
    }

    suspend fun inviteUsers(
        userId: UserId,
        inviteUsersRequest: InviteUsersToHappening
    ): Happening? {
        return getHappening(inviteUsersRequest.happeningId)?.let {
            val inviteEvents =
                inviteUsersRequest.usersToInvite.map {
                    UserInvitedToHappening(
                        it,
                        Instant.now(),
                        inviteUsersRequest.happeningId,
                        it
                    )
                }
                    .toList()
            happeningEventsRepository.addEvents(inviteEvents)
            val happening = getHappening(inviteUsersRequest.happeningId)!!
            sendInvitePushToUsers(inviteUsersRequest.usersToInvite, happening)

            happening
        }
    }

    suspend fun acceptHappeningInvite(
        userId: UserId,
        acceptHappeningInviteRequest: AcceptHappeningInvite
    ): Happening? {
        return getHappening(acceptHappeningInviteRequest.happeningId)?.let {
            val accepted = UserAcceptedHappeningInvite(userId, Instant.now(), acceptHappeningInviteRequest.happeningId)
            happeningEventsRepository.addEvents(listOf(accepted))
            withContext(Dispatchers.IO) {
                val user = userService.findUserById(userId)!!
                pushService.push(it.admin.id, "${user.nick} is tagging along on ${it.name}")
            }

            getHappening(acceptHappeningInviteRequest.happeningId)!!
        }
    }

    suspend fun rejectHappeningInvite(
        userId: UserId,
        rejectHappeningInviteRequest: RejectHappeningInvite
    ): Happening? {
        return getHappening(rejectHappeningInviteRequest.happeningId)?.let {
            val rejected = UserRejectedHappeningInvite(userId, Instant.now(), rejectHappeningInviteRequest.happeningId)
            happeningEventsRepository.addEvents(listOf(rejected))
            withContext(Dispatchers.IO) {
                val user = userService.findUserById(userId)!!
                pushService.push(it.admin.id, "${user.nick} won't come to ${it.name}")
            }

            getHappening(rejectHappeningInviteRequest.happeningId)!!
        }
    }
}