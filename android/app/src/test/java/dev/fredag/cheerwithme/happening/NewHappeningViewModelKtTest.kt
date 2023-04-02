package dev.fredag.cheerwithme.happening

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

internal class NewHappeningViewModelKtTest {
    @Test
    fun `it gets the next friday at 18 from any day of the week`() {
        // First day of year 2022 was a saturday
        assertEquals(Instant.parse("2022-01-07T18:00:00Z"), nextFridayAtSix(LocalDateTime.of(2022, 1, 1, 12, 0 ,0)))
        assertEquals(Instant.parse("2022-01-07T18:00:00Z"), nextFridayAtSix(LocalDateTime.of(2022, 1, 2, 12, 0 ,0)))
        assertEquals(Instant.parse("2022-01-07T18:00:00Z"), nextFridayAtSix(LocalDateTime.of(2022, 1, 3, 12, 0 ,0)))
        assertEquals(Instant.parse("2022-01-07T18:00:00Z"), nextFridayAtSix(LocalDateTime.of(2022, 1, 4, 12, 0 ,0)))
        assertEquals(Instant.parse("2022-01-07T18:00:00Z"), nextFridayAtSix(LocalDateTime.of(2022, 1, 5, 12, 0 ,0)))
        assertEquals(Instant.parse("2022-01-07T18:00:00Z"), nextFridayAtSix(LocalDateTime.of(2022, 1, 6, 12, 0 ,0)))
        assertEquals(Instant.parse("2022-01-07T18:00:00Z"), nextFridayAtSix(LocalDateTime.of(2022, 1, 7, 12, 0 ,0)))

        assertEquals(Instant.parse("2022-01-07T18:00:00Z"), nextFridayAtSix(LocalDate.of(2022, 1, 7).atTime(17,59)))
        assertEquals(Instant.parse("2022-01-14T18:00:00Z"), nextFridayAtSix(LocalDate.of(2022, 1, 7).atTime(18,0)))

    }
}