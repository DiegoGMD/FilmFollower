package com.diegogmd.filmfollower.data.local

import com.diegogmd.filmfollower.data.local.remote.tmdbApi
import kotlinx.coroutines.runBlocking

/**
 * Manual connection test — run this file's main() directly to confirm your
 * TMDB token and network setup work, without launching the Android app.
 */

fun main() = runBlocking {
    println("Testing TMDB connection...")

    try {
        val response = tmdbApi.searchMulti(query = "Matrix")

        println("Success! Got ${response.results.size} results (page ${response.page} of ${response.totalPages})")

        response.results
            .filter { it.media_type == "movie" || it.media_type == "tv" }
            .forEach { result ->
                println("- [${result.media_type}] ${result.displayTitle}")
            }
    } catch (e: Exception) {
        println("Request failed: ${e.message}")
        e.printStackTrace()
    }
}