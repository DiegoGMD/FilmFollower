package com.diegogmd.filmfollower.data.repository

import com.diegogmd.filmfollower.model.MultiSearchResult
import com.diegogmd.filmfollower.model.Film
import com.diegogmd.filmfollower.data.local.remote.TmdbApiService
import com.diegogmd.filmfollower.model.TvShow
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class SearchRepository(private val api: TmdbApiService) {
    suspend fun search(query: String): List<MultiSearchResult> = coroutineScope {
        val q = query.trim()
        if (q.isEmpty()) return@coroutineScope emptyList()

        // "wall-e" / "wall e" -> "wall·e" (TMDB titles like WALL·E, BURN·E use a middle dot)
        val dotted = q.replace(Regex("(?<=\\p{L})[-.\\s](?=\\p{L}{1,2}$)"), "·")

        val original = async { api.searchMulti(q).results }
        val variant = if (dotted != q) async { api.searchMulti(dotted).results } else null

        (variant?.await().orEmpty() + original.await())
            .filter { it.media_type == "movie" || it.media_type == "tv" }
            .distinctBy { it.media_type to it.id }
    }

    /**
     * Top [limit] trending movies/TV shows (people excluded), then
     * re-sorted alphabetically by display title for the home screen.
     * The "top N" cut happens BEFORE the alphabetical sort, so you're
     * always getting the N most popular, just displayed A-Z.
     */
    suspend fun getTopTrending(limit: Int = 10): List<MultiSearchResult> {
        return api.getTrendingAll()
            .results
            .filter { it.media_type == "movie" || it.media_type == "tv" }
            .take(limit)
            .sortedBy { it.displayTitle }
    }

    suspend fun getFilm(id: Int): Film {
        return api.getFilm(filmId = id).toFilm()
    }

    suspend fun getTvShow(id: Int): TvShow {
        return api.getTvShow(showId = id).toTvShow()
    }
}