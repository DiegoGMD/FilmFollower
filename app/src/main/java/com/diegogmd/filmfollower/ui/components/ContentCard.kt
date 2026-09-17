package com.diegogmd.filmfollower.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.Alignment
import org.threeten.bp.LocalDate
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.diegogmd.filmfollower.R
import com.diegogmd.filmfollower.model.Episode
import com.diegogmd.filmfollower.model.MultiSearchResult
import com.diegogmd.filmfollower.model.Film
import com.diegogmd.filmfollower.model.getFilm
import com.diegogmd.filmfollower.model.getTvShow
import com.diegogmd.filmfollower.model.eraseFilm
import com.diegogmd.filmfollower.ui.theme.DarkCoffee
import com.diegogmd.filmfollower.ui.theme.LightCaramel
import com.diegogmd.filmfollower.viewmodels.FilmViewModel
import com.diegogmd.filmfollower.viewmodels.FilmViewModelFactory

@Composable
fun ContentCard(
    content: MultiSearchResult,
    orientation: Boolean = false, // false = horizontal row, true = vertical poster
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val title = if (content.title != null) content.title else "Unknown"
    val posterUrl = content.poster_path?.let { "https://image.tmdb.org/t/p/w92$it" }
    val date = when (content.media_type) {
        "movie" -> content.release_date.toLocalDateOrNull()
        "tv" -> content.first_air_date.toLocalDateOrNull()
        else -> null // "person" and anything unexpected
    }
    val rating = Math.round(content.vote_average * 10) / 10.0
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
            VerticalContentCard(content.id, posterUrl, title, date, rating, wishlisted, true)
        } else {
            // Horizontal layout: image left, text right
            HorizontalContentCard(content.id, posterUrl, title, date, rating, wishlisted, true)
        }
    }
}

@Composable
fun ContentCard(
    content: Film,
    orientation: Boolean = false, // false = horizontal row, true = vertical poster
) {
    val navController = rememberNavController()
    val posterUrl = content.posterPath?.let { "https://image.tmdb.org/t/p/w92$it" }

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
            VerticalContentCard(content.filmId, posterUrl, content.title, content.releaseDate, content.rating)
        } else {
            // Horizontal layout: image left, text right
            HorizontalContentCard(content.filmId, posterUrl, content.title, content.releaseDate, content.rating)
        }
    }
}

@Composable
private fun VerticalContentCard(
    filmId: Int,
    posterUrl: String?,
    title: String,
    date: LocalDate?,
    rating: Double,
    wishlisted: Boolean = false,
    button: Boolean = false
) {
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
            onClick = { /* When pressed i'll see ContentPage of the film/show */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            ContentCardText(title, date, rating)
        }
        if (button) {
            Button(
                onClick = {
                    if (!wishlisted) {
                        // Add wishlist and change the button icon
                    } else {
                        // Erase from wishlist and change the button icon
                    }
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
    rating: Double,
    wishlisted: Boolean = false,
    button: Boolean = false,
    viewModel: FilmViewModel = viewModel(factory = FilmViewModelFactory())
) {
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
            onClick = { /* When pressed i'll see ContentPage of the film/show */ },
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
    rating: Double,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(10.dp)) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = (if (date != null) "${date} · " else "") + "$rating ★",
            fontSize = 14.sp
        )
    }
}

@Composable
fun EpisodeContentCard(
    episode: Episode,
    orientation: Boolean = false // false = horizontal row, true = vertical poster
) {
    var showName = "Unknown"
    var posterUrl: String? = null
    val context = LocalContext.current
    val tvShow = getTvShow(context, episode.showId)
    if (tvShow != null) {
        showName = tvShow.title
        posterUrl = tvShow.poster_path
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
                EpisodeContentCardText(showName, episode.seasonNumber, episode.episodeNumber,  episode.title)
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
                EpisodeContentCardText(showName, episode.seasonNumber, episode.episodeNumber,  episode.title)
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

fun String?.toLocalDateOrNull(): LocalDate? {
    if (this.isNullOrBlank()) return null
    return runCatching { LocalDate.parse(this) }.getOrNull()
}