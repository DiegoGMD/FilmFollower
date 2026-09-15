package com.diegogmd.filmfollower.data.repository

import com.diegogmd.filmfollower.model.MultiSearchResult
import com.diegogmd.filmfollower.model.Film
import com.diegogmd.filmfollower.data.local.remote.TmdbApiService

class SearchRepository(private val api: TmdbApiService) {
    suspend fun search(query: String): List<MultiSearchResult> {
        if (query.isBlank()) return emptyList()
        return api.searchMulti(query)
            .results
            .filter { it.media_type == "movie" || it.media_type == "tv" }
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

//    suspend fun getTVShow(id: Int): List<MultiSearchResult> {
//        return api.getFilm(filmId = id).toFilm()
//    }
}