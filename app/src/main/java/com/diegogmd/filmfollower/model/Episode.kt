package com.diegogmd.filmfollower.model

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.diegogmd.filmfollower.data.local.FilmFillowerDatabase
import org.threeten.bp.LocalDate

class Episode(
    val showId: Int,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val tmdbEpisodeId: Int?,
    val title: String,
    val overview: String,
    val airDate: LocalDate,
    val runtime: Int,
    val watchedDate: LocalDate?
) {
    fun insertNewEpisode(context: Context) {
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase

        try {
            val contentValues = ContentValues().apply {
                put("show_id", showId)
                put("season_number", seasonNumber)
                put("episode_number", episodeNumber)
                put("tmdb_episode_id", tmdbEpisodeId)
                put("title", title)
                put("overview", overview)
                put("air_date", airDate.toString())
                put("runtime", runtime)
                put("watched_date", watchedDate.toString())
            }
            db.insert("Episode", null, contentValues)

        } catch (e: Exception) {
            Log.e("Database", "Error inserting new episode", e)
        } finally {
            db.close()
        }
    }
}

fun getEpisode(context: Context, showId: Int, seasonNumber: Int, episodeNumber: Int): Episode? {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.writableDatabase
    var theEpisode: Episode? = null

    if(showId == 0){
        Log.e("Database", "Error getting user info: showId is 0")
        return null
    }

    val query = """
            SELECT * FROM Episode
            WHERE show_id = ? AND season_number = ? AND episode_number = ?
        """
    val selectionArgs = arrayOf(
        showId.toString(),
        seasonNumber.toString(),
        episodeNumber.toString()
    )

    try {
        val cursor = db.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {

            val dateStr1 = cursor.getString(cursor.getColumnIndexOrThrow("air_date"))
            val dateStr2 = cursor.getString(cursor.getColumnIndexOrThrow("watched_date"))

            theEpisode = Episode(
                showId = cursor.getInt(cursor.getColumnIndexOrThrow("show_id")),
                seasonNumber = cursor.getInt(cursor.getColumnIndexOrThrow("season_number")),
                episodeNumber = cursor.getInt(cursor.getColumnIndexOrThrow("episode_number")),
                tmdbEpisodeId = cursor.getInt(cursor.getColumnIndexOrThrow("tmdb_episode_id")),
                title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                overview = cursor.getString(cursor.getColumnIndexOrThrow("overview")),
                airDate = LocalDate.parse(dateStr1),
                runtime = cursor.getInt(cursor.getColumnIndexOrThrow("runetime")),
                watchedDate = LocalDate.parse(dateStr2)
            )
        }
        cursor.close()
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
    val db = dbHelper.writableDatabase
    var theEpisode: Episode? = null

    if(tmdbEpisodeId == 0){
        Log.e("???", "Error getting user info: tmdb_episode_id is 0")
        return null
    }

    val query = """
            SELECT * FROM Episode
            WHERE tmdb_episode_id = ?
        """
    val selectionArgs = arrayOf(
        tmdbEpisodeId.toString()
    )

    try {
        val cursor = db.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {

            val dateStr1 = cursor.getString(cursor.getColumnIndexOrThrow("watched_date"))
            val dateStr2 = cursor.getString(cursor.getColumnIndexOrThrow("watched_date"))

            theEpisode = Episode(
                showId = cursor.getInt(cursor.getColumnIndexOrThrow("show_id")),
                seasonNumber = cursor.getInt(cursor.getColumnIndexOrThrow("season_number")),
                episodeNumber = cursor.getInt(cursor.getColumnIndexOrThrow("episode_number")),
                tmdbEpisodeId = cursor.getInt(cursor.getColumnIndexOrThrow("tmdb_episode_id")),
                title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                overview = cursor.getString(cursor.getColumnIndexOrThrow("overview")),
                airDate = LocalDate.parse(dateStr1),
                runtime = cursor.getInt(cursor.getColumnIndexOrThrow("runetime")),
                watchedDate = LocalDate.parse(dateStr2)
            )
        }
        cursor.close()
        Log.d("Database", "Successful Mission: Getting episode info")
    } catch (e: Exception) {
        Log.e("Database", "Error getting episode", e)
    } finally {
        db.close()
    }
    return theEpisode
}


