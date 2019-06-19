package dev.fredag

import io.ktor.http.*
import kotlin.test.*
import io.ktor.server.testing.*


class FriendModuleTest {
    @Test
    fun testFriendsList() {
        withTestApplication({ friendModule(testing = true) }) {
            handleRequest(HttpMethod.Get, "/friends/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("You have no friends :,(", response.content)
            }
        }
    }
}
