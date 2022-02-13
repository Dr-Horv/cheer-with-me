package dev.fredag.cheerwithme

import dev.fredag.cheerwithme.happening.durationToEventText
import dev.fredag.cheerwithme.happening.sensibleDurationString
import dev.fredag.cheerwithme.happening.toMapByKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant


internal class UtilsKtTest {

    @Test
    fun `when list has entries which derives the same key the last one is kept`() {
        val keptEntry1 = Pair('a', 2)
        val keptEntry2 = Pair('b', 2)
        val list = listOf(
            Pair('a', 1),
            Pair('b', 1),
            keptEntry1,
            keptEntry2,
        )
        val map = list.toMapByKey { it.first }
        assert(listOf(keptEntry1, keptEntry2) == map.values.toList())
    }

    @Test
    fun `sensibleDurationString works for durations of hours, minutes and seconds`() {
        assertEquals("1 year 1 day", sensibleDurationString(Duration.ofDays(366)))
        assertEquals("2 years 1 day", sensibleDurationString(Duration.ofDays(365*2 +1)))
        assertEquals("2 years 2 days", sensibleDurationString(Duration.ofDays(365*2 +2)))
        assertEquals("364 days", sensibleDurationString(Duration.ofDays(364)))
        assertEquals("1 day", sensibleDurationString(Duration.ofHours(25)))
        assertEquals("1 day", sensibleDurationString(Duration.ofHours(24)))
        assertEquals("23 hours", sensibleDurationString(Duration.ofHours(23)))
        assertEquals("23 hours 10 minutes", sensibleDurationString(Duration.ofHours(23).plusMinutes(10)))
        assertEquals("23 hours 1 minute", sensibleDurationString(Duration.ofHours(23).plusMinutes(1)))
        assertEquals("1 hour", sensibleDurationString(Duration.ofHours(1)))
        assertEquals("59 minutes", sensibleDurationString(Duration.ofMinutes(59)))
        assertEquals("now", sensibleDurationString(Duration.ofSeconds(10)))
        val now = Instant.now()
        var future = now.plusSeconds(60)
        assertEquals("starting in 1 minute", durationToEventText(now, future))
        assertEquals("started 1 minute ago", durationToEventText(future, now))
        future = now.plusSeconds(59)
        assertEquals("starting now", durationToEventText(now, future))
        assertEquals("starting now", durationToEventText(future, now))
    }
}