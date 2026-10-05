package com.diegogmd.filmfollower.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import org.threeten.bp.LocalDate
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.diegogmd.filmfollower.R
import com.diegogmd.filmfollower.model.Episode
import com.diegogmd.filmfollower.model.MultiSearchResult
import com.diegogmd.filmfollower.model.Film
import com.diegogmd.filmfollower.model.getFilm
import com.diegogmd.filmfollower.model.getTvShow
import com.diegogmd.filmfollower.model.eraseFilm
import com.diegogmd.filmfollower.ui.theme.DarkCoffee
import com.diegogmd.filmfollower.ui.theme.FilmTypography
import com.diegogmd.filmfollower.ui.theme.LightCaramel
import com.diegogmd.filmfollower.viewmodels.FilmViewModel
import com.diegogmd.filmfollower.viewmodels.FilmViewModelFactory

@Composable
fun ContentCard(
    content: MultiSearchResult,
    orientation: Boolean = false, // false = horizontal row, true = vertical poster
    onClick: (Int) -> Unit = {},
) {
    val context = LocalContext.current
    val title = content.displayTitle
    val posterUrl = content.poster_path?.let { "https://image.tmdb.org/t/p/w342$it" }
    val date = when (content.media_type) {
        "movie" -> content.release_date.toLocalDateOrNull()
        "tv" -> content.first_air_date.toLocalDateOrNull()
        else -> null // "person" and anything unexpected
    }
    val rating =
        if (content.vote_average != null) Math.round(content.vote_average * 10) / 10.0 else null
    val wishlisted = if (getFilm(context, content.id) == null) false else true

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightCaramel,
            contentColor = DarkCoffee
        ),
        border = BorderStroke(1.dp, DarkCoffee)
    ) {

        if (orientation) {
            // Vertical layout: image on top, text below
            VerticalContentCard(
                content.id,
                posterUrl,
                title,
                date,
                rating,
                wishlisted,
                true,
                onClick
            )
        } else {
            // Horizontal layout: image left, text right
            HorizontalContentCard(
                content.id,
                posterUrl,
                title,
                date,
                rating,
                wishlisted,
                true,
                onClick
            )
        }
    }
}

@Composable
fun ContentCard(
    content: Film,
    orientation: Boolean = false, // false = horizontal row, true = vertical poster
    onClick: (Int) -> Unit = {},
) {
    val posterUrl = content.posterPath?.let { "https://image.tmdb.org/t/p/w342$it" }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightCaramel,
            contentColor = DarkCoffee
        ),
        border = BorderStroke(1.dp, DarkCoffee)
    ) {

        if (orientation) {
            // Vertical layout: image on top, text below
            VerticalContentCard(
                content.filmId,
                posterUrl,
                content.title,
                content.releaseDate,
                content.rating,
                false,
                false,
                onClick
            )
        } else {
            // Horizontal layout: image left, text right
            HorizontalContentCard(
                content.filmId,
                posterUrl,
                content.title,
                content.releaseDate,
                content.rating,
                false,
                false,
                onClick
            )
        }
    }
}

@Composable
private fun VerticalContentCard(
    filmId: Int,
    posterUrl: String?,
    title: String,
    date: LocalDate?,
    rating: Double?,
    wishlisted: Boolean = false,
    button: Boolean = false,
    onClick: (Int) -> Unit = {},
    viewModel: FilmViewModel = viewModel(factory = FilmViewModelFactory())
) {
    var wishlisted by remember(wishlisted) { mutableStateOf(wishlisted) }

    Column(modifier = Modifier.padding(0.dp)) {
        AsyncImage(
            model = posterUrl,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder_poster),
            error = painterResource(R.drawable.placeholder_poster),
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        )
        Surface(
            onClick = { onClick(filmId) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            ContentCardText(title, date, rating)
        }
        if (button) {
            val context = LocalContext.current

            Button(
                onClick = {
                    if (!wishlisted) {
                        viewModel.addFilmToWishlist(context, filmId)
                    } else {
                        eraseFilm(context, filmId)
                    }
                    wishlisted = !wishlisted
                },
                modifier = Modifier
                    .width(56.dp)
                    .fillMaxHeight(),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkCoffee
                ),
                shape = RoundedCornerShape(
                    topStart = 0.dp, bottomStart = 0.dp,
                    topEnd = 12.dp, bottomEnd = 12.dp
                )
            ) {
                if (!wishlisted) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Not wishlisted",
                        tint = LightCaramel
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Wishlisted",
                        tint = LightCaramel
                    )
                }
            }
        }
    }
}

