package com.diegogmd.filmfollower.data.local.remote

import com.diegogmd.filmfollower.model.FilmDetailsResponse
import com.diegogmd.filmfollower.model.MultiSearchResponse
import com.diegogmd.filmfollower.model.TvShowDetailsResponse
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

    @GET("trending/all/{time_window}")
    suspend fun getTrendingAll(
        @Path("time_window") timeWindow: String = "day",
    ): MultiSearchResponse

    @GET("movie/{film_id}")
    suspend fun getFilm(
        @Path("film_id") filmId: Int,
    ): FilmDetailsResponse

    @GET("tv/{tvshow_id}")
    suspend fun getTvShow(
        @Path("tvshow_id") showId: Int,
    ): TvShowDetailsResponse
}