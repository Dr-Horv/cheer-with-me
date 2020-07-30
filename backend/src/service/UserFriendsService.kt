package dev.fredag.cheerwithme.service

import com.fasterxml.jackson.module.kotlin.readValue
import dev.fredag.cheerwithme.logger
import dev.fredag.cheerwithme.model.*
import dev.fredag.cheerwithme.objectMapper
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.select
import org.joda.time.DateTime

class UserFriendsService(
    private val userService: UserService,
    private val pushService: PushService
) {
    private val log by logger()

    private suspend fun addEvents(events: List<Event>) = Database.dbQuery {
        UserFriendsEvents.batchInsert(events) {
            this[UserFriendsEvents.userId] = it.userId
            this[UserFriendsEvents.timestamp] = DateTime(it.timestamp.toEpochMilli())
            this[UserFriendsEvents.eventData] = objectMapper.writeValueAsString(it)
        }
    }

    private suspend fun readEvents(userId: UserId): List<Event> = Database.dbQuery {
        UserFriendsEvents.select { UserFriendsEvents.userId.eq(userId) }
            .orderBy(UserFriendsEvents.timestamp to SortOrder.ASC)
            .map { row: ResultRow ->
                log.debug(row[UserFriendsEvents.timestamp].toString())
                objectMapper.readValue<Event>(row[UserFriendsEvents.eventData])
            }
    }

    suspend fun sendFriendRequest(userId: UserId, request: SendFriendRequest) {
        val aggregate = getUserFriendAggregate(userId)
        if (aggregate.friends.contains(request.userId) || aggregate.outgoingFriendRequests.contains(request.userId)) {
            log.debug("$userId is already friends with or have outgoing request towards ${request.userId}")
            return
        }

        addEvents(
            listOf(
                FriendRequest(userId, now(), requester = userId, receiver = request.userId),
                FriendRequest(request.userId, now(), requester = userId, receiver = request.userId)
            )
        )

        userService.findUserById(userId)?.apply {
            pushService.push(request.userId, "${this.nick} has requested to be your friend")
        }
    }

    suspend fun acceptFriendRequest(userId: UserId, request: AcceptFriendRequest) {
        val aggregate = getUserFriendAggregate(userId)
        if (aggregate.friends.contains(request.userId) || !aggregate.incomingFriendRequests.contains(request.userId)) {
            log.debug("$userId is already friends with or doesnt have an incoming friend request from ${request.userId}")
            return
        }
        addEvents(
            listOf(
                FriendRequestAccepted(userId, now(), requester = request.userId, receiver = userId),
                FriendRequestAccepted(request.userId, now(), requester = request.userId, receiver = userId)
            )
        )

        userService.findUserById(userId)?.apply {
            pushService.push(request.userId, "${this.nick} has accepted your friend request")
        }

    }



    private suspend fun getUserFriendAggregate(userId: UserId): UserFriendsAggregate {
        val userEvents = readEvents(userId)
        val myFriends = mutableListOf<UserId>()
        val myRequests = mutableListOf<UserId>()
        val myIncomingRequests = mutableListOf<UserId>()
        for (e in userEvents) {
            when (e) {
                is FriendRequest -> handleFriendRequest(e, userId, myIncomingRequests, myRequests, myFriends)
                is FriendRequestAccepted -> handleFriendRequestAccepted(
                    e,
                    userId,
                    myFriends,
                    myRequests,
                    myIncomingRequests
                )
            }
        }

        return UserFriendsAggregate(myFriends, myIncomingRequests, myRequests)
    }

    suspend fun getUserFriends(userId: UserId): UserFriends {
        val aggregate = getUserFriendAggregate(userId)
        val users = userService.findUsersWithIds(
            aggregate.friends + aggregate.incomingFriendRequests + aggregate.outgoingFriendRequests
        ).groupBy { it.id }

        return UserFriends(
            aggregate.friends.map { users.getValue(it).first() },
            aggregate.incomingFriendRequests.map { users.getValue(it).first() },
            aggregate.outgoingFriendRequests.map { users.getValue(it).first() })
    }

    private fun handleFriendRequestAccepted(
        e: FriendRequestAccepted,
        userId: UserId,
        myFriends: MutableList<UserId>,
        myRequests: MutableList<UserId>,
        myIncomingRequests: MutableList<UserId>
    ) {
        val friendId = if (e.requester == userId) {
            e.receiver
        } else {
            e.requester
        }
        becomeFriends(friendId, myFriends, myRequests, myIncomingRequests)
    }

    private fun handleFriendRequest(
        e: FriendRequest,
        userId: UserId,
        myIncomingRequests: MutableList<UserId>,
        myRequests: MutableList<UserId>,
        myFriends: MutableList<UserId>
    ) {
        val friendId = if (e.requester == userId && myIncomingRequests.contains(e.receiver)) {
            e.receiver
        } else if (myRequests.contains(e.requester)) {
            e.requester
        } else {
            null
        }

        when {
            friendId != null -> {
                becomeFriends(
                    friendId,
                    myFriends,
                    myRequests,
                    myIncomingRequests
                )
            }
            e.requester == userId -> {
                myRequests += e.receiver
            }
            else -> {
                myIncomingRequests += e.requester
            }
        }
    }

    private fun becomeFriends(
        friendUserId: UserId,
        myFriends: MutableList<UserId>,
        myRequests: MutableList<UserId>,
        myIncomingRequests: MutableList<UserId>
    ) {
        myFriends += friendUserId
        myRequests.remove(friendUserId)
        myIncomingRequests.remove(friendUserId)
    }
}