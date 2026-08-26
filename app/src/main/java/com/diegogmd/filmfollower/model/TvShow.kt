package com.diegogmd.filmfollower.model

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.diegogmd.filmfollower.data.local.FilmFillowerDatabase
import org.threeten.bp.LocalDate

class TvShow (
    val showId: Int = 0,
    val title: String = "",
    val original_title: String? = "",
    val overview: String? = "",
    val first_air_date: LocalDate, // YYYY-MM-DD
    val number_of_seasons: Int,
    val number_of_episodes: Int,
    var rating: Double = 0.0, // from 0 to 10, default is 0
    var poster_path: String? = "",
    var tmdb_status: String = "",
    var tmdb_last_synced: LocalDate,
    var watch_status: String = "",
    val added_at: LocalDate
) {
    fun insertNewTvShow(context: Context) {
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase

        try {
            val contentValues = ContentValues().apply {
                put("show_id", showId)
                put("title", title)
                put("original_title", original_title)
                put("overview", overview)
                put("first_air_date", first_air_date.toString())
                put("number_of_seasons", number_of_seasons)
                put("number_of_episodes", number_of_episodes)
                put("rating", rating)
                put("poster_path", poster_path)
                put("tmdb_status", tmdb_status)
                put("tmdb_last_synced", tmdb_last_synced.toString())
                put("watch_status", watch_status)
                put("added_at", added_at.toString())
            }
            db.insert("TvShow", null, contentValues)

        } catch (e: Exception) {
            Log.e("Database", "Error inserting new episode", e)
        } finally {
            db.close()
        }
    }
}

fun getTvShow(context: Context, showId: Int): TvShow? {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    var theTvShow: TvShow? = null

    if(showId == 0){
        Log.e("Database", "Error getting tvshow info: showId is null or 0")
        return null
    }

    val query = """
        SELECT * FROM TvShow
        WHERE show_id = ?
    """
    val selectionArgs = arrayOf(
        showId.toString()
    )

    try {
        val cursor = db.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {

            val dateStr1 = cursor.getString(cursor.getColumnIndexOrThrow("first_air_date"))
            val dateStr2 = cursor.getString(cursor.getColumnIndexOrThrow("tmdb_last_synced"))
            val dateStr3 = cursor.getString(cursor.getColumnIndexOrThrow("added_at"))

            theTvShow = TvShow(
                showId = cursor.getInt(cursor.getColumnIndexOrThrow("show_id")),
                title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                original_title = cursor.getString(cursor.getColumnIndexOrThrow("original_title")),
                overview = cursor.getString(cursor.getColumnIndexOrThrow("overview")),
                first_air_date = LocalDate.parse(dateStr1),
                number_of_seasons = cursor.getInt(cursor.getColumnIndexOrThrow("number_of_seasons")),
                number_of_episodes = cursor.getInt(cursor.getColumnIndexOrThrow("number_of_episodes")),
                rating = cursor.getDouble(cursor.getColumnIndexOrThrow("rating")),
                poster_path = cursor.getString(cursor.getColumnIndexOrThrow("poster_path")),
                tmdb_status = cursor.getString(cursor.getColumnIndexOrThrow("tmdb_status")),
                tmdb_last_synced = LocalDate.parse(dateStr2),
                watch_status = cursor.getString(cursor.getColumnIndexOrThrow("watch_status")),
                added_at = LocalDate.parse(dateStr3)
            )
        }
        cursor.close()
        Log.d("Database", "Successful Mission: Getting tvshow info")
    } catch (e: Exception) {
        Log.e("Database", "Error getting tvshow", e)
    } finally {
        db.close()
    }
    return theTvShow
}