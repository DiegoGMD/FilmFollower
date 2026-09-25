package com.diegogmd.filmfollower.model

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.diegogmd.filmfollower.data.local.FilmFillowerDatabase

class Genre(
    val genreId: Int,
    val name:String
) {
    fun insertGenre(context: Context){
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase

        try {
            val contentValues = ContentValues().apply {
                put("genre_id", genreId)
                put("name", name)
            }
            db.insert("Genre", null, contentValues)

        } catch (e: Exception) {
            Log.e("Database", "Error inserting new genre", e)
        } finally {
            db.close()
        }
    }

    fun eraseGenre(context: Context): Int {
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase
        var rowsAffected = 0

        try {
            val whereClause = "genre_id = ?"
            val whereArgs = arrayOf(genreId.toString())

            rowsAffected = db.delete("Genre", whereClause, whereArgs)

            if (rowsAffected > 0) {
                Log.d("Database", "Genre deleted successfully. Rows affected: $rowsAffected")
            } else {
                Log.e("Database", "Failed to delete genre with ID: $genreId")
            }
        } catch (e: Exception) {
            Log.e("Database", "Error deleting genre", e)
        } finally {
            db.close()
        }
        return rowsAffected
    }
}

fun getGenre(context: Context, genreId: Int): Genre? {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    var theGenre: Genre? = null

    if (genreId == 0) {
        Log.e("Database", "Error getting genre info: genreId is null or 0")
        return null
    }

    val query = """
            SELECT * FROM Genre
            WHERE genre_id = ?
        """
    val selectionArgs = arrayOf(genreId.toString())

    try {
        val cursor = db.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {
            theGenre = Genre(
                genreId = cursor.getInt(cursor.getColumnIndexOrThrow("genre_id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            )

        }
        cursor.close()
        Log.d("Database", "Successful Mission: Getting genre info")
    } catch (e: Exception) {
        Log.e("Database", "Error getting genre", e)
    } finally {
        db.close()
    }

    return theGenre
}

@Composable
fun defaultGenreList() {
    var context = LocalContext.current

    Genre(12, "Adventure").insertGenre(context)
    Genre(14, "Fantasy").insertGenre(context)
    Genre(16, "Animation").insertGenre(context)
    Genre(18, "Drama").insertGenre(context)
    Genre(27, "Horror").insertGenre(context)
    Genre(28, "Action").insertGenre(context)
    Genre(35, "Comedy").insertGenre(context)
    Genre(36, "History").insertGenre(context)
    Genre(37, "Western").insertGenre(context)
    Genre(53, "Thriller").insertGenre(context)
    Genre(80, "Crime").insertGenre(context)
    Genre(99, "Documentary").insertGenre(context)
    Genre(878, "Science Fiction").insertGenre(context)
    Genre(9648, "Mystery").insertGenre(context)
    Genre(10402, "Music").insertGenre(context)
    Genre(10749, "Romance").insertGenre(context)
    Genre(10751, "Family").insertGenre(context)
    Genre(10752, "War").insertGenre(context)
    Genre(10759, "Action & Adventure").insertGenre(context)
    Genre(10762, "Kids").insertGenre(context)
    Genre(10763, "News").insertGenre(context)
    Genre(10764, "Reality").insertGenre(context)
    Genre(10765, "Sci-Fi & Fantasy").insertGenre(context)
    Genre(10766, "Soap").insertGenre(context)
    Genre(10767, "Talk").insertGenre(context)
    Genre(10768, "War & Politics").insertGenre(context)
    Genre(10770, "TV Movie").insertGenre(context)
}

class FilmGenre(
    val filmId: Int,
    val genreId: Int
){
    fun insertFilmGenre(context: Context){
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase

        try {
            val contentValues = ContentValues().apply {
                put("film_id", filmId)
                put("genre_id", genreId)
            }
            db.insert("FilmGenre", null, contentValues)

        } catch (e: Exception) {
            Log.e("Database", "Error inserting new filmGenre", e)
        } finally {
            db.close()
        }
    }

    fun eraseFilmGenre(context: Context): Int {
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase
        var rowsAffected = 0

        try {
            val whereClause = "film_id = ? AND genre_id = ? "
            val whereArgs = arrayOf(filmId.toString(), genreId.toString())

            rowsAffected = db.delete("FilmGenre", whereClause, whereArgs)

            if (rowsAffected > 0) {
                Log.d("Database", "FilmGenre deleted successfully. Rows affected: $rowsAffected")
            } else {
                Log.e("Database", "Failed to delete filmGenre with film_id: $filmId and genre_id: $genreId")
            }
        } catch (e: Exception) {
            Log.e("Database", "Error deleting filmGenre", e)
        } finally {
            db.close()
        }
        return rowsAffected
    }
}

fun getFilmGenre(context: Context, filmId: Int): String {
    val dbHelper =FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    val genreNames = mutableListOf<String>()

    if (filmId == 0){
        Log.e("Database", "Error getting film info: filmId is null or 0")
        return "Unknown"
    }

    val query = """
        SELECT g.name
        FROM FilmGenre fg
        INNER JOIN Genre g ON fg.genre_id = g.genre_id
        WHERE fg.film_id = ?
    """.trimIndent()
    val selectionArgs = arrayOf(filmId.toString())

    try {
        val cursor = db.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            genreNames.add(name)
        }
        cursor.close()
        Log.d("Database", "Successful Mission: Getting genre info")
    } catch (e: Exception) {
        Log.e("Database", "Error getting filmGenre", e)
    } finally {
        db.close()
    }

    return if (genreNames.isEmpty()) "Unknown" else genreNames.joinToString(", ")
}

class TvShowGenre(
    val showId: Int,
    val genreId: Int
){
    fun insertTvShowGenre(context: Context){
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase

        try {
            val contentValues = ContentValues().apply {
                put("show_id", showId)
                put("genre_id", genreId)
            }
            db.insert("TvShowGenre", null, contentValues)

        } catch (e: Exception) {
            Log.e("Database", "Error inserting new tvShowGenre", e)
        } finally {
            db.close()
        }
    }

    fun eraseTvShowGenre(context: Context): Int {
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase
        var rowsAffected = 0

        try {
            val whereClause = "show_id = ? AND genre_id = ? "
            val whereArgs = arrayOf(showId.toString(), genreId.toString())

            rowsAffected = db.delete("TvShowGenre", whereClause, whereArgs)

            if (rowsAffected > 0) {
                Log.d("Database", "TvShowGenre deleted successfully. Rows affected: $rowsAffected")
            } else {
                Log.e("Database", "Failed to delete tvShowGenre with show_id: $showId and genre_id: $genreId")
            }
        } catch (e: Exception) {
            Log.e("Database", "Error deleting tvShowGenre", e)
        } finally {
            db.close()
        }
        return rowsAffected
    }
}

fun getTvShowGenre(context: Context, showId: Int): String {
    val dbHelper =FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    val genreNames = mutableListOf<String>()

    if (showId == 0){
        Log.e("Database", "Error getting tvShow info: showId is null or 0")
        return "Unknown"
    }

    val query = """
        SELECT g.name
        FROM TvShowGenre sg
        INNER JOIN Genre g ON sg.genre_id = g.genre_id
        WHERE sg.show_id = ?
    """.trimIndent()
    val selectionArgs = arrayOf(showId.toString())

    try {
        val cursor = db.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            genreNames.add(name)
        }
        cursor.close()
        Log.d("Database", "Successful Mission: Getting genre info")
    } catch (e: Exception) {
        Log.e("Database", "Error getting tvShowGenre", e)
    } finally {
        db.close()
    }

    return if (genreNames.isEmpty()) "Unknown" else genreNames.joinToString(", ")
}