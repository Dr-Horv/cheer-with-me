package service

import dev.fredag.cheerwithme.model.*
import dev.fredag.cheerwithme.repository.UserFriendsEventsRepository
import dev.fredag.cheerwithme.service.PushService
import dev.fredag.cheerwithme.service.UserFriendsService
import dev.fredag.cheerwithme.service.UserService
import dev.fredag.cheerwithme.service.now
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

data class FriendRequestMatcher(
    val expected: List<UserFriendsEvent>,
    val refEq: Boolean
): Matcher<List<UserFriendsEvent>> {
    override fun match(arg: List<UserFriendsEvent>?): Boolean {
        if(arg === null) {
            return false
        }

        return expected.all { expected -> arg.any {
            it.userId == expected.userId &&
                    equalEvents(expected, it)
        } }
    }

    private fun equalEvents(e1: UserFriendsEvent, e2: UserFriendsEvent): Boolean =
        if (e1 is FriendRequestAccepted && e2 is FriendRequestAccepted) {
           e1.requester == e2.requester && e1.receiver == e2.receiver
        } else if (e1 is FriendRequest && e2 is FriendRequest) {
            e1.requester == e2.requester && e1.receiver == e2.receiver
        } else {
            false
        }

}

inline fun <reified T : List<UserFriendsEvent>> MockKMatcherScope.matchFriendRequests(
    vararg items: UserFriendsEvent,
    refEq: Boolean = true
): T = match(FriendRequestMatcher(listOf(*items), refEq))

val userFriendsEventRepositoryMock = mutableMapOf<UserId, List<UserFriendsEvent>>().withDefault { mutableListOf() }

internal class UserFriendsServiceTest {

    private val userService = mockk<UserService>();
    private val userFriendsEventsRepository = mockk<UserFriendsEventsRepository>();
    private val pushService = mockk<PushService>();
    private val userFriendsService = UserFriendsService(userService, userFriendsEventsRepository, pushService)


    @BeforeEach
    fun setUp() {
        coEvery { userFriendsEventsRepository.readEvents(any<UserId>()) } answers {
            userFriendsEventRepositoryMock.getValue(firstArg())
        }
        coEvery { userFriendsEventsRepository.addEvents(any<List<FriendRequest>>()) } answers {
            val events = firstArg<List<UserFriendsEvent>>()
            events.forEach { userFriendsEventRepositoryMock[it.userId] = userFriendsEventRepositoryMock.getValue(it.userId) + it }
            events.size
        }
        coEvery { userService.findUserById(any()) } answers {
            val id = firstArg<Long>()
            User(id, "TestUser-$id")
        }
        coEvery { userService.findUsersWithIds(any<List<UserId>>()) } answers {
            val ids = firstArg<List<UserId>>()
            ids.map { User(it, "TestUser-$it") }
        }
        coEvery { pushService.push(any<Long>(), any()) } returns Unit
    }

    @AfterEach
    fun tearDown() {
        userFriendsEventRepositoryMock.clear()
        clearAllMocks()
    }

    @Test
    fun `friend request should create two events`() = runBlocking {
        val user = 1L
        val futureFriend = 2L
        userFriendsService.sendFriendRequest(user, SendFriendRequest(futureFriend))
        assertEquals(futureFriend, userFriendsService.getUserFriends(user).outgoingFriendRequests.first().id)
        assertEquals(user, userFriendsService.getUserFriends(futureFriend).incomingFriendRequests.first().id)

        coVerify {
            userFriendsEventsRepository.addEvents(
                matchFriendRequests(
                    FriendRequest(user, now(), requester = user, receiver = futureFriend),
                    FriendRequest(futureFriend, now(), requester = user, receiver = futureFriend))
                )
        }

    }

    @Test
    fun `sending and accepting friend request should create friendship`() = runBlocking {
        val user = 1L
        val futureFriend = 2L
        userFriendsService.sendFriendRequest(user, SendFriendRequest(futureFriend))
        userFriendsService.acceptFriendRequest(futureFriend, AcceptFriendRequest(user))
        assertEquals(futureFriend, userFriendsService.getUserFriends(user).friends.first().id)
        assertEquals(user, userFriendsService.getUserFriends(futureFriend).friends.first().id)

        coVerify {
            userFriendsEventsRepository.addEvents(
                matchFriendRequests(
                    FriendRequest(user, now(), requester = user, receiver = futureFriend),
                    FriendRequest(futureFriend, now(), requester = user, receiver = futureFriend))
            )
            userFriendsEventsRepository.addEvents(
                matchFriendRequests(
                    FriendRequestAccepted(futureFriend, now(), requester = user, receiver = futureFriend),
                    FriendRequestAccepted(user, now(), requester = user, receiver = futureFriend)
                )
            )
        }

    }

