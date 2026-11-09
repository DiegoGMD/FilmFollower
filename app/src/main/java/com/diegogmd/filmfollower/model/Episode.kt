package com.diegogmd.filmfollower.model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.diegogmd.filmfollower.data.local.FilmFillowerDatabase
import com.diegogmd.filmfollower.model.responses.toLocalDateOrNull
import org.threeten.bp.LocalDate

class Episode(
    val showId: Int,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val tmdbEpisodeId: Int? = null,
    val title: String,
    val overview: String,
    val airDate: LocalDate? = null,
    val runtime: Int? = null,
    val watchedDate: LocalDate? = null
) {
    fun insertNewEpisode(context: Context): Boolean {
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase
        var ok = false

        try {
            val contentValues = ContentValues().apply {
                put("show_id", showId)
                put("season_number", seasonNumber)
                put("episode_number", episodeNumber)
                put("tmdb_episode_id", tmdbEpisodeId)
                put("title", title)
                put("overview", overview)
                if (airDate != null) {
                    put("air_date", airDate.toString())
                } else {
                    putNull("air_date")
                }
                put("runtime", runtime)
                if (watchedDate != null) {
                    put("watched_date", watchedDate.toString())
                } else {
                    putNull("watched_date")
                }
            }
            db.beginTransaction()
            try {
                val rowId = db.insert("Episode", null, contentValues)
                if (rowId == -1L) throw IllegalStateException("Episode insert returned -1")
                db.setTransactionSuccessful()
                ok = true
            } finally {
                db.endTransaction()
            }
        } catch (e: Exception) {
            Log.e("Database", "Error inserting new episode", e)
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
                put("tmdb_episode_id", tmdbEpisodeId)
                put("title", title)
                put("overview", overview)
                put("air_date", airDate?.toString())
                put("runtime", runtime)
            }
            db.beginTransaction()
            try {
                db.update(
                    "Episode",
                    values,
                    "show_id = ? AND season_number = ? AND episode_number = ?",
                    arrayOf(showId.toString(), seasonNumber.toString(), episodeNumber.toString())
                )
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        } catch (e: Exception) {
            Log.e("Database", "Error syncing episode", e)
        } finally {
            db.close()
        }
    }

    internal fun upsertInto(db: SQLiteDatabase) {
        val whereArgs =
            arrayOf(showId.toString(), seasonNumber.toString(), episodeNumber.toString())
        // Full row, only used if the episode doesn't exist yet
        val insertValues = ContentValues().apply {
            put("show_id", showId)
            put("season_number", seasonNumber)
            put("episode_number", episodeNumber)
            put("tmdb_episode_id", tmdbEpisodeId)
            put("title", title)
            put("overview", overview)
            put("air_date", airDate?.toString())
            put("runtime", runtime)
            put("watched_date", watchedDate?.toString())
        }
        // Only TMDB fields: watched_date is NOT touched
        val tmdbValues = ContentValues().apply {
            put("tmdb_episode_id", tmdbEpisodeId)
            put("title", title)
            put("overview", overview)
            put("air_date", airDate?.toString())
            put("runtime", runtime)
        }

        db.insertWithOnConflict("Episode", null, insertValues, SQLiteDatabase.CONFLICT_IGNORE)
        db.update(
            "Episode",
            tmdbValues,
            "show_id = ? AND season_number = ? AND episode_number = ?",
            whereArgs
        )
    }

    val isWatched get() = watchedDate != null
}

private fun Cursor.getIntOrNull(column: String): Int? {
    val i = getColumnIndexOrThrow(column)
    return if (isNull(i)) null else getInt(i)
}

private fun Cursor.toEpisode() = Episode(
    showId = getInt(getColumnIndexOrThrow("show_id")),
    seasonNumber = getInt(getColumnIndexOrThrow("season_number")),
    episodeNumber = getInt(getColumnIndexOrThrow("episode_number")),
    tmdbEpisodeId = getIntOrNull("tmdb_episode_id"),
    title = getString(getColumnIndexOrThrow("title")) ?: "",
    overview = getString(getColumnIndexOrThrow("overview")) ?: "",
    airDate = getString(getColumnIndexOrThrow("air_date")).toLocalDateOrNull(),
    runtime = getIntOrNull("runtime"),
    watchedDate = getString(getColumnIndexOrThrow("watched_date")).toLocalDateOrNull()
)

fun insertEpisodes(context: Context, episodes: List<Episode>): Boolean {
    if (episodes.isEmpty()) return true
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.writableDatabase
    var ok = false

    try {
        db.beginTransaction()
        try {
            episodes.forEach { it.upsertInto(db) }
            db.setTransactionSuccessful()
            ok = true
        } finally {
            db.endTransaction()
        }
    } catch (e: Exception) {
        Log.e("Database", "Error inserting episodes", e)
    } finally {
        db.close()
    }
    return ok
}

