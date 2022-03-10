package com.example.flowpass.database

import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.flowpass.MainActivity
import com.example.flowpass.ReservoirClosedException
import java.lang.Exception
import java.util.*

/**
 * Class that defines how the app interacts with the sqlite database, for parsing raw UI data.
 * Handles all encryption and decryption of password data, then calling a [DatabaseObject]
 * directly for persistence
 */
class DatabaseFilter(private val activity: FragmentActivity) {

    private val dbObject = DatabaseObject(activity.applicationContext, null)

    // Validates that an incoming entry is not too long for the database
    private fun validateEntry(entry: Entry): Boolean {
        return entry.name.length < 20 && entry.password.length < 20 && entry.key1.length < 15 &&
                entry.key2.length < 15 && entry.key3.length < 15
    }

    // Allows fragments to import a backup database into the app
    fun importDb(name: String): Boolean { return dbObject.importDb(name) }

    // If already validated, exports a copy of the encrypted app database to backup location
    fun exportDb(): Boolean { return dbObject.exportDb() }

    // Encrypt and send a new entry to the database
    fun addEntry(entry: Entry) {
        try {
            if (!validateEntry(entry)) { throw InvalidPropertiesFormatException("Field too small") }
            val output = (activity as MainActivity).rvr.buryData(entry)
            dbObject.addSilt(output)
            Log.i("DatabaseFilter", "entry encrypted")
        } catch (err: ReservoirClosedException) {
            Log.i("DatabaseFilter", "reservoir closed; data cannot be accessed")
        }
    }

    // Decrypt and receive a new entry from db
    fun getEntry(id: Int): Entry {
        return try {
            val silt = dbObject.getSilt(id)!!
            (activity as MainActivity).rvr.releaseData(silt)
        } catch (err: Exception) {
            Log.i("DatabaseFilter", "error finding silts")
            err.printStackTrace()
            Entry(-1, "placeholder", "", "", "", "pass")
        }
    }

    // Decrypt and receive all entries from db
    fun getEntries(): List<Entry> {
        try {
            val silts: List<Silt> = dbObject.getAllSilt()
            if (silts.isEmpty()) { return emptyList() }
            return silts.map { silt -> (activity as MainActivity).rvr.releaseData(silt) }
        } catch (err: Exception) {
            Log.i("DatabaseFilter", "error finding silts")
            err.printStackTrace()
            return emptyList()
        }
    }

    // Encrypt and update an entry in the db
    fun editEntry(entry: Entry) {
        try {
            if (!validateEntry(entry)) { throw InvalidPropertiesFormatException("Field too small") }
            val output = (activity as MainActivity).rvr.buryData(entry)
            dbObject.editSilt(output)
        } catch (err: ReservoirClosedException) {
            Log.i("DatabaseFilter", "reservoir closed; data cannot be accessed")
        }
    }

    // Delete a db entry
    fun deleteEntry(id: Int) { dbObject.deleteSilt(id) }

    // Delete all db entries
    fun deleteAllEntries() { dbObject.deleteAllSilt() }
}