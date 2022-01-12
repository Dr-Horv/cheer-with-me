package dev.fredag.cheerwithme

import dev.fredag.cheerwithme.happening.toMapByKey
import org.junit.jupiter.api.Test


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

}