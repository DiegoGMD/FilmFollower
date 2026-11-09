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

class TvShow (
    val showId: Int = 0,
    val title: String = "",
    val originalTitle: String? = "",
    val overview: String = "",
    val firstAirDate: LocalDate, // YYYY-MM-DD
    val numberOfSeasons: Int,
    val numberOfEpisodes: Int,
    val rating: Double?, // from 0 to 10, default is 0
    val posterPath: String? = "",
    val tmdbStatus: String = "", // watchlist/completed/archived
    val tmdbLastSynced: LocalDate,
    val watchStatus: String = "",
    val addedAt: LocalDate,
    val genres: List<Genre> = emptyList()
) {
    fun insertNewTvShow(context: Context): Boolean {
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase
        var ok = false

        try {
            val contentValues = ContentValues().apply {
                put("show_id", showId)
                put("title", title)
                put("original_title", originalTitle)
                put("overview", overview)
                put("first_air_date", firstAirDate.toString())
                put("number_of_seasons", numberOfSeasons)
                put("number_of_episodes", numberOfEpisodes)
                if (rating != null) {
                    put("rating", Math.round(rating * 10) / 10.0)
                } else {
                    putNull("rating")
                }
                put("poster_path", posterPath)
                put("tmdb_status", tmdbStatus)
                put("tmdb_last_synced", tmdbLastSynced.toString())
                put("watch_status", watchStatus)
                put("added_at", addedAt.toString())
            }
            db.beginTransaction()
            try {
                val rowId = db.insert("TvShow", null, contentValues)
                if (rowId == -1L) throw throw IllegalStateException("TvShow insert returned -1")
                genres.forEach { genre ->
                    val tvShowGenre = ContentValues().apply {
                        put("show_id", showId)
                        put("genre_id", genre.genreId)
                    }
                    db.insertWithOnConflict("TvShowGenre", null, tvShowGenre, SQLiteDatabase.CONFLICT_IGNORE)
                }
                db.setTransactionSuccessful()
                ok = true
            } finally {
                db.endTransaction()
            }
        } catch (e: Exception) {
            Log.e("Database", "Error inserting new tv show", e)
        } finally {
            db.close()
        }
        return ok
    }

    fun updateFromTmdb(context: Context) {
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase
        try {
            val values = ContentValues().apply {
                put("show_id", showId)
                put("title", title)
                put("original_title", originalTitle)
                put("overview", overview)
                put("first_air_date", firstAirDate.toString())
                put("number_of_seasons", numberOfSeasons)
                put("number_of_episodes", numberOfEpisodes)
                if (rating != null) {
                    put("rating", Math.round(rating * 10) / 10.0)
                } else {
                    putNull("rating")
                }
                put("poster_path", posterPath)
                put("tmdb_status", tmdbStatus)
                put("tmdb_last_synced", LocalDate.now().toString())
                // NOT touched: watch_status, watched_date, times_watched, added_at
            }
            db.beginTransaction()
            try {
                db.update("TvShow", values, "show_id = ?", arrayOf(showId.toString()))

                // genres can change on TMDB: replace the links
                db.delete("TvShowGenre", "show_id = ?", arrayOf(showId.toString()))
                genres.forEach { g ->
                    val fg = ContentValues().apply {
                        put("show_id", showId)
                        put("genre_id", g.genreId)
                    }
                    db.insertWithOnConflict("TvShowGenre", null, fg, SQLiteDatabase.CONFLICT_IGNORE)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        } catch (e: Exception) {
            Log.e("Database", "Error syncing tv show", e)
        } finally {
            db.close()
        }
    }

    fun isStatus(status: String) = watchStatus == status
    val needsSync get() = tmdbLastSynced.isBefore(LocalDate.now().minusDays(7))
}

fun getTvShow(context: Context, showId: Int): TvShow? {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    var theTvShow: TvShow? = null

    if (showId == 0) {
        Log.e("Database", "Error getting tvshow info: showId is 0")
        return null
    }

    val query = """
        SELECT * FROM TvShow
        WHERE show_id = ?
    """.trimIndent()
    val selectionArgs = arrayOf(showId.toString())

    try {
        val cursor = db.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {

            val dateStr1 = cursor.getString(cursor.getColumnIndexOrThrow("first_air_date"))
            val dateStr2 = cursor.getString(cursor.getColumnIndexOrThrow("tmdb_last_synced"))
            val dateStr3 = cursor.getString(cursor.getColumnIndexOrThrow("added_at"))

            theTvShow = TvShow(
                showId = cursor.getInt(cursor.getColumnIndexOrThrow("show_id")),
                title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                originalTitle = cursor.getString(cursor.getColumnIndexOrThrow("original_title")),
                overview = cursor.getString(cursor.getColumnIndexOrThrow("overview")),
                firstAirDate = LocalDate.parse(dateStr1),
                numberOfSeasons = cursor.getInt(cursor.getColumnIndexOrThrow("number_of_seasons")),
                numberOfEpisodes = cursor.getInt(cursor.getColumnIndexOrThrow("number_of_episodes")),
                rating = cursor.getDouble(cursor.getColumnIndexOrThrow("rating")),
                posterPath = cursor.getString(cursor.getColumnIndexOrThrow("poster_path")),
                tmdbStatus = cursor.getString(cursor.getColumnIndexOrThrow("tmdb_status")),
                tmdbLastSynced = LocalDate.parse(dateStr2),
                watchStatus = cursor.getString(cursor.getColumnIndexOrThrow("watch_status")),
                addedAt = LocalDate.parse(dateStr3)
            )
        }
        cursor.close()
        Log.d("Database", "Successful Mission: Getting tv show info")
    } catch (e: Exception) {
        Log.e("Database", "Error getting tv show", e)
    } finally {
        db.close()
    }
    return theTvShow
}

fun eraseTvShow(context: Context, showId: Int): Int? {
    val watchlistedTvShow: TvShow? = getTvShow(context, showId)
    if (watchlistedTvShow == null) {
        Log.e("Database", "Error getting tv show")
        return null
    } else {
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase

        try {
            db.execSQL(
                "DELETE FROM TvShow WHERE show_id = ?", arrayOf(showId.toString())
            )
            Log.d("Database", "TvShow with ID $showId deleted successfully")
        } catch (e: Exception) {
            Log.e("Database", "Error deleting tv show", e)
            return 0
        } finally {
            db.close()
        }

        return 1
    }
}

fun getTvShowStatus(context: Context, id: Int): String? {
    return getTvShow(context, id)?.tmdbStatus
}

fun getAllTvShowStatus(context: Context, status: String, released: Int? = null): List<TvShow>? {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    val theTvShows = mutableListOf<TvShow>()

    var query: String? = null
    var selectionArgs: Array<String>? = null

    when (released) {
        0 -> {
            query = """ SELECT * FROM TvShow WHERE watch_status = ? AND release_date > ?"""
            selectionArgs = arrayOf(status, LocalDate.now().toString())
        }

        1 -> {
            query = """ SELECT * FROM TvShow WHERE watch_status = ? AND release_date <= ?"""
            selectionArgs = arrayOf(status, LocalDate.now().toString())
        }

        else -> {
            query = """ SELECT * FROM TvShow WHERE watch_status = ?"""
            selectionArgs = arrayOf(status)
        }
    }

    db.rawQuery(query, selectionArgs).use { cursor ->
        while (cursor.moveToNext()) {
            val dateStr1 = cursor.getString(cursor.getColumnIndexOrThrow("first_air_date"))
            val dateStr2 = cursor.getString(cursor.getColumnIndexOrThrow("tmdb_last_synced"))
            val dateStr3 = cursor.getString(cursor.getColumnIndexOrThrow("added_at"))

            val tvShow = TvShow(
                showId = cursor.getInt(cursor.getColumnIndexOrThrow("show_id")),
                title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                originalTitle = cursor.getString(cursor.getColumnIndexOrThrow("original_title")),
                overview = cursor.getString(cursor.getColumnIndexOrThrow("overview")),
                firstAirDate = LocalDate.parse(dateStr1),
                numberOfSeasons = cursor.getInt(cursor.getColumnIndexOrThrow("number_of_seasons")),
                numberOfEpisodes = cursor.getInt(cursor.getColumnIndexOrThrow("number_of_episodes")),
                rating = cursor.getDouble(cursor.getColumnIndexOrThrow("rating")),
                posterPath = cursor.getString(cursor.getColumnIndexOrThrow("poster_path")),
                tmdbStatus = cursor.getString(cursor.getColumnIndexOrThrow("tmdb_status")),
                tmdbLastSynced = LocalDate.parse(dateStr2),
                watchStatus = cursor.getString(cursor.getColumnIndexOrThrow("watch_status")),
                addedAt = LocalDate.parse(dateStr3)
            )

            theTvShows.add(tvShow)
        }
    }

    return if (theTvShows.isEmpty()) null else theTvShows
}

fun isTvShowStatus(context: Context, id: Int, status: String): Boolean =
    getTvShow(context, id)?.watchStatus == status

@SuppressLint("ViewModelConstructorInComposable")
fun setTvShowStatus(context: Context, id: Int, status: String) {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.writableDatabase

    try {
        db.execSQL(
            "UPDATE TvShow SET watch_status = ? WHERE show_id = ?", arrayOf(status, id.toString())
        )
        Log.d("Database", "Content with ID $id status is set as $status")
    } catch (e: Exception) {
        Log.e("Database", "Error changing state", e)
    } finally {
        db.close()
    }
}

suspend fun syncTvShow(
    context: Context, id: Int, repository: SearchRepository, force: Boolean = false
): Boolean {
    val local = withContext(Dispatchers.IO) { getTvShow(context, id) } ?: return false
    if (!local.needsSync && !force) return false

    val fresh = try {
        repository.getTvShow(id)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        return false // offline/API error: keep old data and old tmdb_last_synced so it retries next time
    }

    withContext(Dispatchers.IO) { fresh.updateFromTmdb(context) }
    return true
}

fun isFavouriteTvShow(context: Context, showId: Int): Boolean {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    var isFavourite = false

    val query = """
        SELECT 1 FROM ListItem
        WHERE list_id = ? and show_id = ?
        LIMIT 1
    """.trimIndent()
    val selectionArgs = arrayOf("1", showId.toString())

    try {
        db.rawQuery(query, selectionArgs).use { cursor ->
            isFavourite = cursor.moveToFirst()
        }
    } catch (e: Exception) {
        Log.e("Database", "Error getting if is a favourite tv show", e)
    } finally {
        db.close()
    }

    return isFavourite
}

fun setFavouriteTvShow(context: Context, showId: Int): Boolean {
    val contentListItem = ContentListItem(listId = 1, filmId = 0, showId = showId)
    return contentListItem.insertNewContentListItem(context)
}

fun unsetFavouriteTvShow(context: Context, showId: Int): Boolean {
    return eraseContentListItem(context, listId = 1, filmId = 0, showId = showId) == 1
}

fun getAllFavouriteTvShows(context: Context): List<TvShow>? {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    val theTvShows = mutableListOf<TvShow>()

    val query = """
        SELECT TvShow.* FROM TvShow
        INNER JOIN ListItem ON TvShow.show_id = ListItem.show_id
        WHERE ListItem.list_id = ?
        ORDER BY ListItem.position
    """.trimIndent()

    try {
        db.rawQuery(query, arrayOf("1")).use { cursor ->
            while (cursor.moveToNext()) {
                val dateStr1 = cursor.getString(cursor.getColumnIndexOrThrow("first_air_date"))
                val dateStr2 = cursor.getString(cursor.getColumnIndexOrThrow("tmdb_last_synced"))
                val dateStr3 = cursor.getString(cursor.getColumnIndexOrThrow("added_at"))

                val tvShow = TvShow(
                    showId = cursor.getInt(cursor.getColumnIndexOrThrow("show_id")),
                    title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    originalTitle = cursor.getString(cursor.getColumnIndexOrThrow("original_title")),
                    overview = cursor.getString(cursor.getColumnIndexOrThrow("overview")),
                    firstAirDate = LocalDate.parse(dateStr1),
                    numberOfSeasons = cursor.getInt(cursor.getColumnIndexOrThrow("number_of_seasons")),
                    numberOfEpisodes = cursor.getInt(cursor.getColumnIndexOrThrow("number_of_episodes")),
                    rating = cursor.getDouble(cursor.getColumnIndexOrThrow("rating")),
                    posterPath = cursor.getString(cursor.getColumnIndexOrThrow("poster_path")),
                    tmdbStatus = cursor.getString(cursor.getColumnIndexOrThrow("tmdb_status")),
                    tmdbLastSynced = LocalDate.parse(dateStr2),
                    watchStatus = cursor.getString(cursor.getColumnIndexOrThrow("watch_status")),
                    addedAt = LocalDate.parse(dateStr3)
                )

                theTvShows.add(tvShow)
            }
        }
    } catch (e: Exception) {
        Log.e("Database", "Error getting favourite tv shows", e)
    } finally {
        db.close()
    }

    return if (theTvShows.isEmpty()) null else theTvShows
}