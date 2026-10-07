package com.diegogmd.filmfollower.model

import org.threeten.bp.LocalDate

data class MultiSearchResponse(
    val page: Int,
    val results: List<MultiSearchResult>,
    val totalPages: Int,
    val totalResults: Int
)

data class MultiSearchResult(
    val id: Int,
    val media_type: String, // "movie", "tv", or "person"
    val title: String? = null, // movies
    val name: String? = null, // tv shows
    val poster_path: String? = null,
    val release_date: String? = null, // movies
    val first_air_date: String? = null, // tv
    val vote_average: Double? // TMDB's average rating out of 10
) {
    val displayTitle: String get() = title ?: name ?: "Unknown"
    val displayMediaType: String get() = media_type ?: "Unknown"
}

data class FilmDetailsResponse(
    val id: Int,
    val title: String,
    val original_title: String,
    val overview: String,
    val release_date: String? = null,
    val runtime: Int = 0,
    val vote_average: Double,
    val poster_path: String? = null,
    val status: String,
    val genres: List<GenreDetailsResponse>? = null
) {
    fun toFilm(): Film {
        return Film(
            filmId = id,
            title = title,
            originalTitle = original_title,
            overview = overview,
            releaseDate = if (release_date != null) {
                LocalDate.parse(release_date)
            } else {
                LocalDate.of(9999, 12, 31)
            },
            runtime = runtime ?: 0,
            posterPath = poster_path ?: "",
            tmdbStatus = status,
            tmdbLastSynced = LocalDate.now(),
            rating = Math.round(vote_average * 10) / 10.0,
            watchStatus = "watchlist",// default value
            watchedDate = null,
            timesWatched = 0,
            addedAt = LocalDate.now(),
            genres = genres.orEmpty().map { it.toGenre() }
        )
    }
}

data class TvShowDetailsResponse(
    val show_id: Int = 0,
    val title: String = "",
    val original_title: String? = "",
    val overview: String = "",
    val first_air_date: String? = null,
    val number_of_seasons: Int,
    val number_of_episodes: Int,
    var rating: Double = 0.0, // from 0 to 10, default is 0
    var poster_path:  String? = null,
    var status: String = ""
) {
    fun toTvShow(): TvShow {
        return TvShow(
            showId = show_id,
            title = title,
            originalTitle = original_title,
            overview = overview,
            firstAirDate = if (first_air_date != null) {
                LocalDate.parse(first_air_date)
            } else {
                LocalDate.of(9999, 12, 31)
            },
            numberOfSeasons = number_of_seasons ?: 0,
            numberOfEpisodes = number_of_episodes ?: 0,
            rating = Math.round(rating * 10) / 10.0, // from 0 to 10, default is 0
            posterPath = poster_path ?: "",
            tmdbStatus = status,
            tmdbLastSynced = LocalDate.now(),
            watchStatus = "watchlist",// default value
            addedAt = LocalDate.now()
        )
    }
}

data class GenreDetailsResponse(
    val id: Int,
    val name: String
) {
    fun toGenre(): Genre {
        return Genre(
            genreId = id,
            name = name
        )
    }
}

//data class SeasonDetailsResponse(
//    val showId: Int = 0,
//    val title: String = "",
//    val originalTitle: String? = "",
//    val overview: String? = "",
//    val firstAirDate: String? = null,
//    val numberOfSeasons: Int,
//    val numberOfEpisodes: Int,
//    var rating: Double = 0.0, // from 0 to 10, default is 0
//    var posterPath:  String? = null,
//    var tmdbStatus: String = "",
//)

//data class EpisodeDetailsResponse(
//    val showId: Int = 0,
//    val title: String = "",
//    val originalTitle: String? = "",
//    val overview: String? = "",
//    val firstAirDate: String? = null,
//    val numberOfSeasons: Int,
//    val numberOfEpisodes: Int,
//    var rating: Double = 0.0, // from 0 to 10, default is 0
//    var posterPath:  String? = null,
//    var tmdbStatus: String = "",
//)