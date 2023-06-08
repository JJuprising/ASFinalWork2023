package com.example.asfinalwork2023.ui.dashboard

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class PassageDBHelper (val context: Context, name: String, version: Int) : SQLiteOpenHelper(context, name, null, version) {

    private val createPassageDB = "create table Passage (" +
            " id integer primary key autoincrement," +
            "title text," +
            "content text," +
            "picture BLOB)"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createPassageDB)
        Toast.makeText(context,"Finish", Toast.LENGTH_SHORT).show()
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {

    }

}