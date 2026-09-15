package com.diegogmd.filmfollower.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FilmFillowerDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "FilmFollower.sqlite"
        const val DATABASE_VERSION = 1

        @Volatile
        private var INSTANCE: FilmFillowerDatabase? = null

        fun getInstance(context: Context): FilmFillowerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = FilmFillowerDatabase(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Table creation statements
        val createFilmTable = """
            CREATE TABLE IF NOT EXISTS Film (
                film_id INTEGER PRIMARY KEY,
                title TEXT NOT NULL,
                original_title TEXT,
                overview TEXT,
                release_date TEXT,
                runtime INTEGER,
                poster_path TEXT,
                tmdb_status TEXT,
                tmdb_last_synced TIMESTAMP,
                rating INTEGER CHECK (rating BETWEEN 1 AND 10),
                watch_status TEXT NOT NULL DEFAULT 'wishlist' CHECK (watch_status IN ('wishlist','watching','seen')),
                watched_date TEXT,
                times_watched INTEGER NOT NULL DEFAULT 0,
                added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """.trimIndent()

        val createGenreTable = """
            CREATE TABLE IF NOT EXISTS Genre (
                genre_id INTEGER PRIMARY KEY,
                name TEXT NOT NULL UNIQUE
            );
        """.trimIndent()

        val createFilmGenreTable = """
            CREATE TABLE FilmGenre (
                film_id INTEGER NOT NULL,
                genre_id INTEGER NOT NULL,
                PRIMARY KEY (film_id, genre_id),
                FOREIGN KEY (film_id)  REFERENCES film(film_id) ON DELETE CASCADE,
                FOREIGN KEY (genre_id) REFERENCES genre(genre_id) ON DELETE CASCADE
            );
        """.trimIndent()

        val createTvShowTable = """
            CREATE TABLE TvShow (
                show_id INTEGER PRIMARY KEY,
                title TEXT NOT NULL,
                original_title TEXT,
                overview TEXT,
                first_air_date TEXT,
                number_of_seasons INTEGER NOT NULL DEFAULT 0,
                number_of_episodes INTEGER NOT NULL DEFAULT 0,
                poster_path TEXT,
                tmdb_status TEXT,
                tmdb_last_synced TIMESTAMP,
                watch_status TEXT NOT NULL DEFAULT 'wishlist' CHECK (watch_status IN ('wishlist','watching','completed','dropped')),
                rating INTEGER CHECK (rating BETWEEN 1 AND 10),
                added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """.trimIndent()

        val createTvShowGenreTable = """
            CREATE TABLE TvShowGenre (
                show_id INTEGER NOT NULL,
                genre_id INTEGER NOT NULL,
                PRIMARY KEY (show_id, genre_id),
                FOREIGN KEY (show_id)  REFERENCES tv_show(show_id) ON DELETE CASCADE,
                FOREIGN KEY (genre_id) REFERENCES genre(genre_id) ON DELETE CASCADE
            );
        """.trimIndent()

        val createSeasonTable = """
            CREATE TABLE Season (
                show_id INTEGER NOT NULL,
                season_number INTEGER NOT NULL,
                tmdb_season_id INTEGER UNIQUE,
                name TEXT,
                overview TEXT,
                air_date TEXT,
                episode_count INTEGER,
                poster_path TEXT,
                PRIMARY KEY (show_id, season_number),
                FOREIGN KEY (show_id) REFERENCES tv_show(show_id) ON DELETE CASCADE
            );
        """.trimIndent()

        val createEpisodeTable = """
            CREATE TABLE Episode (
                show_id INTEGER NOT NULL,
                season_number INTEGER NOT NULL,
                episode_number INTEGER NOT NULL,
                tmdb_episode_id INTEGER UNIQUE,
                title TEXT,
                overview TEXT,
                air_date TEXT,
                runtime INTEGER,
                watched_date TEXT, -- If i watched the ep this won't be null
                PRIMARY KEY (show_id, season_number, episode_number),
                FOREIGN KEY (show_id, season_number) REFERENCES season(show_id, season_number) ON DELETE CASCADE
            );
        """.trimIndent()

        db?.apply {
            execSQL(createFilmTable)
            execSQL(createFilmGenreTable)
            execSQL(createGenreTable)
            execSQL(createTvShowTable)
            execSQL(createTvShowGenreTable)
            execSQL(createSeasonTable)
            execSQL(createEpisodeTable)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.apply {
            execSQL("DROP TABLE IF EXISTS Film")
            execSQL("DROP TABLE IF EXISTS FilmGenre")
            execSQL("DROP TABLE IF EXISTS Genre")
            execSQL("DROP TABLE IF EXISTS TvShow")
            execSQL("DROP TABLE IF EXISTS TvShowGenre")
            execSQL("DROP TABLE IF EXISTS Season")
            execSQL("DROP TABLE IF EXISTS Episode")
            onCreate(this)
        }
    }
}