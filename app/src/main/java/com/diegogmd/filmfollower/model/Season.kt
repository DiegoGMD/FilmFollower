package com.diegogmd.filmfollower.model

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.diegogmd.filmfollower.data.local.FilmFillowerDatabase
import org.threeten.bp.LocalDate

class Season (
    val show_id: Int,
    val season_number: Int,
    val tmdb_season_id: Int,
    val name: String,
    val overview: String,
    val air_date: LocalDate,
    val episode_count: Int,
    val poster_path: String,
) {
    fun insertNewSeason(context: Context) {
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase

        try {
            val contentValues = ContentValues().apply {
                put("show_id", show_id)
                put("season_number", season_number)
                put("tmdb_season_id", tmdb_season_id)
                put("name", name)
                put("overview", overview)
                put("air_date", air_date.toString())
                put("episode_count", episode_count)
                put("poster_path", poster_path)
            }
            db.insert("Season", null, contentValues)

        } catch (e: Exception) {
            Log.e("Database", "Error inserting new season", e)
        } finally {
            db.close()
        }
    }
}

fun getSeason(context: Context, showId: Int, seasonNumber: Int): Season? {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.readableDatabase
    var theSeason: Season? = null

    if(showId == 0){
        Log.e("Database", "Error getting season info: showId is null or 0")
        return null
    }

    val query = """
        SELECT * FROM Season
        WHERE show_id = ? and season_number = ?
    """
    val selectionArgs = arrayOf(
        showId.toString(),
        seasonNumber.toString()
    )

    try {
        val cursor = db.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {
            val dateStr1 = cursor.getString(cursor.getColumnIndexOrThrow("air_date"))

            theSeason = Season(
                show_id = cursor.getInt(cursor.getColumnIndexOrThrow("show_id")),
                season_number = cursor.getInt(cursor.getColumnIndexOrThrow("season_number")),
                tmdb_season_id = cursor.getInt(cursor.getColumnIndexOrThrow("tmdb_season_id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                overview = cursor.getString(cursor.getColumnIndexOrThrow("overview")),
                air_date = LocalDate.parse(dateStr1),
                episode_count = cursor.getInt(cursor.getColumnIndexOrThrow("episode_count")),
                poster_path = cursor.getString(cursor.getColumnIndexOrThrow("poster_path"))
            )
        }
        cursor.close()
        Log.d("Database", "Successful Mission: Getting season info")
    } catch (e: Exception) {
        Log.e("Database", "Error getting season", e)
    } finally {
        db.close()
    }
    return theSeason
}