package com.diegogmd.filmfollower.model

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.diegogmd.filmfollower.data.local.FilmFillowerDatabase
import com.diegogmd.filmfollower.data.repository.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import kotlin.coroutines.cancellation.CancellationException

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
    val rating: Double?, // between 0.0 AND 10.0, default 0.0
    val watchStatus: String, // watchlist/seen/archived
    val watchedDate: LocalDate?,
    val timesWatched: Int = 0,
    val addedAt: LocalDate,
    val genres: List<Genre> = emptyList()
) {
    @SuppressLint("DefaultLocale")
    fun insertNewFilm(context: Context): Boolean {
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase
        var ok = false

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
                val rowId = db.insert("Film", null, contentValues)
                if (rowId == -1L) throw IllegalStateException("Film insert returned -1")
                genres.forEach { g ->
                    val fg = ContentValues().apply {
                        put("film_id", filmId)
                        put("genre_id", g.genreId)
                    }
                    db.insertWithOnConflict("FilmGenre", null, fg, SQLiteDatabase.CONFLICT_IGNORE)
                }
                db.setTransactionSuccessful()
                ok = true
            } finally {
                db.endTransaction()
            }
        } catch (e: Exception) {
            Log.e("Database", "Error inserting new film", e)
        } finally {
            db.close()
        }
        return ok
    }

    fun updateFromTmdb(context: Context) {
        val db = FilmFillowerDatabase(context).writableDatabase
        try {
            val values = ContentValues().apply {
                put("title", title)
                put("original_title", originalTitle)
                put("overview", overview)
                put("release_date", releaseDate.toString())
                put("runtime", runtime)
                put("poster_path", posterPath)
                put("tmdb_status", tmdbStatus)
                put("tmdb_last_synced", LocalDate.now().toString())
                // NOT touched: rating, watch_status, watched_date, times_watched, added_at
            }
            db.beginTransaction()
            try {
                db.update("Film", values, "film_id = ?", arrayOf(filmId.toString()))

                // genres can change on TMDB: replace the links
                db.delete("FilmGenre", "film_id = ?", arrayOf(filmId.toString()))
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
            Log.e("Database", "Error syncing film", e)
        } finally {
            db.close()
        }
    }

    fun isStatus(status: String) = watchStatus == status
    val needsSync get() = tmdbLastSynced.isBefore(LocalDate.now().minusDays(7))
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
): Int? {
    val watchlistedFilm: Film? = getFilm(context, filmId)
    if (watchlistedFilm == null) {
        Log.e("Database", "Error getting film")
        return null
    } else {
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase

        try {
            db.execSQL(
                "DELETE FROM Film WHERE film_id = ?", arrayOf(filmId.toString())
            )
            Log.d("Database", "Film with ID $filmId deleted successfully")
        } catch (e: Exception) {
            Log.e("Database", "Error deleting film", e)
            return 0
        } finally {
            db.close()
        }
        return 1
    }
}

fun getStatus(context: Context, id: Int): String? {
    return getFilm(context, id)?.tmdbStatus
}

fun getAllStatus(context: Context, status: String, released: Int? = null): List<Film>? {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    val theFilms = mutableListOf<Film>()

    var query: String? = null
    var selectionArgs: Array<String>? = null

    when (released) {
        0 -> {
            query = """ SELECT * FROM Film WHERE watch_status = ? AND release_date > ?"""
            selectionArgs = arrayOf(status, LocalDate.now().toString())
        }

        1 -> {
            query = """ SELECT * FROM Film WHERE watch_status = ? AND release_date <= ?"""
            selectionArgs = arrayOf(status, LocalDate.now().toString())
        }

        else -> {
            query = """ SELECT * FROM Film WHERE watch_status = ?"""
            selectionArgs = arrayOf(status)
        }
    }

    db.rawQuery(query, selectionArgs).use { cursor ->
        while (cursor.moveToNext()) {
            val dateStr1 = cursor.getString(cursor.getColumnIndexOrThrow("release_date"))
            val dateStr2 = cursor.getString(cursor.getColumnIndexOrThrow("tmdb_last_synced"))
            val dateStr3 = cursor.getString(cursor.getColumnIndexOrThrow("watched_date"))
            val dateStr4 = cursor.getString(cursor.getColumnIndexOrThrow("added_at"))

            val film = Film(
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

            theFilms.add(film)
        }
    }

    return if (theFilms.isEmpty()) null else theFilms
}

fun isStatus(context: Context, id: Int, status: String): Boolean =
    getFilm(context, id)?.watchStatus == status

@SuppressLint("ViewModelConstructorInComposable")
fun setStatus(context: Context, id: Int, status: String) {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.writableDatabase

    try {
        db.execSQL(
            "UPDATE Film SET watch_status = ? WHERE film_id = ?", arrayOf(status, id.toString())
        )
        Log.d("Database", "Content with ID $id status is set as $status")
    } catch (e: Exception) {
        Log.e("Database", "Error changing state", e)
    } finally {
        db.close()
    }
}

suspend fun syncFilm(context: Context, id: Int, repository: SearchRepository): Boolean {
    val local = withContext(Dispatchers.IO) { getFilm(context, id) } ?: return false
    if (!local.needsSync) return false

    val fresh = try {
        repository.getFilm(id)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        return false // offline/API error: keep old data and old tmdb_last_synced so it retries next time
    }

    withContext(Dispatchers.IO) { fresh.updateFromTmdb(context) }
    return true
}