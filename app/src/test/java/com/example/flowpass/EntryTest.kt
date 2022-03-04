package com.example.flowpass

import com.example.flowpass.database.Entry
import org.junit.Test
import kotlin.test.*

/**
 * Checks that [Entry] behaves as expected
 */
class EntryTest {

    @Test
    fun entryIsValid() {
        // given
        val testEntry = Entry(
            0, "Google", "play", "", "YouTube", "MarkovBuff$"
        )

        // then
        assertTrue(testEntry._id is Int)
        assertEquals(testEntry.password, "MarkovBuff$")
        assertTrue(testEntry.key2.isBlank())
    }
}