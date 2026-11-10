package com.diegogmd.filmfollower.model.responses

import com.diegogmd.filmfollower.model.Episode
import com.diegogmd.filmfollower.model.Film
import com.diegogmd.filmfollower.model.Genre
import com.diegogmd.filmfollower.model.Season
import com.diegogmd.filmfollower.model.TvShow
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
            releaseDate = if (!release_date.isNullOrBlank()) {
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
    val id: Int = 0,
    val name: String = "",
    val original_name: String? = "",
    val overview: String = "",
    val first_air_date: String? = null,
    val number_of_seasons: Int,
    val number_of_episodes: Int,
    var vote_average: Double = 0.0, // from 0 to 10, default is 0
    var poster_path:  String? = null,
    var status: String = "",
    val genres: List<GenreDetailsResponse>? = null,
    val seasons: List<SeasonSummaryResponse>? = null
) {
    fun toTvShow(): TvShow {
        return TvShow(
            showId = id,
            title = name,
            originalTitle = original_name,
            overview = overview,
            firstAirDate = if (!first_air_date.isNullOrBlank()) {
                LocalDate.parse(first_air_date)
            } else {
                LocalDate.of(9999, 12, 31)
            },
            numberOfSeasons = number_of_seasons ?: 0,
            numberOfEpisodes = number_of_episodes ?: 0,
            rating = Math.round(vote_average * 10) / 10.0, // from 0 to 10, default is 0
            posterPath = poster_path ?: "",
            tmdbStatus = status,
            tmdbLastSynced = LocalDate.now(),
            watchStatus = "watchlist",// default value
            addedAt = LocalDate.now(),
            genres = genres.orEmpty().map { it.toGenre() }
        )
    }
}

data class SeasonSummaryResponse(
    val id: Int,
    val season_number: Int,
    val name: String? = null,
    val overview: String? = null,
    val air_date: String? = null,
    val episode_count:Int? = null,
    val poster_path: String? = null
) {
    fun toSeason(showId: Int) = Season(
        showId = showId,
        seasonNumber = season_number,
        tmdbSeasonId = id,
        name = name ?: "Season $season_number",
        overview = overview ?: "",
        airDate = air_date.toLocalDateOrNull(),
        episodeCount = episode_count ?: 0,
        posterPath = poster_path
    )
}

data class SeasonDetailsResponse(
    val id: Int,
    val season_number: Int,
    val name: String? = null,
    val overview: String? = null,
    val air_date: String? = null,
    val poster_path: String? = null,
    val episodes: List<EpisodeDetailsResponse>? = null
)

data class EpisodeDetailsResponse(
    val id: Int,
    val episode_number: Int,
    val name: String? = null,
    val overview: String? = null,
    val air_date: String? = null,
    val runtime:Int? = null
) {
    fun toEpisode(showId: Int, seasonNumber: Int) = Episode(
        showId = showId,
        seasonNumber = seasonNumber,
        episodeNumber = episode_number,
        tmdbEpisodeId = id,
        title = name ?: "Episode $episode_number",
        overview = overview ?: "",
        airDate = air_date.toLocalDateOrNull(),
        runtime = runtime
    )
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

data class VideoResponse(
    val id: Int,
    val results: List<Video>
)

data class Video(
    val key: String,
    val name: String,
    val site: String,
    val type: String,
    val official: Boolean,
    val published_at: String?
)

fun List<Video>.bestTrailer(): Video? {
    val youtube = filter { it.site == "YouTube" }
    return youtube.firstOrNull { it.type == "Trailer" && it.official }
        ?: youtube.firstOrNull { it.type == "Trailer" }
        ?: youtube.firstOrNull { it.type == "Teaser" }
}

data class CastMember(
    val id: Int,
    val name: String,
    val role: String,
    val photoUrl: String? = null
)

data class CreditsResponse(
    val id: Int,
    val cast: List<CastDto> = emptyList()
)

data class CastDto(
    val id: Int,
    val name: String,
    val character: String?,
    val profile_path: String?,
    val order: Int = 0
)

fun CastDto.toCastMember() = CastMember(
    id = id,
    name = name,
    role = character ?: "",
    photoUrl = profile_path
)

fun String?.toLocalDateOrNull(): LocalDate? {
    if (this.isNullOrBlank()) return null
    return runCatching { LocalDate.parse(this) }.getOrNull()
}