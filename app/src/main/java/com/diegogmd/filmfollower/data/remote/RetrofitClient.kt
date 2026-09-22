package com.diegogmd.filmfollower.data.local.remote

import com.diegogmd.filmfollower.App
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okio.IOException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val authInterceptor = Interceptor { chain ->
    val token = ApiConfig.getTmdbToken(App.appContext)
        ?: throw IOException(
            "No TMDB API key saved yet — add one in Settings before making requests."
        )

    val request = chain.request().newBuilder()
        .addHeader("Authorization", "Bearer $token")
        .addHeader("accept", "application/json")
        .build()

    chain.proceed(request)
}

val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(authInterceptor)
    .build()

val retrofit = Retrofit.Builder()
    .baseUrl("https://api.themoviedb.org/3/")
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create()) // or Moshi/kotlinx.serialization
    .build()

val tmdbApi: TmdbApiService = retrofit.create(TmdbApiService::class.java)