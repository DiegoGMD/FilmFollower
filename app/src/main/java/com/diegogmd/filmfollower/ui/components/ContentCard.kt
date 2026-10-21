package com.diegogmd.filmfollower.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.diegogmd.filmfollower.R
import com.diegogmd.filmfollower.model.Episode
import com.diegogmd.filmfollower.model.MultiSearchResult
import com.diegogmd.filmfollower.model.Film
import com.diegogmd.filmfollower.model.eraseFilm
import com.diegogmd.filmfollower.model.getFilm
import com.diegogmd.filmfollower.model.getTvShow
import com.diegogmd.filmfollower.model.isStatus
import com.diegogmd.filmfollower.model.setStatus
import com.diegogmd.filmfollower.ui.theme.FilmTypography
import com.diegogmd.filmfollower.ui.theme.placeholders
import com.diegogmd.filmfollower.viewmodels.FilmViewModel
import com.diegogmd.filmfollower.viewmodels.FilmViewModelFactory
import com.diegogmd.filmfollower.viewmodels.TvShowViewModel
import com.diegogmd.filmfollower.viewmodels.TvShowViewModelFactory

enum class CardOrientation { Horizontal, Vertical }

@Composable
fun ContentCard(
    modifier: Modifier = Modifier,
    content: MultiSearchResult,
    orientation: Boolean = false, // false = horizontal row, true = vertical poster
    cardType: Int = 0,
    onClick: (Int) -> Unit = {},
    onChanged: () -> Unit = {}
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


    if (orientation) {
        // Vertical layout: image on top, text below
        VerticalContentCard(
            modifier,
            content.media_type,
            content.id,
            posterUrl,
            title,
            date,
            rating,
            cardType,
            onClick,
            onChanged
        )
    } else {
        // Horizontal layout: image left, text right
        HorizontalContentCard(
            modifier,
            content.media_type,
            content.id,
            posterUrl,
            title,
            date,
            rating,
            cardType,
            onClick,
            onChanged
        )
    }
}

@Composable
fun ContentCard(
    modifier: Modifier = Modifier,
    content: Film,
    orientation: Boolean = false, // false = horizontal row, true = vertical poster
    cardType: Int = 0,
    onClick: (Int) -> Unit = {},
    onChanged: () -> Unit = {}
) {
    val posterUrl = content.posterPath?.let { "https://image.tmdb.org/t/p/w342$it" }

    if (orientation) {
        // Vertical layout: image on top, text below
        VerticalContentCard(
            modifier,
            "movie",
            content.filmId,
            posterUrl,
            content.title,
            content.releaseDate,
            content.rating,
            cardType,
            onClick,
            onChanged
        )
    } else {
        // Horizontal layout: image left, text right
        HorizontalContentCard(
            modifier,
            "movie",
            content.filmId,
            posterUrl,
            content.title,
            content.releaseDate,
            content.rating,
            cardType,
            onClick,
            onChanged
        )
    }
}

