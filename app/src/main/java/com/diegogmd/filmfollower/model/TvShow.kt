package com.diegogmd.filmfollower.model

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.diegogmd.filmfollower.data.local.FilmFillowerDatabase
import org.threeten.bp.LocalDate

class TvShow (
    val showId: Int = 0,
    val title: String = "",
    val originalTitle: String? = "",
    val overview: String = "",
    val firstAirDate: LocalDate, // YYYY-MM-DD
    val numberOfSeasons: Int,
    val numberOfEpisodes: Int,
    var rating: Double?, // from 0 to 10, default is 0
    var posterPath: String? = "",
    var tmdbStatus: String = "",
    var tmdbLastSynced: LocalDate,
    var watchStatus: String = "",
    val addedAt: LocalDate
) {
    fun insertNewTvShow(context: Context) {
        val dbHelper = FilmFillowerDatabase(context)
        val db = dbHelper.writableDatabase

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
                    put("rating", rating.toString())
                } else {
                    putNull("rating")
                }
                put("poster_path", posterPath)
                put("tmdb_status", tmdbStatus)
                put("tmdb_last_synced", tmdbLastSynced.toString())
                put("watch_status", watchStatus)
                put("added_at", addedAt.toString())
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
        Log.d("Database", "Successful Mission: Getting tvshow info")
    } catch (e: Exception) {
        Log.e("Database", "Error getting tvshow", e)
    } finally {
        db.close()
    }
    return theTvShow
}