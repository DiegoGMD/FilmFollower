package com.diegogmd.filmfollower.ui.pages

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.diegogmd.filmfollower.data.local.FilmFillowerDatabase

// ---------- ContentList ----------
class ContentList(
    val listId: Int,
    val name: String
) {
    fun insertNewContentList(context: Context): Boolean {
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase
        var ok = false

        try {
            val contentValues = ContentValues().apply {
                put("list_id", listId)
                put("name", name)
            }
            db.beginTransaction()
            try {
                val rowId = db.insert("List", null, contentValues)
                if (rowId == -1L) throw IllegalStateException("List insert returned -1")
                db.setTransactionSuccessful()
                ok = true
            } finally {
                db.endTransaction()
            }
        } catch (e: Exception) {
            Log.e("Database", "Error inserting new list", e)
        } finally {
            db.close()
        }
        return ok
    }
}

fun getContentList(context: Context, listId: Int): ContentList? {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    var theList: ContentList? = null

    val query = """
        SELECT * FROM List
        WHERE list_id = ?
    """.trimIndent()
    val selectionArgs = arrayOf(listId.toString())

    try {
        val cursor = db.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {
            theList = ContentList(
                listId = cursor.getInt(cursor.getColumnIndexOrThrow("list_id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            )
        }
        cursor.close()
        Log.d("Database", "Successful Mission: Getting list info")
    } catch (e: Exception) {
        Log.e("Database", "Error getting list", e)
    } finally {
        db.close()
    }

    return theList
}

fun eraseContentList(context: Context, listId: Int): Int? {
    val selectedList: ContentList? = getContentList(context, listId)
    if (selectedList == null) {
        Log.e("Database", "Error getting list")
        return null
    } else {
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase

        try {
            db.execSQL(
                "DELETE FROM List WHERE list_id = ?", arrayOf(listId.toString())
            )
            Log.d("Database", "List with ID $listId deleted successfully")
        } catch (e: Exception) {
            Log.e("Database", "Error deleting list", e)
            return 0
        } finally {
            db.close()
        }
        return 1
    }
}

// ---------- ListItem ----------
class ContentListItem(
    val listId: Int,
    val filmId: Int,
    val showId: Int,
    val position: Int
) {
    fun insertNewContentListItem(context: Context): Boolean {
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase
        var ok = false

        try {
            val contentValues = ContentValues().apply {
                put("list_id", listId)
                put("film_id", filmId)
                put("show_id", showId)
                put("position", position)
            }
            db.beginTransaction()
            try {
                val rowId = db.insert("ListItem", null, contentValues)
                if (rowId == -1L) throw IllegalStateException("ListItem insert returned -1")
                db.setTransactionSuccessful()
                ok = true
            } finally {
                db.endTransaction()
            }
        } catch (e: Exception) {
            Log.e("Database", "Error inserting new list item", e)
        } finally {
            db.close()
        }
        return ok
    }
}

fun getContentListItem(context: Context, listId: Int, filmId: Int, showId: Int): ContentListItem? {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    var theItem: ContentListItem? = null

    val query = """
        SELECT * FROM ListItem
        WHERE list_id = ? AND film_id = ? AND show_id = ?
    """.trimIndent()
    val selectionArgs = arrayOf(listId.toString(), filmId.toString(), showId.toString())

    try {
        val cursor = db.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {
            theItem = ContentListItem(
                listId = cursor.getInt(cursor.getColumnIndexOrThrow("list_id")),
                filmId = cursor.getInt(cursor.getColumnIndexOrThrow("film_id")),
                showId = cursor.getInt(cursor.getColumnIndexOrThrow("show_id")),
                position = cursor.getInt(cursor.getColumnIndexOrThrow("position")),
            )
        }
        cursor.close()
        Log.d("Database", "Successful Mission: Getting list item info")
    } catch (e: Exception) {
        Log.e("Database", "Error getting list item", e)
    } finally {
        db.close()
    }

    return theItem
}

fun getContentListItem(context: Context, listId: Int, position: Int): ContentListItem? {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    var theItem: ContentListItem? = null

    val query = """
        SELECT * FROM ListItem
        WHERE list_id = ? AND position = ?
    """.trimIndent()
    val selectionArgs = arrayOf(listId.toString(), position.toString())

    try {
        val cursor = db.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {
            theItem = ContentListItem(
                listId = cursor.getInt(cursor.getColumnIndexOrThrow("list_id")),
                filmId = cursor.getInt(cursor.getColumnIndexOrThrow("film_id")),
                showId = cursor.getInt(cursor.getColumnIndexOrThrow("show_id")),
                position = cursor.getInt(cursor.getColumnIndexOrThrow("position")),
            )
        }
        cursor.close()
        Log.d("Database", "Successful Mission: Getting list item info")
    } catch (e: Exception) {
        Log.e("Database", "Error getting list item", e)
    } finally {
        db.close()
    }

    return theItem
}

fun eraseContentListItem(context: Context, listId: Int, filmId: Int, showId: Int): Int? {
    val selectedContentListItem: ContentListItem? =
        getContentListItem(context, listId, filmId, showId)
    if (selectedContentListItem == null) {
        Log.e("Database", "Error getting list")
        return null
    } else {
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase

        try {
            db.execSQL(
                "DELETE FROM List WHERE list_id = ? AND film_id = ? AND show_id = ?",
                arrayOf(listId.toString(), filmId.toString(), showId.toString())
            )
            Log.d("Database", "List item with ID $listId deleted successfully")
        } catch (e: Exception) {
            Log.e("Database", "Error deleting list", e)
            return 0
        } finally {
            db.close()
        }
        return 1
    }
}

fun eraseContentListItem(context: Context, listId: Int, position: Int): Int? {
    val selectedContentListItem: ContentListItem? = getContentListItem(context, listId, position)
    if (selectedContentListItem == null) {
        Log.e("Database", "Error getting list")
        return null
    } else {
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase

        try {
            db.execSQL(
                "DELETE FROM List WHERE list_id = ? AND position = ?",
                arrayOf(listId.toString(), position.toString())
            )
            Log.d("Database", "List item with ID $listId deleted successfully")
        } catch (e: Exception) {
            Log.e("Database", "Error deleting list", e)
            return 0
        } finally {
            db.close()
        }
        return 1
    }
}

fun getContentListItems(context: Context, listId: Int): List<ContentListItem>? {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    val theItems = mutableListOf<ContentListItem>()

    val query = """
        SELECT * FROM ListItem
        WHERE list_id = ?
    """.trimIndent()
    val selectionArgs = arrayOf(listId.toString())

    try {
        val cursor = db.rawQuery(query, selectionArgs)
        cursor.use { cursor ->
            val listIdCol = cursor.getColumnIndexOrThrow("list_id")
            val filmIdCol = cursor.getColumnIndexOrThrow("film_id")
            val showIdCol = cursor.getColumnIndexOrThrow("show_id")
            val positionCol = cursor.getColumnIndexOrThrow("position")

            while (cursor.moveToNext()) {
                theItems.add(
                    ContentListItem(
                        listId = cursor.getInt(listIdCol),
                        filmId = cursor.getInt(filmIdCol),
                        showId = cursor.getInt(showIdCol),
                        position = cursor.getInt(positionCol)
                    )
                )
            }
        }
        Log.d("Database", "Successful Mission: Getting list of items")
        cursor.close()
    } catch (e: Exception) {
        Log.e("Database", "Error getting list list of items", e)
    } finally {
        db.close()
    }

    return theItems
}