    @Test
    fun `sending multiple friend requests only creates one event`() = runBlocking {
        val user = 1L
        val futureFriend = 2L
        userFriendsService.sendFriendRequest(user, SendFriendRequest(futureFriend))
        userFriendsService.sendFriendRequest(user, SendFriendRequest(futureFriend))

        coVerify {
            userFriendsEventsRepository.addEvents(
                matchFriendRequests(
                    FriendRequest(user, now(), requester = user, receiver = futureFriend),
                    FriendRequest(futureFriend, now(), requester = user, receiver = futureFriend))
            )
        }
        coVerify { userFriendsEventsRepository.readEvents(any()) }
        confirmVerified(userFriendsEventsRepository)
    }


    @Test
    fun `sending friend request to an existing friends doesn't create any event`() = runBlocking {
        val user = 1L
        val friend = 2L
        userFriendsEventRepositoryMock[user] = listOf(
            FriendRequest(user, now(), requester = user, receiver = friend),
            FriendRequestAccepted(user, now(), requester = user, receiver = friend)
        )

        userFriendsService.sendFriendRequest(user, SendFriendRequest(friend))
        coVerify(exactly = 0) { userFriendsEventsRepository.addEvents(any()) }
        coVerify { userFriendsEventsRepository.readEvents(any()) }
        confirmVerified(userFriendsEventsRepository)
    }


    @Test
    fun `sending friend request to someone requesting to be your friend creates friendship`() = runBlocking {
        val user = 1L
        val futureFriend = 2L
        userFriendsService.sendFriendRequest(user, SendFriendRequest(futureFriend))
        userFriendsService.sendFriendRequest(futureFriend, SendFriendRequest(user))

        assertEquals(futureFriend, userFriendsService.getUserFriends(user).friends.first().id)
        assertEquals(user, userFriendsService.getUserFriends(futureFriend).friends.first().id)
    }

    @Test
    fun `accepting friend request without any incoming requests doesn't create any event`() = runBlocking {
        val user = 1L
        val randomNonRequestingOtherUser = 2L

        userFriendsService.acceptFriendRequest(randomNonRequestingOtherUser, AcceptFriendRequest(user))
        coVerify(exactly = 0) { userFriendsEventsRepository.addEvents(any()) }
        coVerify { userFriendsEventsRepository.readEvents(any()) }
        confirmVerified(userFriendsEventsRepository)
    }

    @Test
    fun `accepting friend request on an existing friends doesn't create any event`() = runBlocking {
        val user = 1L
        val friend = 2L
        userFriendsEventRepositoryMock[user] = listOf(
            FriendRequest(user, now(), requester = user, receiver = friend),
            FriendRequestAccepted(user, now(), requester = user, receiver = friend),
        )
        userFriendsEventRepositoryMock[friend] = listOf(
            FriendRequest(friend, now(), requester = user, receiver = friend),
            FriendRequestAccepted(friend, now(), requester = user, receiver = friend),
        )

        userFriendsService.acceptFriendRequest(friend, AcceptFriendRequest(user))
        coVerify(exactly = 0) { userFriendsEventsRepository.addEvents(any()) }
        coVerify { userFriendsEventsRepository.readEvents(any()) }
        confirmVerified(userFriendsEventsRepository)
    }

    @Test
    fun getUserFriends() = runBlocking {
        val user = 1L
        val friend = 2L
        val friendRequester = 3L
        val futureFriend = 4L
        userFriendsEventRepositoryMock[user] = listOf(
            FriendRequest(user, now(), requester = user, receiver = friend),
            FriendRequestAccepted(user, now(), requester = user, receiver = friend),
            FriendRequest(user, now(), requester = friendRequester, receiver = user),
            FriendRequest(user, now(), requester = user, receiver = futureFriend)
        )

        val userFriends = userFriendsService.getUserFriends(user)

        assertEquals(1, userFriends.friends.size)
        assertEquals(friend, userFriends.friends.first().id)

        assertEquals(1, userFriends.outgoingFriendRequests.size)
        assertEquals(futureFriend, userFriends.outgoingFriendRequests.first().id)

        assertEquals(1, userFriends.incomingFriendRequests.size)
        assertEquals(friendRequester, userFriends.incomingFriendRequests.first().id)
    }
}