@Composable
private fun VerticalContentCard(
    modifier: Modifier,
    mediaType: String,
    contentId: Int,
    posterUrl: String?,
    title: String,
    date: LocalDate?,
    rating: Double?,
    cardType: Int = 0,
    onClick: (Int) -> Unit = {},
    onChanged: () -> Unit = {}
) {
    val context = LocalContext.current
    val filmViewModel: FilmViewModel = viewModel(factory = FilmViewModelFactory())
    val tvShowViewModel: TvShowViewModel = viewModel(factory = TvShowViewModelFactory())
    var watchlisted by remember(contentId) { mutableStateOf(getFilm(context, contentId) != null) }
    var seen by remember(contentId) { mutableStateOf(isStatus(context, contentId, "seen")) }

    Card (
        modifier = modifier
            .width(100.dp)
            .wrapContentHeight()
            .clickable { onClick(contentId) },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column {
            AsyncImage(
                model = posterUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(MaterialTheme.placeholders.poster),
                error = painterResource(MaterialTheme.placeholders.poster),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onPrimary,
                thickness = 1.dp,
            )
            Surface(
                onClick = { onClick(contentId) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    if (date != null) {
                        Text(
                            text = date.toString(),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            when (cardType) {
                0 -> {}
                1 -> ButtonType(
                    seen,
                    R.drawable.ic_visibility_off_24px,
                    R.drawable.ic_visibility_24px,
                    "Seen",
                    "Not seen"
                ) {
                    setStatus(context, contentId, if (seen) "watchlist" else "seen")
                    seen = !seen
                    onChanged()
                }

                2 -> ButtonType(
                    watchlisted,
                    R.drawable.ic_check_24px,
                    R.drawable.ic_add_24px,
                    "Watchlisted",
                    "Not watchlisted"
                ) {
                    if (watchlisted) {
                        eraseFilm(context, contentId)
                        watchlisted = false
                        onChanged()
                    } else {
                        filmViewModel.addFilmToWatchlist(context, contentId) { ok ->
                            if (ok) {
                                watchlisted = true
                                onChanged()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HorizontalContentCard(
    modifier: Modifier,
    mediaType: String,
    contentId: Int,
    posterUrl: String?,
    title: String,
    date: LocalDate?,
    rating: Double?,
    cardType: Int = 0,
    onClick: (Int) -> Unit = {},
    onChanged: () -> Unit = {}
) {
    val context = LocalContext.current
    val filmViewModel: FilmViewModel = viewModel(factory = FilmViewModelFactory())
    val tvShowViewModel: TvShowViewModel = viewModel(factory = TvShowViewModelFactory())
    var watchlisted by remember(contentId) { mutableStateOf(getFilm(context, contentId) != null) }
    var seen by remember(contentId) { mutableStateOf(isStatus(context, contentId, "seen")) }

    Card (modifier = modifier
        .fillMaxWidth()
        .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainer)
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
                placeholder = painterResource(MaterialTheme.placeholders.poster),
                error = painterResource(MaterialTheme.placeholders.poster),
                modifier = Modifier.size(100.dp)
            )
            VerticalDivider(
                color = MaterialTheme.colorScheme.onPrimary,
                thickness = 1.dp,
            )
            Surface(
                onClick = { onClick(contentId) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = modifier.padding(10.dp)) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = (if (date != null) "${date} · " else "")
                                + (if (rating != null) "${rating} ★" else "? ★"),
                        fontSize = 14.sp
                    )
                }
            }
            when (cardType) {
                0 -> {}
                1 -> ButtonType(
                    seen,
                    R.drawable.ic_visibility_off_24px,
                    R.drawable.ic_visibility_24px,
                    "Seen",
                    "Not seen"
                ) {
                    setStatus(context, contentId, if (seen) "watchlist" else "seen")
                    seen = !seen
                    onChanged()
                }

                2 -> ButtonType(
                    watchlisted,
                    R.drawable.ic_check_24px,
                    R.drawable.ic_add_24px,
                    "Watchlisted",
                    "Not watchlisted"
                ) {
                    if (watchlisted) {
                        eraseFilm(context, contentId)
                        watchlisted = false
                        onChanged()
                    } else {
                        filmViewModel.addFilmToWatchlist(context, contentId) { ok ->
                            if (ok) {
                                watchlisted = true
                                onChanged()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardActionButton(
    active: Boolean,
    @DrawableRes activeIcon: Int,
    @DrawableRes inactiveIcon: Int,
    activeDescription: String,
    inactiveDescription: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(56.dp)
            .fillMaxHeight(),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        shape = RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 12.dp, bottomEnd = 12.dp)
    ) {
        Icon(
            painter = painterResource(if (active) activeIcon else inactiveIcon),
            contentDescription = if (active) activeDescription else inactiveDescription,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ButtonType(
    boolean: Boolean,
    @DrawableRes activeIcon: Int,
    @DrawableRes inactiveIcon: Int,
    activeDescription: String,
    inactiveDescription: String,
    onAction: () -> Unit = {}
) {
    CardActionButton(
        active = boolean,
        activeIcon = activeIcon,
        inactiveIcon = inactiveIcon,
        activeDescription = activeDescription,
        inactiveDescription = inactiveDescription,
        onClick = { onAction() }
    )
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainer)
    ) {
        if (orientation) {
            // Vertical layout: image on top, text below
            Column(modifier = Modifier.padding(0.dp)) {
                AsyncImage(
                    model = posterUrl,
                    contentDescription = showName,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(MaterialTheme.placeholders.poster),
                    error = painterResource(MaterialTheme.placeholders.poster),
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
                    placeholder = painterResource(MaterialTheme.placeholders.poster),
                    error = painterResource(MaterialTheme.placeholders.poster),
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainer)
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
                modifier = Modifier.size(50.dp)
            )
            Text(
                text = message,
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_wifi_off_24px),
                contentDescription = "No internet connection",
                modifier = Modifier.size(50.dp)
            )
            Text(
                text = "No internet connection",
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