@Composable
private fun HorizontalContentCard(
    filmId: Int,
    posterUrl: String?,
    title: String,
    date: LocalDate?,
    rating: Double?,
    wishlisted: Boolean = false,
    button: Boolean = false,
    onClick: (Int) -> Unit = {},
    viewModel: FilmViewModel = viewModel(factory = FilmViewModelFactory())
) {
    var wishlisted by remember(wishlisted) { mutableStateOf(wishlisted) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = posterUrl,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder_poster),
            error = painterResource(R.drawable.placeholder_poster),
            modifier = Modifier.size(100.dp)
        )
        Surface(
            onClick = { onClick(filmId) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp)
        ) { ContentCardText(title, date, rating) }
        if (button) {
            val context = LocalContext.current

            Button(
                onClick = {
                    if (!wishlisted) {
                        viewModel.addFilmToWishlist(context, filmId)
                    } else {
                        eraseFilm(context, filmId)
                    }
                    wishlisted = !wishlisted
                },
                modifier = Modifier
                    .width(56.dp)
                    .fillMaxHeight(),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkCoffee
                ),
                shape = RoundedCornerShape(
                    topStart = 0.dp, bottomStart = 0.dp,
                    topEnd = 12.dp, bottomEnd = 12.dp
                )
            ) {
                if (!wishlisted) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Not wishlisted",
                        tint = LightCaramel
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Whishlisted",
                        tint = LightCaramel
                    )
                }
            }
        }
    }
}

@Composable
private fun ContentCardText(
    title: String,
    date: LocalDate?,
    rating: Double?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(10.dp)) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = (if (date != null) "${date} · " else "") + (if (rating != null) "${rating} ★" else "? ★"),
            fontSize = 14.sp
        )
    }
}

@Composable
fun EpisodeContentCard(
    episode: Episode,
    orientation: Boolean = false // false = horizontal row, true = vertical poster
) {
    var showName: String
    var posterUrl: String? = null
    val context = LocalContext.current
    val tvShow = getTvShow(context, episode.showId)
    if (tvShow != null) {
        showName = tvShow.title
        posterUrl = tvShow.posterPath
    } else {
        showName = "Unknown"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, DarkCoffee)
    ) {
        if (orientation) {
            // Vertical layout: image on top, text below
            Column(modifier = Modifier.padding(0.dp)) {
                AsyncImage(
                    model = posterUrl,
                    contentDescription = showName,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.placeholder_poster),
                    error = painterResource(R.drawable.placeholder_poster),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )
                Spacer(Modifier.height(8.dp))
                EpisodeContentCardText(
                    showName,
                    episode.seasonNumber,
                    episode.episodeNumber,
                    episode.title
                )
            }
        } else {
            // Horizontal layout: image left, text right
            Row(
                modifier = Modifier.padding(0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = posterUrl,
                    contentDescription = showName,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.placeholder_poster),
                    error = painterResource(R.drawable.placeholder_poster),
                    modifier = Modifier.size(100.dp)
                )
                EpisodeContentCardText(
                    showName,
                    episode.seasonNumber,
                    episode.episodeNumber,
                    episode.title
                )
            }
        }
    }
}

@Composable
fun EpisodeContentCardText(
    showName: String,
    seasonNumber: Int,
    episodeNumber: Int,
    episodeTitle: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(10.dp)) {
        Text(
            text = "$showName | S${seasonNumber}E${episodeNumber}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = episodeTitle,
            fontSize = 14.sp
        )
    }
}

@Composable
fun EmptyContentCard(modifier: Modifier = Modifier, isFilm: Boolean) {
    Card(
        modifier = modifier.padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightCaramel,
            contentColor = DarkCoffee
        ),
        border = BorderStroke(1.dp, DarkCoffee)
    ) {
        val message = if (isFilm) "No films in the watchlist" else "No TV shows in the watchlist"

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_info_24px),
                contentDescription = message,
                tint = DarkCoffee,
                modifier = Modifier.size(50.dp)
            )
            Text(
                text = message,
                color = DarkCoffee,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                style = FilmTypography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            )
        }
    }
}

@Composable
fun NoInternetCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightCaramel,
            contentColor = DarkCoffee
        ),
        border = BorderStroke(1.dp, DarkCoffee)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_wifi_off_24px),
                contentDescription = "No internet connection",
                tint = DarkCoffee,
                modifier = Modifier.size(50.dp)
            )
            Text(
                text = "No internet connection",
                color = DarkCoffee,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                style = FilmTypography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            )
        }
    }
}

fun String?.toLocalDateOrNull(): LocalDate? {
    if (this.isNullOrBlank()) return null
    return runCatching { LocalDate.parse(this) }.getOrNull()
}