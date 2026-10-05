package com.diegogmd.filmfollower.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FilmFillowerDatabase(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

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

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
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
                rating REAL CHECK (rating BETWEEN 0 AND 10),
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
                FOREIGN KEY (film_id)  REFERENCES Film(film_id) ON DELETE CASCADE,
                FOREIGN KEY (genre_id) REFERENCES Genre(genre_id) ON DELETE CASCADE
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
                rating REAL CHECK (rating BETWEEN 0 AND 10),
                added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """.trimIndent()

        val createTvShowGenreTable = """
            CREATE TABLE TvShowGenre (
                show_id INTEGER NOT NULL,
                genre_id INTEGER NOT NULL,
                PRIMARY KEY (show_id, genre_id),
                FOREIGN KEY (show_id)  REFERENCES TvShow(show_id) ON DELETE CASCADE,
                FOREIGN KEY (genre_id) REFERENCES Genre(genre_id) ON DELETE CASCADE
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
                FOREIGN KEY (show_id) REFERENCES TvShow(show_id) ON DELETE CASCADE
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
                FOREIGN KEY (show_id, season_number) REFERENCES Season(show_id, season_number) ON DELETE CASCADE
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

        db?.let { seedGenres(it) }
    }

    private fun seedGenres(db: SQLiteDatabase) {
        val genres = mapOf(
            12 to "Adventure",
            14 to "Fantasy",
            16 to "Animation",
            18 to "Drama",
            27 to "Horror",
            28 to "Action",
            35 to "Comedy",
            36 to "History",
            37 to "Western",
            53 to "Thriller",
            80 to "Crime",
            99 to "Documentary",
            878 to "Science Fiction",
            9648 to "Mystery",
            10402 to "Music",
            10749 to "Romance",
            10751 to "Family",
            10752 to "War",
            10759 to "Action & Adventure",
            10762 to "Kids",
            10763 to "News",
            10764 to "Reality",
            10765 to "Sci-Fi & Fantasy",
            10766 to "Soap",
            10767 to "Talk",
            10768 to "War & Politics",
            10770 to "TV Movie"
        )
        genres.forEach { (id, name) ->
            val values = ContentValues().apply {
                put("genre_id", id)
                put("name", name)
            }
            db.insertWithOnConflict("Genre", null, values, SQLiteDatabase.CONFLICT_IGNORE)
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