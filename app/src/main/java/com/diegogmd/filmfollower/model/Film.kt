package com.diegogmd.filmfollower.model

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.diegogmd.filmfollower.data.local.FilmFillowerDatabase
import org.threeten.bp.LocalDate


class Film(
    val filmId: Int,
    val title: String,
    val originalTitle: String,
    val overview: String,
    val releaseDate: LocalDate,
    val runtime: Int,
    val posterPath: String,
    val tmdbStatus: String,
    val tmdbLastSynced: LocalDate,
    val rating: Double?,
    val watchStatus: String,
    val watchedDate: LocalDate?,
    val timesWatched: Int = 0,
    val addedAt: LocalDate,
    val genres: List<Genre> = emptyList()
) {
    @SuppressLint("DefaultLocale")
    fun insertNewFilm(context: Context) {
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
                    put("rating", Math.round(rating * 10) / 10.0)
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
            db.beginTransaction()
            try {
                db.insert("Film", null, contentValues)
                genres.forEach { g ->
                    val fg = ContentValues().apply {
                        put("film_id", filmId)
                        put("genre_id", g.genreId)
                    }
                    db.insertWithOnConflict("FilmGenre", null, fg, SQLiteDatabase.CONFLICT_IGNORE)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }

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

    if (filmId == 0) {
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
    val watchlistedFilm: Film? = getFilm(context, filmId)
    if (watchlistedFilm == null) {
        Log.e("Database", "Error getting film")
    } else {
        watchlistedFilm.eraseFilm(context)
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
        watchedDate = cursor.getString(cursor.getColumnIndexOrThrow("watched_date"))
            ?.let { LocalDate.parse(it) },
        timesWatched = cursor.getInt(cursor.getColumnIndexOrThrow("times_watched")),
        addedAt = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow("added_at")))
    )
}

private fun queryFilms(
    context: Context, whereClause: String? = null, whereArgs: Array<String>? = null
): List<Film> {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    val films = mutableListOf<Film>()

    try {
        val cursor = db.query(
            "Film", null, whereClause, whereArgs, null, null, "release_date DESC"
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

private fun existsFilm(
    context: Context, whereClause: String, whereArgs: Array<String>
): Boolean {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase

    return try {
        db.rawQuery(
            "SELECT EXISTS(SELECT 1 FROM Film WHERE $whereClause LIMIT 1)", whereArgs
        ).use { cursor ->
            cursor.moveToFirst() && cursor.getInt(0) == 1
        }
    } catch (e: Exception) {
        Log.e("Database", "Error checking films", e)
        false
    } finally {
        db.close()
    }
}

fun isWatchlisted(context: Context, filmId: Int): Boolean {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    var exists = false

    if (filmId == 0) {
        Log.e("Database", "Error checking film: filmId is 0")
        return false
    }

    val query = """
            SELECT EXISTS(
                SELECT 1 FROM Film
                WHERE film_id = ?
            )
        """.trimIndent()

    try {
        db.rawQuery(query, arrayOf(filmId.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                exists = cursor.getInt(0) == 1
            }
        }
    } catch (e: Exception) {
        Log.e("Database", "Error checking film existence", e)
    } finally {
        db.close()
    }

    return exists
}

fun anyWatchlistedFilm(context: Context): Boolean =
    existsFilm(context, "watch_status = ?", arrayOf("watchlist"))

fun anyWatchlistedReleasedFilm(context: Context): Boolean = existsFilm(
    context,
    "release_date <= ? AND watch_status = ?",
    arrayOf(LocalDate.now().toString(), "watchlist")
)

fun anyWatchlistedUpcomingFilm(context: Context): Boolean = existsFilm(
    context,
    "release_date > ? AND watch_status = ?",
    arrayOf(LocalDate.now().toString(), "watchlist")
)

fun getWatchlistedFilms(context: Context): List<Film> =
    queryFilms(context, "watch_status = ?", arrayOf("watchlist"))

fun getWatchlistedReleasedFilms(context: Context): List<Film> = queryFilms(
    context,
    "release_date <= ? AND watch_status = ?",
    arrayOf(LocalDate.now().toString(), "watchlist")
)

fun getWatchlistedUpcomingFilms(context: Context): List<Film> = queryFilms(
    context,
    "release_date > ? AND watch_status = ?",
    arrayOf(LocalDate.now().toString(), "watchlist")
)