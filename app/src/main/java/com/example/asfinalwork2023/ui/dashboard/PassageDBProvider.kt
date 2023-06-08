package com.example.asfinalwork2023.ui.dashboard

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.databasetest.later

class PassageDBProvider : ContentProvider() {

    private val authority = "com.example.asfinalwork2023.provider"
    private var dbHelper: PassageDBHelper? = null
    private val passageDir = 0
    private val passageItem = 1

    private val uriMatcher by later {
        val matcher = UriMatcher(UriMatcher.NO_MATCH)
        matcher.addURI(authority, "passage", passageDir)
        matcher.addURI(authority, "passage/#", passageItem)
        matcher
    }

    override fun onCreate() = context?.let {
        dbHelper = PassageDBHelper(it, "Passage.db", 1)
        true
    } ?: false

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ) = dbHelper?.let {
        // 查询数据
        val db = it.readableDatabase
        val cursor = when (uriMatcher.match(uri)) {
            passageDir -> db.query(
                "Passage",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
            )
            passageItem -> {
                val passageId = uri.pathSegments[1]
                db.query("Passage", projection, "id = ?", arrayOf(passageId), null, null, sortOrder)
            }
            else -> null
        }
        cursor
    }

    override fun getType(uri: Uri) = when (uriMatcher.match(uri)) {
        passageDir -> "vnd.android.cursor.dir/vnd.com.example.asfinalwork2023.provider.passage"
        passageItem -> "vnd.android.cursor.item/vnd.com.example.asfinalwork2023.provider.passage"
        else -> null
    }

    override fun insert(uri: Uri, values: ContentValues?) = dbHelper?.let {
        // 添加数据
        val db = it.writableDatabase
        val uriReturn = when (uriMatcher.match(uri)) {
            passageDir, passageItem -> {
                val newPassageId = db.insert("Passage", null, values)
                Uri.parse("content://$authority/passage/$newPassageId")
            }
            else -> null
        }
        uriReturn
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?) =
        dbHelper?.let {
            // 删除数据
            val db = it.writableDatabase
            val deletedRows = when (uriMatcher.match(uri)) {
                passageDir -> db.delete("Passage", selection, selectionArgs)
                passageItem -> {
                    val passageId = uri.pathSegments[1]
                    db.delete("Passage", "id = ?", arrayOf(passageId))
                }
                else -> 0
            }
            deletedRows
        } ?: 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ) =
        dbHelper?.let {
            // 更新数据
            val db = it.writableDatabase
            val updatedRows = when (uriMatcher.match(uri)) {
                passageDir -> db.update("Passage", values, selection, selectionArgs)
                passageItem -> {
                    val passageId = uri.pathSegments[1]
                    db.update("Passage", values, "id = ?", arrayOf(passageId))
                }
                else -> 0
            }
            updatedRows
        } ?: 0
}