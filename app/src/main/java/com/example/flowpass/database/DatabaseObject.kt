package com.example.flowpass.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Environment
import android.os.FileUtils
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception

/**
 * Class that directly manipulates the encrypted sqlite db. Performs CRUD operations on
 * binary data
 */
class DatabaseObject(val context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "reservoir.db", factory, 1){

    // Where the sqlite database file is stored for the running app
    private val dbFilepath = context.applicationInfo.dataDir + "/databases/reservoir.db"

    // Locations where backup file is created. One available to user and one in somewhat
    // hidden app downloads directory
    private val publicBackupPath = "/storage/emulated/0/Download/reservoir"
    private val hiddenBackupPath = context.getExternalFilesDir(
        Environment.DIRECTORY_DOWNLOADS)!!.path + "/reservoir.db"

    // Initialise the encrypted db
    override fun onCreate(db: SQLiteDatabase) {
        Log.i("DatabaseObject", "created")
        val query = ("create table silt (" +
            "_id integer primary key autoincrement not null, field1 blob not null, " +
                "field5 blob not null, field2 blob, field3 blob, field4 blob)"
        )
        db.execSQL(query)
    }

    // Copies encrypted backup to app process database. First attempts to use the public
    // backup. If this has been deleted by the user or system, use hidden backup
    fun importDb(path: String): Boolean {
        // Access new app database so it gets cached
        writableDatabase.close()
        this.close()
        val importAppDb = if (File(path).exists()) {
            File(path)
        } else if (File(hiddenBackupPath).exists()) {
            File(hiddenBackupPath)
        } else return false
        val existingAppDb = File(dbFilepath)

        // Copy the selected backup database into the app one, using FileUtils
        val toStream = FileOutputStream(existingAppDb)
        FileUtils.copy(FileInputStream(importAppDb), toStream)
        Log.i("DatabaseObject", "Import hash: ${toStream.hashCode()}")
        // Access new app database so it gets cached
        writableDatabase.close()
        return true
    }

    // Exports app process database to backup locations
    fun exportDbSlim(): Boolean {
        return try {
            this.close()
            val existingAppDb = File(dbFilepath)
            val exportAppDb = File(hiddenBackupPath)
            val fromStream = FileInputStream(existingAppDb)
            val toStream = FileOutputStream(exportAppDb)
            FileUtils.copy(fromStream, toStream)
            fromStream.close()
            Log.i("DatabaseObject", "Export hash 1: ${toStream.hashCode()}")
            toStream.flush()
            toStream.close()
            true
        } catch (err: Exception) {
            err.printStackTrace()
            false
        }
    }

    // Exports app process database to backup locations
    fun exportDb(): Boolean {
        return try {
            this.close()
            val existingAppDb = File(dbFilepath)
            val exportAppDbs = arrayOf(File(hiddenBackupPath), File(publicBackupPath))
            val fromStream = FileInputStream(existingAppDb)
            val toStreams = arrayOf(
                FileOutputStream(exportAppDbs[0]), FileOutputStream(exportAppDbs[1]))
            FileUtils.copy(fromStream, toStreams[0])
            FileUtils.copy(fromStream, toStreams[1])
            fromStream.close()
            Log.i("DatabaseObject", "Export hash 1: ${toStreams[0].hashCode()}")
            Log.i("DatabaseObject", "Export hash 2: ${toStreams[1].hashCode()}")
            toStreams[0].flush()
            toStreams[1].flush()
            toStreams[0].close()
            toStreams[1].close()
            true
        } catch (err: Exception) {
            err.printStackTrace()
            false
        }
    }

    // Upgrade the db
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table if exists silt")
        onCreate(db)
    }

    // Add new binary data to the db
    fun addSilt(silt: Silt) {
        val params = ContentValues()
        params.put("field1", silt.field1)
        params.put("field2", silt.field2)
        params.put("field3", silt.field3)
        params.put("field4", silt.field4)
        params.put("field5", silt.field5)
        val db = this.writableDatabase
        db.insert("silt", null, params)
        Log.i("DatabaseObject", "silt added")
        db.close()
    }

    // Get a tuple from the encrypted db
    @SuppressLint("Range")
    fun getSilt(siltId: Int): Silt? {
        val db = this.readableDatabase
        val cur: Cursor = db.rawQuery(
            "select * from silt where _id = ?",
            arrayOf(siltId.toString())
        )

        // Return silt if able to move through the db
        val output: Silt? = if (!cur.moveToFirst() || cur.getColumnIndex("_id") < 0) {
            null
        } else {
            Silt(
                cur.getString(cur.getColumnIndex("_id")).toInt(),
                cur.getBlob(cur.getColumnIndex("field1")),
                cur.getBlob(cur.getColumnIndex("field2")),
                cur.getBlob(cur.getColumnIndex("field3")),
                cur.getBlob(cur.getColumnIndex("field4")),
                cur.getBlob(cur.getColumnIndex("field5"))
            )
        }

        Log.i("DatabaseObject", "silt grabbed")
        cur.close()
        db.close()
        return output
    }

    // Get all tuples from the encrypted db
    @SuppressLint("Range")
    fun getAllSilt(): List<Silt> {
        val lst: MutableList<Silt> = mutableListOf()
        val db = this.readableDatabase
        val cur: Cursor = db.rawQuery("select * from silt", null)
        if (!cur.moveToFirst()) return emptyList()

        // Pass the cursor through all tuples of the database
        do {
            if (cur.getColumnIndex("_id") >= 0) {
                lst.add(Silt(
                    cur.getString(cur.getColumnIndex("_id")).toInt(),
                    cur.getBlob(cur.getColumnIndex("field1")),
                    cur.getBlob(cur.getColumnIndex("field2")),
                    cur.getBlob(cur.getColumnIndex("field3")),
                    cur.getBlob(cur.getColumnIndex("field4")),
                    cur.getBlob(cur.getColumnIndex("field5"))
                ))
            }
        } while (cur.moveToNext())

        Log.i("DatabaseObject", "all silt grabbed")
        cur.close()
        db.close()
        Log.i("DatabaseObject", lst.toString())
        return lst
    }

    // Edit a specified tuple in db, from encrypted parameters
    fun editSilt(silt: Silt) {
        val params = ContentValues()
        params.put("_id", silt._id)
        params.put("field1", silt.field1)
        params.put("field2", silt.field2)
        params.put("field3", silt.field3)
        params.put("field4", silt.field4)
        params.put("field5", silt.field5)
        val db = this.writableDatabase
        db.update("silt", params, "_id = ?", arrayOf(silt._id.toString()))
        Log.i("DatabaseObject", "silt updated")
        db.close()
    }

    // Remove silt from the database
    fun deleteSilt(siltId: Int) {
        val query = "delete from silt where _id = $siltId"
        val db = this.writableDatabase
        db.execSQL(query)
        Log.i("DatabaseObject", "silt removed")
        db.close()
    }

    // Delete all silt from db; clearing it
    fun deleteAllSilt() {
        val db = this.writableDatabase
        db.execSQL("delete from silt")
        Log.i("DatabaseObject", "all silt removed")
        db.close()
    }

}