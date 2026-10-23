package com.diegogmd.filmfollower.model

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
    var position: Int? = null
) {
    fun insertNewContentListItem(context: Context): Boolean {
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase
        var ok = false

        if (position == null) {
            position = (getContentListItemCount(context, listId) ?: 0) + 1
        }

        try {
            val contentValues = ContentValues().apply {
                put("list_id", listId)
                if (filmId != 0) put("film_id", filmId) else putNull("film_id")
                if (showId != 0) put("show_id", showId) else putNull("show_id")
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
        WHERE list_id = ? AND COALESCE(film_id, 0) = ? AND COALESCE(show_id, 0) = ?
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
    val item = getContentListItem(context, listId, filmId, showId)
    if (item == null) {
        Log.e("Database", "Error getting list item")
        return null
    }
    val removedPosition = item.position ?: 0

    val db = FilmFillowerDatabase(context).writableDatabase
    try {
        db.beginTransaction()
        try {
            db.execSQL(
                "DELETE FROM ListItem WHERE list_id = ? AND film_id = ? AND show_id = ?",
                arrayOf(listId.toString(), filmId.toString(), showId.toString())
            )
            db.execSQL(
                "UPDATE ListItem SET position = position - 1 WHERE list_id = ? AND position > ?",
                arrayOf<Any>(listId, removedPosition)
            )
            db.execSQL(
                "UPDATE ListItem SET position = -position - 1 WHERE list_id = ? AND position < 0",
                arrayOf<Any>(listId)
            )
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        Log.d("Database", "List item deleted from list $listId")
    } catch (e: Exception) {
        Log.e("Database", "Error deleting list item", e)
        return 0
    } finally {
        db.close()
    }
    return 1
}

fun eraseContentListItem(context: Context, listId: Int, position: Int): Int? { // Not finished
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

fun changePositionContentListItem(
    context: Context,
    listId: Int,
    oldPosition: Int,
    newPosition: Int
): Int? {
    val selectedContentListItem = getContentListItem(context, listId, oldPosition)
    if (selectedContentListItem == null) {
        Log.e("Database", "Error getting list item")
        return null
    }
    if (oldPosition == newPosition) return 1

    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.writableDatabase

    try {
        db.beginTransaction()
        try {
            // Set moving item on a temporary position so nothing collides
            db.execSQL(
                "UPDATE ListItem SET position = -1 WHERE list_id = ? AND position = ?",
                arrayOf<Any>(listId, oldPosition)
            )

            if (newPosition < oldPosition) {
                // If you move up: items in [new, old-1] go +1
                db.execSQL(
                    "UPDATE ListItem SET position = position + 1 " +
                            "WHERE list_id = ? AND position BETWEEN ? AND ?",
                    arrayOf<Any>(listId, newPosition, oldPosition - 1)
                )
            } else {
                // If you move down: items in [old+1, new] go -1
                db.execSQL(
                    "UPDATE ListItem SET position = position - 1 " +
                            "WHERE list_id = ? AND position BETWEEN ? AND ?",
                    arrayOf<Any>(listId, oldPosition + 1, newPosition)
                )
            }

            // Place the moving item in its new position
            db.execSQL(
                "UPDATE ListItem SET position = ? WHERE list_id = ? AND position = -1",
                arrayOf<Any>(newPosition, listId)
            )

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        Log.d("Database", "Item moved from $oldPosition to $newPosition in list $listId")
    } catch (e: Exception) {
        Log.e("Database", "Error changing item position", e)
        return 0
    } finally {
        db.close()
    }
    return 1
}

fun getContentListItemCount(context: Context, listId: Int): Int? {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    var count: Int? = null

    val query = "SELECT COUNT(*) FROM ListItem WHERE list_id = ?"
    val selectionArgs = arrayOf(listId.toString())

    try {
        db.rawQuery(query, selectionArgs).use { cursor ->
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0)
            }
        }
        Log.d("Database", "Successful Mission: Getting item count for list $listId")
    } catch (e: Exception) {
        Log.e("Database", "Error getting item count", e)
    } finally {
        db.close()
    }

    return count
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