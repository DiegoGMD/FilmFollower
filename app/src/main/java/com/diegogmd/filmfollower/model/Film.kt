package com.diegogmd.filmfollower.model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.diegogmd.filmfollower.data.local.FilmFillowerDatabase
import org.threeten.bp.LocalDate


class Film(
    val filmId: Int,
    val title:String,
    val originalTitle:String,
    val overview:String,
    val releaseDate:LocalDate,
    val runtime: Int,
    val posterPath:String,
    val tmdbStatus:String,
    val tmdbLastSynced:LocalDate,
    val rating: Double?,
    val watchStatus:String,
    val watchedDate: LocalDate?,
    val timesWatched: Int = 0,
    val addedAt:LocalDate
) {
    fun insertNewFilm(context: Context){
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase

        try {
            val contentValues = ContentValues().apply {
                put("film_id", filmId)
                put("title", title)
                put("original_title", originalTitle)
                put("overview", overview)
                put("release_date", releaseDate.toString())
                put("runtime", runtime)
                put("poster_path", posterPath)
                put("tmdb_status", tmdbStatus)
                put("tmdb_last_synced", tmdbLastSynced.toString())
                if (rating != null) {
                    put("rating", rating.toString())
                } else {
                    putNull("rating")
                }
                put("watch_status", watchStatus)
                if (watchedDate != null) {
                    put("watched_date", watchedDate.toString())
                } else {
                    putNull("watched_date")
                }
                put("times_watched", timesWatched)
                put("added_at", addedAt.toString())
            }
            db.insert("Film", null, contentValues)

        } catch (e: Exception) {
            Log.e("Database", "Error inserting new film", e)
        } finally {
            db.close()
        }
    }

    fun eraseFilm(context: Context): Int {
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase
        var rowsAffected = 0

        try {
            val whereClause = "film_id = ?"
            val whereArgs = arrayOf(filmId.toString())

            rowsAffected = db.delete("Film", whereClause, whereArgs)

            if (rowsAffected > 0) {
                Log.d("Database", "Film deleted successfully. Rows affected: $rowsAffected")
            } else {
                Log.e("Database", "Failed to delete film with ID: $filmId")
            }
        } catch (e: Exception) {
            Log.e("Database", "Error deleting film", e)
        } finally {
            db.close()
        }
        return rowsAffected
    }
}

fun getFilm(context: Context, filmId: Int): Film? {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    var theFilm: Film? = null

    if (filmId == 0){
        Log.e("Database", "Error getting film info: filmId is null or 0")
        return null
    }

    val query = """
            SELECT * FROM Film
            WHERE film_id = ?
        """
    val selectionArgs = arrayOf(filmId.toString())

    try {
        val cursor = db.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {
            val dateStr1 = cursor.getString(cursor.getColumnIndexOrThrow("release_date"))
            val dateStr2 = cursor.getString(cursor.getColumnIndexOrThrow("tmdb_last_synced"))
            val dateStr3 = cursor.getString(cursor.getColumnIndexOrThrow("watched_date"))
            val dateStr4 = cursor.getString(cursor.getColumnIndexOrThrow("added_at"))

            theFilm = Film(
                filmId = cursor.getInt(cursor.getColumnIndexOrThrow("film_id")),
                title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                originalTitle = cursor.getString(cursor.getColumnIndexOrThrow("original_title")),
                overview = cursor.getString(cursor.getColumnIndexOrThrow("overview")),
                releaseDate = LocalDate.parse(dateStr1),
                runtime = cursor.getInt(cursor.getColumnIndexOrThrow("runtime")),
                posterPath = cursor.getString(cursor.getColumnIndexOrThrow("poster_path")),
                tmdbStatus = cursor.getString(cursor.getColumnIndexOrThrow("tmdb_status")),
                tmdbLastSynced = LocalDate.parse(dateStr2),
                rating = cursor.getDouble(cursor.getColumnIndexOrThrow("rating")),
                watchStatus = cursor.getString(cursor.getColumnIndexOrThrow("watch_status")),
                watchedDate = dateStr3?.let { LocalDate.parse(it) },
                timesWatched = cursor.getInt(cursor.getColumnIndexOrThrow("times_watched")),
                addedAt = LocalDate.parse(dateStr4)
            )

        }
        cursor.close()
        Log.d("Database", "Successful Mission: Getting film info")
    } catch (e: Exception) {
        Log.e("Database", "Error getting film", e)
    } finally {
        db.close()
    }

    return theFilm
}

fun eraseFilm(
    context: Context,
    filmId: Int,
) {
    val wishlistedFilm: Film? = getFilm(context, filmId)
    if (wishlistedFilm == null) {
        Log.e("Database", "Error getting film")
    } else {
        wishlistedFilm.eraseFilm(context)
    }
}

private fun cursorToFilm(cursor: Cursor): Film {
    return Film(
        filmId = cursor.getInt(cursor.getColumnIndexOrThrow("film_id")),
        title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
        originalTitle = cursor.getString(cursor.getColumnIndexOrThrow("original_title")),
        overview = cursor.getString(cursor.getColumnIndexOrThrow("overview")),
        releaseDate = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow("release_date"))),
        runtime = cursor.getInt(cursor.getColumnIndexOrThrow("runtime")),
        posterPath = cursor.getString(cursor.getColumnIndexOrThrow("poster_path")),
        tmdbStatus = cursor.getString(cursor.getColumnIndexOrThrow("tmdb_status")),
        tmdbLastSynced = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow("tmdb_last_synced"))),
        rating = if (cursor.isNull(cursor.getColumnIndexOrThrow("rating"))) null
                 else cursor.getDouble(cursor.getColumnIndexOrThrow("rating")),
        watchStatus = cursor.getString(cursor.getColumnIndexOrThrow("watch_status")),
        watchedDate = cursor.getString(cursor.getColumnIndexOrThrow("watched_date"))?.let { LocalDate.parse(it) },
        timesWatched = cursor.getInt(cursor.getColumnIndexOrThrow("times_watched")),
        addedAt = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow("added_at")))
    )
}

private fun queryFilms(context: Context, whereClause: String? = null, whereArgs: Array<String>? = null): List<Film> {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    val films = mutableListOf<Film>()

    try {
        val cursor = db.query(
            "Film",
            null,
            whereClause,
            whereArgs,
            null,
            null,
            "release_date DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                films.add(cursorToFilm(it))
            }
        }
        cursor.close()
        Log.d("Database", "Successful Mission: Getting film info")
    } catch (e: Exception) {
        Log.e("Database", "Error querying films", e)
    } finally {
        db.close()
    }
    return films
}

fun getWishlistedFilms(context: Context): List<Film> =
    queryFilms(context, "watch_status = ?", arrayOf("wishlist"))

fun getWishlistedReleasedFilms(context: Context): List<Film> =
    queryFilms(
        context,
        "release_date <= ? AND watch_status = ?",
        arrayOf(LocalDate.now().toString(), "wishlist")
    )

fun getWishlistedUpcomingFilms(context: Context): List<Film> =
    queryFilms(
        context,
        "release_date > ? AND watch_status = ?",
        arrayOf(LocalDate.now().toString(), "wishlist")
    )