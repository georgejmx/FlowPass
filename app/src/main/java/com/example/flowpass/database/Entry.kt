package com.example.flowpass.database

/**
 * Data class that represents tuples from the database. This is used by the UI
 * for storing and displaying such data. Used by sqlite to represent the tuples
 */
data class Entry(
    val _id: Int?,
    val name: String,
    val key1: String,
    val key2: String,
    val key3: String,
    val password: String
)