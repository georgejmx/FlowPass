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

    // Orders entries alphabetically by name
    private fun orderEntries(unordered: List<Entry>): List<Entry> {
        return unordered.sortedBy { it.name }
    }

    @Test
    fun entrySortsCorrectly() {
        val testEntry1 = Entry(
            0, "Google", "play", "", "YouTube", "MarkovBuff$")
        val testEntry2 = Entry(
            0, "Foogle", "play", "", "gpay", "MarkovBuffy$")
        val testEntries = listOf<Entry>(testEntry1, testEntry2)
        val reverseTestEntries = testEntries.reversed()

        assertNotEquals(testEntries, reverseTestEntries)
        assertEquals(orderEntries(testEntries), reverseTestEntries)
    }
}