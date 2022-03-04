package com.example.flowpass.database

/**
 * Data class that represents binary data that is stored in the database. This data
 * needs to be decrypted by the reservoir to be displayed on the UI
 */
data class Silt(
    val _id: Int?,
    val field1: ByteArray,
    val field2: ByteArray,
    val field3: ByteArray,
    val field4: ByteArray,
    val field5: ByteArray
) {
    // Boilerplate code that ensures binary data behaves correctly if 'equals' called on it
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Silt
        if (!field1.contentEquals(other.field1)) return false
        if (!field2.contentEquals(other.field2)) return false
        if (!field3.contentEquals(other.field3)) return false
        if (!field4.contentEquals(other.field4)) return false
        if (!field5.contentEquals(other.field5)) return false
        return true
    }

    // Boilerplate code that ensures binary data behaves correctly if 'hashCode' called on it
    override fun hashCode(): Int {
        var result = field1.contentHashCode()
        result = 31 * result + field2.contentHashCode()
        result = 31 * result + field3.contentHashCode()
        result = 31 * result + field4.contentHashCode()
        result = 31 * result + field5.contentHashCode()
        return result
    }
}