fun getEpisode(context: Context, showId: Int, seasonNumber: Int, episodeNumber: Int): Episode? {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    var theEpisode: Episode? = null

    if (showId == 0) {
        Log.e("Database", "Error getting user info: showId is 0")
        return null
    }

    val query = """
            SELECT * FROM Episode
            WHERE show_id = ? AND season_number = ? AND episode_number = ?
        """
    val selectionArgs =
        arrayOf(showId.toString(), seasonNumber.toString(), episodeNumber.toString())

    try {
        db.rawQuery(query, selectionArgs).use { cursor ->
            if (cursor.moveToFirst()) theEpisode = cursor.toEpisode()
        }
        Log.d("Database", "Successful Mission: Getting episode info")
    } catch (e: Exception) {
        Log.e("Database", "Error getting episode", e)
    } finally {
        db.close()
    }
    return theEpisode
}

fun getEpisode(context: Context, tmdbEpisodeId: Int): Episode? {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    var theEpisode: Episode? = null

    if (tmdbEpisodeId == 0) {
        Log.e("Database", "Error getting episode info: tmdb_episode_id is 0")
        return null
    }

    val query = """
        SELECT * FROM Episode
        WHERE tmdb_episode_id = ?
    """.trimIndent()
    val selectionArgs = arrayOf(tmdbEpisodeId.toString())

    try {
        db.rawQuery(query, selectionArgs).use { cursor ->
            if (cursor.moveToFirst()) theEpisode = cursor.toEpisode()
        }
    } catch (e: Exception) {
        Log.e("Database", "Error getting episode", e)
    } finally {
        db.close()
    }
    return theEpisode
}

fun getEpisodesOfSeason(context: Context, showId: Int, seasonNumber: Int): List<Episode> {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    val episodes = mutableListOf<Episode>()

    val query = """
        SELECT * FROM Episode
        WHERE show_id = ? AND season_number = ?
        ORDER BY episode_number
    """.trimIndent()
    val selectionArgs = arrayOf(showId.toString(), seasonNumber.toString())

    try {
        db.rawQuery(query, selectionArgs).use { cursor ->
            while (cursor.moveToNext()) episodes.add(cursor.toEpisode())
        }
    } catch (e: Exception) {
        Log.e("Database", "Error getting episodes of season", e)
    } finally {
        db.close()
    }
    return episodes
}

fun setEpisodeWatched(
    context: Context, showId: Int, seasonNumber: Int, episodeNumber: Int
): Boolean {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.writableDatabase
    var ok = false

    try {
        val values = ContentValues().apply { put("watched_date", LocalDate.now().toString()) }
        val rows = db.update(
            "Episode",
            values,
            "show_id = ? AND season_number = ? AND episode_number = ?",
            arrayOf(showId.toString(), seasonNumber.toString(), episodeNumber.toString())
        )
        ok = rows > 0
    } catch (e: Exception) {
        Log.e("Database", "Error setting episode watched", e)
    } finally {
        db.close()
    }
    return ok
}

fun setEpisodeUnwatched(
    context: Context, showId: Int, seasonNumber: Int, episodeNumber: Int
): Boolean {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.writableDatabase
    var ok = false
    try {
        val values = ContentValues().apply { putNull("watched_date") }
        ok = db.update(
            "Episode",
            values,
            "show_id = ? AND season_number = ? AND episode_number = ?",
            arrayOf(showId.toString(), seasonNumber.toString(), episodeNumber.toString())
        ) > 0
    } catch (e: Exception) {
        Log.e("Database", "Error setting episode unwatched", e)
    } finally {
        db.close()
    }
    return ok
}

fun setSeasonWatched(context: Context, showId: Int, seasonNumber: Int): Boolean {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.writableDatabase
    var ok = false

    try {
        val values = ContentValues().apply { put("watched_date", LocalDate.now().toString()) }
        val where = "show_id = ? AND season_number = ? AND watched_date IS NULL"
        db.update("Episode", values, where, arrayOf(showId.toString(), seasonNumber.toString()))
        ok = true
    } catch (e: Exception) {
        Log.e("Database", "Error setting season watched", e)
    } finally {
        db.close()
    }
    return ok
}

fun setSeasonUnwatched(context: Context, showId: Int, seasonNumber: Int): Boolean {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.writableDatabase
    var ok = false
    try {
        val values = ContentValues().apply { putNull("watched_date") }
        db.update(
            "Episode",
            values,
            "show_id = ? AND season_number = ?",
            arrayOf(showId.toString(), seasonNumber.toString())
        )
        ok = true
    } catch (e: Exception) {
        Log.e("Database", "Error setting season unwatched", e)
    } finally {
        db.close()
    }
    return ok
}

fun countWatchedEpisodes(context: Context, showId: Int, seasonNumber: Int? = null): Int {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    var count = 0

    val query: String
    val args: Array<String>
    if (seasonNumber == null) {
        query = "SELECT COUNT(*) FROM Episode WHERE show_id = ? AND watched_date IS NOT NULL"
        args = arrayOf(showId.toString())
    } else {
        query =
            "SELECT COUNT(*) FROM Episode WHERE show_id = ? AND season_number = ? AND watched_date IS NOT NULL"
        args = arrayOf(showId.toString(), seasonNumber.toString())
    }

    try {
        db.rawQuery(query, args).use { cursor ->
            if (cursor.moveToFirst()) count = cursor.getInt(0)
        }
    } catch (e: Exception) {
        Log.e("Database", "Error counting watched episodes", e)
    } finally {
        db.close()
    }
    return count
}