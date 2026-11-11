package com.diegogmd.filmfollower.model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.diegogmd.filmfollower.data.local.FilmFillowerDatabase
import com.diegogmd.filmfollower.data.repository.SearchRepository
import com.diegogmd.filmfollower.model.responses.toLocalDateOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import kotlin.coroutines.cancellation.CancellationException

class Season (
    val showId: Int,
    val seasonNumber: Int,
    val tmdbSeasonId: Int,
    val name: String,
    val overview: String,
    val airDate: LocalDate? = null,
    val episodeCount: Int,
    val posterPath: String? = null,
) {
    fun insertNewSeason(context: Context): Boolean {
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase
        var ok = false

        try {
            val contentValues = ContentValues().apply {
                put("show_id", showId)
                put("season_number", seasonNumber)
                put("tmdb_season_id", tmdbSeasonId)
                put("name", name)
                put("overview", overview)
                if (airDate != null) {
                    put("air_date", airDate.toString())
                } else {
                    putNull("air_date")
                }
                put("episode_count", episodeCount)
                if (posterPath != null) {
                    put("poster_path", posterPath)
                } else {
                    putNull("poster_path")
                }
            }
            db.beginTransaction()
            try {
                val rowId = db.insert("Season", null, contentValues)
                if (rowId == -1L) throw IllegalStateException("Season insert returned -1")
                db.setTransactionSuccessful()
                ok = true
            } finally {
                db.endTransaction()
            }
        } catch (e: Exception) {
            Log.e("Database", "Error inserting new season", e)
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
                put("season_number", seasonNumber)
                put("tmdb_season_id", tmdbSeasonId)
                put("name", name)
                put("overview", overview)
                if (airDate != null) {
                    put("air_date", airDate.toString())
                } else {
                    putNull("air_date")
                }
                put("episode_count", episodeCount)
                if (posterPath != null) {
                    put("poster_path", posterPath)
                } else {
                    putNull("poster_path")
                }
            }
            db.beginTransaction()
            try {
                db.update("Season", values, "show_id = ? AND season_number = ?", arrayOf(showId.toString(), seasonNumber.toString()))
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        } catch (e: Exception) {
            Log.e("Database", "Error syncing season", e)
        } finally {
            db.close()
        }
    }

    internal fun upsertInto(db: SQLiteDatabase) {
        val insertValues = ContentValues().apply {
            put("show_id", showId)
            put("season_number", seasonNumber)
            put("tmdb_season_id", tmdbSeasonId)
            put("name", name)
            put("overview", overview)
            put("air_date", airDate?.toString())
            put("episode_count", episodeCount)
            put("poster_path", posterPath)
        }
        // Only TMDB fields: watched_date is NOT touched
        val tmdbValues = ContentValues().apply {
            put("tmdb_season_id", tmdbSeasonId)
            put("name", name)
            put("overview", overview)
            put("air_date", airDate?.toString())
            put("episode_count", episodeCount)
            put("poster_path", posterPath)
        }
        db.insertWithOnConflict("Season", null, insertValues, SQLiteDatabase.CONFLICT_IGNORE)
        db.update(
            "Season",
            tmdbValues,
            "show_id = ? AND season_number = ?",
            arrayOf(showId.toString(), seasonNumber.toString())
        )
    }
}

fun insertSeasons(context: Context, seasons: List<Season>): Boolean {
    if (seasons.isEmpty()) return true
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.writableDatabase
    var ok = false

    try {
        db.beginTransaction()
        try {
            seasons.forEach { it.upsertInto(db) }
            db.setTransactionSuccessful()
            ok = true
        } finally {
            db.endTransaction()
        }
    } catch (e: Exception) {
        Log.e("Database", "Error inserting seasons", e)
    } finally {
        db.close()
    }
    return ok
}

private fun Cursor.toSeason() = Season(
    showId = getInt(getColumnIndexOrThrow("show_id")),
    seasonNumber = getInt(getColumnIndexOrThrow("season_number")),
    tmdbSeasonId = getInt(getColumnIndexOrThrow("tmdb_season_id")),
    name = getString(getColumnIndexOrThrow("name")) ?: "",
    overview = getString(getColumnIndexOrThrow("overview")) ?: "",
    airDate = getString(getColumnIndexOrThrow("air_date")).toLocalDateOrNull(),
    episodeCount = getInt(getColumnIndexOrThrow("episode_count")),
    posterPath = getString(getColumnIndexOrThrow("poster_path"))
)

fun getSeason(context: Context, showId: Int, seasonNumber: Int): Season? {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    var theSeason: Season? = null

    if (showId == 0){
        Log.e("Database", "Error getting season info: showId is 0")
        return null
    }

    val query = """
        SELECT * FROM Season
        WHERE show_id = ? AND season_number = ?
    """.trimIndent()
    val selectionArgs = arrayOf(showId.toString(), seasonNumber.toString())

    try {
        db.rawQuery(query, selectionArgs).use { cursor ->
            if (cursor.moveToFirst()) theSeason = cursor.toSeason()
        }
        Log.d("Database", "Successful Mission: Getting season info")
    } catch (e: Exception) {
        Log.e("Database", "Error getting season", e)
    } finally {
        db.close()
    }
    return theSeason
}

fun getAllSeasons(context: Context, showId: Int): List<Season> {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    val seasons = mutableListOf<Season>()

    val query = """
        SELECT * FROM Season
        WHERE show_id = ?
        ORDER BY season_number
    """.trimIndent()
    val selectionArgs = arrayOf(showId.toString())

    try {
        db.rawQuery(query, selectionArgs).use { cursor ->
            while (cursor.moveToNext()) seasons.add(cursor.toSeason())
        }
    } catch (e: Exception) {
        Log.e("Database", "Error getting seasons of show", e)
    } finally {
        db.close()
    }
    return seasons
}

fun eraseSeason(context: Context, showId: Int, seasonNumber: Int): Int? {
    val season: Season? = getSeason(context, showId, seasonNumber)
    if (season == null) {
        Log.e("Database", "Error getting season")
        return null
    } else {
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase

        try {
            db.execSQL(
                "DELETE FROM Season WHERE show_id = ? AND season_number = ?",
                arrayOf(showId.toString(), seasonNumber.toString())
            )
            Log.d(
                "Database",
                "Season from tv show $showId and number $seasonNumber deleted successfully"
            )
        } catch (e: Exception) {
            Log.e("Database", "Error deleting season", e)
            return 0
        } finally {
            db.close()
        }

        return 1
    }
}

fun eraseAllSeasons(context: Context, showId: Int): Int? {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.writableDatabase

    val tvShow: TvShow? = getTvShow(context, showId)
    if (tvShow == null || showId == 0) {
        Log.e("Database", "Error getting tv show to delete its seasons")
        return null
    }

    try {
        db.execSQL(
            "DELETE FROM Season WHERE show_id = ?",
            arrayOf(showId.toString())
        )
        Log.d("Database", "Seasons from tv show $showId deleted successfully")
    } catch (e: Exception) {
        Log.e("Database", "Error deleting all seasons", e)
        return 0
    } finally {
        db.close()
    }

    eraseAllEpisodes(context, showId)
    return 1
}

suspend fun syncSeasons(
    context: Context,
    showId: Int,
    repository: SearchRepository,
): Boolean {
    val fresh = try {
        repository.getSeasons(showId)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        return false // offline/API error: keep old data and it retries next time
    }

    withContext(Dispatchers.IO) { insertSeasons(context, fresh) }
    return true
}