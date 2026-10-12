package com.diegogmd.filmfollower.viewmodels

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