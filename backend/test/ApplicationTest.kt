package dev.fredag

import dev.fredag.cheerwithme.module
import io.ktor.config.MapApplicationConfig
import io.ktor.http.*
import org.junit.Test

import kotlin.test.*
import io.ktor.server.testing.*

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({
            (environment.config as MapApplicationConfig).apply {
                put("push.pushArnIOS", "NONE")
                put("push.pushArnAndroid", "NONE")
            }
            module(testing = true)
        }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Cheers mate! \uD83C\uDF7B", response.content)
            }
        }
    }


}
