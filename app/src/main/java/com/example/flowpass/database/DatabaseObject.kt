package com.example.flowpass.database

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.FileUtils
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
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
    private val appDownloadsPath = context.getExternalFilesDir(
        Environment.DIRECTORY_DOCUMENTS)!!.path + "/reservoir"

    // Initialise the encrypted db
    override fun onCreate(db: SQLiteDatabase) {
        Log.i("DatabaseObject", "created")
        val query = ("create table silt (" +
            "_id integer primary key autoincrement not null, field1 blob not null, " +
                "field5 blob not null, field2 blob, field3 blob, field4 blob)"
        )
        db.execSQL(query)
    }

    private fun copyFromAppDownloads(): Boolean {
        // Access new app database so it gets cached
        writableDatabase.close()
        this.close()
        val importAppDb = File(appDownloadsPath)
        val toStream = FileOutputStream(File(dbFilepath))

        // Copy the selected backup database into the app one, using FileUtils
        FileUtils.copy(FileInputStream(importAppDb), toStream)
        Log.i("DatabaseObject", "Import hash: ${toStream.hashCode()}")
        // Access new app database so it gets cached
        writableDatabase.close()
        return true
    }

    // Copies encrypted backup to app process database. First attempts to use the public
    // backup. If this has been deleted by the user or system, use hidden backup
    fun importDb(path: String): Boolean {
        try {
            // Preparing MediaStore query
            val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
            val projection = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME
            )
            val selection = "${MediaStore.Files.FileColumns.DISPLAY_NAME} == ?"
            val selectionArgs = arrayOf("reservoir")
            val sortOrder = "${MediaStore.Files.FileColumns.DISPLAY_NAME} ASC"

            // Executing query
            val resolver = context.contentResolver
            val query = resolver.query(
                collection, projection, selection, selectionArgs, sortOrder)
            lateinit var contentUri: Uri
            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                cursor.moveToFirst()
                val id = cursor.getLong(idColumn)
                contentUri = ContentUris.withAppendedId(MediaStore.Files.getContentUri(
                    "external"), id)
            }
            resolver.openInputStream(contentUri).use { stream ->
                val outStream = FileOutputStream(File(appDownloadsPath))
                if (stream != null) {
                    FileUtils.copy(stream, outStream)
                } else {
                    return false
                }
                stream.close()
                outStream.close()
            }
            copyFromAppDownloads()
        } catch (err: Exception) {
            err.printStackTrace()
            return false
        }
        return true
    }

    // Exports app process database to the app downloads directory. This allows
    // easy export to the MediaStore
    private fun copyDbToAppDownloads(): Boolean {
        return try {
            this.close()
            val existingAppDb = File(dbFilepath)
            val exportAppDb = File(appDownloadsPath)
            val fromStream = FileInputStream(existingAppDb)
            val toStream = FileOutputStream(exportAppDb)
            FileUtils.copy(fromStream, toStream)
            fromStream.close()
            toStream.close()
            true
        } catch (err: Exception) {
            Log.i("DatabaseObject", "error copying db to app downloads")
            err.printStackTrace()
            false
        }
    }

    // Exports app process database to the media store
    fun exportDb(): Boolean {
        // Initialising contents with correct config
        if (!copyDbToAppDownloads()) { return false }
        val inStream = FileInputStream(File(appDownloadsPath))
        val contents = ContentValues()
        contents.put(MediaStore.MediaColumns.DISPLAY_NAME, "reservoir")
        contents.put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream")
        contents.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
        contents.put(MediaStore.Video.Media.TITLE, "reservoirdb")
        contents.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis())
        contents.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis())
        val resolver = context.contentResolver

        // Perform export to the media store
        return try {
            val contentUri = MediaStore.Files.getContentUri("external")
            val uri = resolver.insert(contentUri, contents)!!
            val pfd: ParcelFileDescriptor = resolver.openFileDescriptor(uri, "w")!!
            val outStream = FileOutputStream(pfd.fileDescriptor)
            FileUtils.copy(inStream, outStream)
            outStream.close()
            inStream.close()
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