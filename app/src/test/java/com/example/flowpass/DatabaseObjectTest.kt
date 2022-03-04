package com.example.flowpass

import android.content.Context
import com.example.flowpass.database.DatabaseObject
import com.example.flowpass.database.Silt
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner

/**
 * Checks that [DatabaseObject] behaves as expected; user interactions with the
 * encrypted database behave as expected
 * TODO: WHen possible get mock test to work, .gradle files are source of problem
 */
@RunWith(MockitoJUnitRunner::class)
class DatabaseObjectTest {

    @Test
    fun checkCreationWorks() {
        val mockContext = mock(Context::class, RETURNS_DEEP_STUBS)

        // Add a db entry and check the expected return value
        `when` (DatabaseObject(mockContext, null).addSilt(Silt(
            1,
            byteArrayOf(0x2E, 0x38),
            byteArrayOf(0x2D, 0x38),
            byteArrayOf(0x2C, 0x38),
            byteArrayOf(0x2B, 0x38),
            byteArrayOf(0x2A, 0x38)
        ))).thenReturn(null)

        // Get db entries checking the expected return value
       `when` (DatabaseObject(mockContext, null).getSilt(1)!!._id).thenReturn(1)
    }
}