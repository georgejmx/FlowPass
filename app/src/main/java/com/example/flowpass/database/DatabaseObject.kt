package com.example.flowpass.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * Class that directly manipulates the encrypted sqlite db. Performs CRUD operations on
 * binary data
 */
class DatabaseObject(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "reservoir_db", factory, 1){

    // Initialise the encrypted db
    override fun onCreate(db: SQLiteDatabase) {
        Log.i("DatabaseObject", "created")
        val query = ("create table silt (" +
            "_id integer primary key autoincrement not null, field1 blob not null, " +
                "field5 blob not null, field2 blob, field3 blob, field4 blob)"
        )
        db.execSQL(query)
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