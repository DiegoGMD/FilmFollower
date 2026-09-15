package com.diegogmd.filmfollower.model

import android.content.ContentValues
import android.content.Context
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
    val rating: Double,
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
                put("rating", rating)
                put("watch_status", watchStatus)
                put("watched_date", watchedDate.toString())
                put("times_watched", timesWatched)
                put("added_at", addedAt.toString())
            }
            db.insert("film", null, contentValues)

        } catch (e: Exception) {
            Log.e("Database", "Error inserting new film", e)
        } finally {
            db.close()
        }
    }
}

fun getFilm(context: Context, filmId: Int): Film? {
    val dbHelper = FilmFillowerDatabase(context)
    val db = dbHelper.writableDatabase
    var theFilm: Film? = null

    if(filmId == 0){
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
                watchedDate = LocalDate.parse(dateStr3),
                timesWatched = cursor.getInt(cursor.getColumnIndexOrThrow("times_watched")),
                addedAt = LocalDate.parse(dateStr4)
            )

        }
    } catch (e: Exception) {
        Log.e("Database", "Error getting film", e)
    } finally {
        db.close()
    }

    return theFilm
}