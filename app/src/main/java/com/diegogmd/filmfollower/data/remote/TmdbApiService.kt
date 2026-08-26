package com.diegogmd.filmfollower.data.local.remote

import com.diegogmd.filmfollower.model.FilmDetailsResponse
import com.diegogmd.filmfollower.model.MultiSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface TmdbApiService {
    @GET("search/multi")
    suspend fun searchMulti(
        @Query("query") query: String,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): MultiSearchResponse

    // time_window: "day" or "week". "day" = closest thing TMDB has to
    // "most seen right now". Response shape matches search/multi:
    // page, results (with media_type per item), total_pages, total_results.
    @GET("trending/all/{time_window}")
    suspend fun getTrendingAll(
        @Path("time_window") timeWindow: String = "day",
        @Query("language") language: String = "en-US"
    ): MultiSearchResponse

    @GET("movie/{film_id}")
    suspend fun getFilm(
        @Path("film_id") filmId: Int,
        @Query("language") language: String = "en-US"
    ): FilmDetailsResponse

//    @GET("tv/{tvshow_id}")
//    suspend fun getTVShow(
//        @Path("tvshow_id") showId: Int,
//        @Query("language") language: String = "en-US"
//    ): TvShowDetailsResponse
}