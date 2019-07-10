package dev.fredag

import dev.fredag.cheerwithme.web.friendRouting
import io.ktor.http.*
import kotlin.test.*
import io.ktor.server.testing.*


class FriendModuleTest {
    @Test
    fun testFriendsList() {
        withTestApplication({ friendRouting(testing = true) }) {
            handleRequest(HttpMethod.Get, "/friends/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("You have no friends :,(", response.content)
            }
        }
    }
}
