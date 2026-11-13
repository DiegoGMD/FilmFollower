package com.diegogmd.filmfollower.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import org.threeten.bp.LocalDate
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.diegogmd.filmfollower.R
import com.diegogmd.filmfollower.model.Episode
import com.diegogmd.filmfollower.model.responses.MultiSearchResult
import com.diegogmd.filmfollower.model.Film
import com.diegogmd.filmfollower.model.TvShow
import com.diegogmd.filmfollower.model.eraseFilm
import com.diegogmd.filmfollower.model.eraseTvShow
import com.diegogmd.filmfollower.model.getFilm
import com.diegogmd.filmfollower.model.getTvShow
import com.diegogmd.filmfollower.model.isFilmStatus
import com.diegogmd.filmfollower.model.isTvShowStatus
import com.diegogmd.filmfollower.model.responses.toLocalDateOrNull
import com.diegogmd.filmfollower.model.setEpisodeUnwatched
import com.diegogmd.filmfollower.model.setEpisodeWatched
import com.diegogmd.filmfollower.model.setFilmStatus
import com.diegogmd.filmfollower.model.setTvShowStatus
import com.diegogmd.filmfollower.ui.theme.FilmTypography
import com.diegogmd.filmfollower.ui.theme.placeholders
import com.diegogmd.filmfollower.viewmodels.FilmViewModel
import com.diegogmd.filmfollower.viewmodels.FilmViewModelFactory
import com.diegogmd.filmfollower.viewmodels.TvShowViewModel
import com.diegogmd.filmfollower.viewmodels.TvShowViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class MediaType(label: String) {
    Film("movie"), TvShow("tv")
}

@Immutable
data class ContentCardUi(
    val id: Int,
    val mediaType: MediaType,
    val title: String,
    val posterUrl: String?,
    val date: LocalDate?,
    val rating: Double?
)

enum class CardType { NONE, SEEN_TOGGLE, WATCHLIST_TOGGLE }

private const val TMDB_POSTER_BASE = "https://image.tmdb.org/t/p/w342"

private fun tmdbPosterUrl(path: String?): String? =
    path?.takeIf { it.isNotBlank() }?.let { TMDB_POSTER_BASE + it }

private fun LocalDate.orNullIfPlaceholder(): LocalDate? = takeUnless { it.year >= 9999 }

private fun Double?.orNullIfUnrated(): Double? =
    this?.takeIf { it > 0.0 }?.let { Math.round(it * 10) / 10.0 }

fun Film.toCardUi() = ContentCardUi(
    id = filmId,
    mediaType = MediaType.Film,
    title = title,
    posterUrl = tmdbPosterUrl(posterPath),
    date = releaseDate.orNullIfPlaceholder(),
    rating = rating.orNullIfUnrated()
)

fun TvShow.toCardUi() = ContentCardUi(
    id = showId,
    mediaType = MediaType.TvShow,
    title = title,
    posterUrl = tmdbPosterUrl(posterPath),
    date = firstAirDate.orNullIfPlaceholder(),
    rating = rating.orNullIfUnrated()
)

fun MultiSearchResult.toCardUi(): ContentCardUi? {
    val type = when (media_type) {
        "movie" -> MediaType.Film
        "tv" -> MediaType.TvShow
        else -> return null
    }
    val rawDate = when (type) {
        MediaType.Film -> release_date
        MediaType.TvShow -> first_air_date
    }
    return ContentCardUi(
        id = id,
        mediaType = type,
        title = displayTitle,
        posterUrl = tmdbPosterUrl(poster_path),
        date = rawDate.toLocalDateOrNull(),
        rating = vote_average.orNullIfUnrated()
    )
}

data class CardAction(
    val active: Boolean,
    @DrawableRes val activeIcon: Int,
    @DrawableRes val inactiveIcon: Int,
    val activeDescription: String,
    val inactiveDescription: String,
    val onClick: () -> Unit
)

@Composable
fun ContentCard(
    content: ContentCardUi,
    modifier: Modifier = Modifier,
    orientation: Boolean = false, // false = horizontal / true = vertical
    action: CardAction? = null,
    onClick: (ContentCardUi) -> Unit = {}
) {
    if (orientation) {
        VerticalContentCard(content, modifier, action, onClick)
    } else {
        HorizontalContentCard(content, modifier, action, onClick)
    }
}

@Composable
private fun VerticalContentCard(
    content: ContentCardUi,
    modifier: Modifier,
    action: CardAction?,
    onClick: (ContentCardUi) -> Unit
) {
    Card(
        onClick = { onClick(content) },
        modifier = modifier.width(100.dp)/*.wrapContentHeight()*/,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column {
            Poster(
                content = content, modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimary, thickness = 1.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = content.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                content.date?.let {
                    Text(
                        text = content.date.toString(),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            action?.let {
                CardActionButton(
                    action = it, modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                )
            }
        }
    }
}

@Composable
private fun HorizontalContentCard(
    content: ContentCardUi,
    modifier: Modifier,
    action: CardAction?,
    onClick: (ContentCardUi) -> Unit
) {
    val dateText = (content.date?.toString() ?: "????-??-??")
    val ratingText = (content.rating?.toString() ?: "?") + " ★"

    Card(
        onClick = { onClick(content) },
        modifier = modifier
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
            Poster(content = content, modifier = Modifier.size(100.dp))
            VerticalDivider(color = MaterialTheme.colorScheme.onPrimary, thickness = 1.dp)
            Column(
                modifier = modifier
                    .weight(1f)
                    .padding(10.dp)
            ) {
                Text(
                    text = content.title, fontSize = 14.sp, fontWeight = FontWeight.Bold
                )
                Text(
                    text = dateText + " · " + ratingText, fontSize = 14.sp
                )
            }
            action?.let {
                CardActionButton(
                    action = it, modifier = Modifier
                        .width(56.dp)
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Composable
private fun Poster(
    content: ContentCardUi, modifier: Modifier
) {
    AsyncImage(
        model = content.posterUrl,
        contentDescription = content.title,
        contentScale = ContentScale.Crop,
        placeholder = painterResource(MaterialTheme.placeholders.poster),
        error = painterResource(MaterialTheme.placeholders.poster),
        modifier = modifier
    )
}

@Composable
private fun Poster(
    content: String, contentDescription: String, modifier: Modifier
) {
    AsyncImage(
        model = content,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        placeholder = painterResource(MaterialTheme.placeholders.poster),
        error = painterResource(MaterialTheme.placeholders.poster),
        modifier = modifier
    )
}

@Composable
private fun CardActionButton(action: CardAction, modifier: Modifier) {
    Button(
        onClick = action.onClick,
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        shape = RoundedCornerShape(
            topStart = 0.dp, bottomStart = 0.dp, topEnd = 12.dp, bottomEnd = 12.dp
        )
    ) {
        Icon(
            painter = painterResource(if (action.active) action.activeIcon else action.inactiveIcon),
            contentDescription = if (action.active) action.activeDescription else action.inactiveDescription,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun CardActionButton(
    action: CardAction,
    modifier: Modifier,
    containerColor: Color = MaterialTheme.colorScheme.onPrimary,
    iconTint: Color = MaterialTheme.colorScheme.primary
) {
    Button(
        onClick = action.onClick,
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = RoundedCornerShape(
            topStart = 0.dp, bottomStart = 0.dp, topEnd = 12.dp, bottomEnd = 12.dp
        )
    ) {
        Icon(
            painter = painterResource(if (action.active) action.activeIcon else action.inactiveIcon),
            contentDescription = if (action.active) action.activeDescription else action.inactiveDescription,
            tint = iconTint
        )
    }
}

@Composable
fun rememberCardAction(
    content: ContentCardUi, cardType: CardType, onChanged: () -> Unit = {}
): CardAction? {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val filmViewModel: FilmViewModel = viewModel(factory = FilmViewModelFactory())
    val tvShowViewModel: TvShowViewModel = viewModel(factory = TvShowViewModelFactory())

    var watchlisted by remember(content.id, content.mediaType) { mutableStateOf(false) }
    var seen by remember(content.id, content.mediaType) { mutableStateOf(false) }

    LaunchedEffect(content.id, content.mediaType, cardType) {
        if (cardType == CardType.NONE) return@LaunchedEffect
        val (isWatchlisted, isSeen) = withContext(Dispatchers.IO) {
            when (content.mediaType) {
                MediaType.Film -> (getFilm(context, content.id) != null) to isFilmStatus(
                    context, content.id, "seen"
                )

                MediaType.TvShow -> (getTvShow(context, content.id) != null) to isTvShowStatus(
                    context, content.id, "seen"
                )
            }
        }
        watchlisted = isWatchlisted
        seen = isSeen
    }

    return when (cardType) {
        CardType.NONE -> null

        CardType.SEEN_TOGGLE -> when (content.mediaType) {
            MediaType.Film -> CardAction(
                active = seen,
                activeIcon = R.drawable.ic_visibility_off_24px,
                inactiveIcon = R.drawable.ic_visibility_24px,
                activeDescription = "Seen",
                inactiveDescription = "Not seen"
            ) {
                val newSeen = !seen
                scope.launch {
                    withContext(Dispatchers.IO) {
                        setFilmStatus(context, content.id, if (newSeen) "seen" else "watchlist")
                    }
                    seen = newSeen
                    onChanged()
                }
            }

            MediaType.TvShow -> CardAction(
                active = seen,
                activeIcon = R.drawable.ic_visibility_off_24px,
                inactiveIcon = R.drawable.ic_visibility_24px,
                activeDescription = "Seen",
                inactiveDescription = "Not seen"
            ) {
                val newSeen = !seen
                scope.launch {
                    withContext(Dispatchers.IO) {
                        setTvShowStatus(context, content.id, if (newSeen) "seen" else "watchlist")
                    }
                    seen = newSeen
                    onChanged()
                }
            }
        }

        CardType.WATCHLIST_TOGGLE -> when (content.mediaType) {
            MediaType.Film -> CardAction(
                active = watchlisted,
                activeIcon = R.drawable.ic_check_24px,
                inactiveIcon = R.drawable.ic_add_24px,
                activeDescription = "Watchlisted",
                inactiveDescription = "Not watchlisted"
            ) {
                if (watchlisted) {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            eraseFilm(context, content.id)
                            watchlisted = false
                            onChanged
                        }
                    }
                } else {
                    filmViewModel.addFilmToWatchlist(context, content.id) { ok ->
                        if (ok) {
                            watchlisted = true
                            onChanged()
                        }
                    }
                }
            }

            MediaType.TvShow -> CardAction(
                active = watchlisted,
                activeIcon = R.drawable.ic_check_24px,
                inactiveIcon = R.drawable.ic_add_24px,
                activeDescription = "Watchlisted",
                inactiveDescription = "Not watchlisted"
            ) {
                if (watchlisted) {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            eraseTvShow(context, content.id)
                            watchlisted = false
                            onChanged
                        }
                    }
                } else {
                    tvShowViewModel.addTvShowToWatchlist(context, content.id) { ok ->
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

@Composable
fun ContentCard(
    content: Film,
    modifier: Modifier = Modifier,
    orientation: Boolean = false,
    cardType: CardType = CardType.NONE,
    onClick: (ContentCardUi) -> Unit = {},
    onChanged: () -> Unit = {}
) {
    val ui = remember(content) { content.toCardUi() }
    ContentCard(ui, modifier, orientation, rememberCardAction(ui, cardType, onChanged), onClick)
}

@Composable
fun ContentCard(
    content: TvShow,
    modifier: Modifier = Modifier,
    orientation: Boolean = false,
    cardType: CardType = CardType.NONE,
    onClick: (ContentCardUi) -> Unit = {},
    onChanged: () -> Unit = {}
) {
    val ui = remember(content) { content.toCardUi() }
    ContentCard(ui, modifier, orientation, rememberCardAction(ui, cardType, onChanged), onClick)
}

@Composable
fun ContentCard(
    content: MultiSearchResult,
    modifier: Modifier = Modifier,
    orientation: Boolean = false,
    cardType: CardType = CardType.NONE,
    onClick: (ContentCardUi) -> Unit = {},
    onChanged: () -> Unit = {}
) {
    val ui = remember(content) { content.toCardUi() } ?: return
    ContentCard(ui, modifier, orientation, rememberCardAction(ui, cardType, onChanged), onClick)
}

@Composable
fun EmptyContentCard(modifier: Modifier = Modifier, isFilm: Boolean) {
    Card(
        modifier = modifier.padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainer)
    ) {
        val message =
            if (isFilm) stringResource(R.string.empty_films) else stringResource(R.string.empty_tvshows)

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
                contentDescription = "No internet",
                modifier = Modifier.size(50.dp)
            )
            Text(
                text = stringResource(R.string.no_internet),
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

// EpisodeContentCard

@Composable
fun EpisodeContentCard(
    episode: Episode,
    modifier: Modifier = Modifier,
    isShowScreen: Boolean = true,
    onClick: (Episode) -> Unit = {},
    onChanged: () -> Unit = {}
) {
    val cardAction = rememberEpisodeCardAction(episode, onChanged)
    if (isShowScreen) {
        EpisodeContentCardLarge(episode, modifier, cardAction, onClick)
    } else {
        EpisodeContentCardCompacted(episode, modifier, cardAction)
    }
}

@Composable
private fun EpisodeContentCardLarge(
    episode: Episode,
    modifier: Modifier,
    action: CardAction?,
    onClick: (Episode) -> Unit = {},
) {
    val tvShow = getTvShow(LocalContext.current, episode.showId)
    var posterUrl = ""
    var description = "Tv Show Poster"
    if (tvShow != null) {
        if (tvShow.posterPath != null) {
            posterUrl = tmdbPosterUrl(tvShow.posterPath).toString()
        }
        description = "${tvShow.title} Poster"
    }

    Card(
        onClick = { onClick(episode) },
        modifier = modifier
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
            Poster(
                content = posterUrl,
                contentDescription = description,
                modifier = Modifier.size(100.dp)
            )
            VerticalDivider(color = MaterialTheme.colorScheme.onPrimary, thickness = 1.dp)
            Column(
                modifier = modifier
                    .weight(1f)
                    .padding(10.dp)
            ) {
                Text(
                    text = episode.title, fontSize = 14.sp, fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${tvShow?.title} - S${episode.seasonNumber} E${episode.episodeNumber}",
                    fontSize = 14.sp
                )
            }
            action?.let {
                CardActionButton(
                    action = it, modifier = Modifier
                        .width(56.dp)
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Composable
private fun EpisodeContentCardCompacted(
    episode: Episode,
    modifier: Modifier,
    action: CardAction?,
) {
    Card(
        onClick = { },
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = modifier
                    .weight(1f)
                    .padding(10.dp)
            ) {
                Text(
                    text = "${episode.episodeNumber}. ${episode.title}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${episode.airDate} · ${episode.runtime} min", fontSize = 12.sp
                )
            }
            action?.let {
                CardActionButton(
                    action = it,
                    modifier = Modifier
                        .width(56.dp)
                        .fillMaxHeight(),
                    containerColor = Color.Transparent,
                    iconTint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun rememberEpisodeCardAction(
    episode: Episode, onChanged: () -> Unit = {}
): CardAction {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var watched by remember(episode.showId, episode.seasonNumber, episode.episodeNumber) {
        mutableStateOf(episode.watchedDate != null)
    }

    return CardAction(
        active = watched,
        activeIcon = R.drawable.ic_visibility_off_24px,
        inactiveIcon = R.drawable.ic_visibility_24px,
        activeDescription = "Seen",
        inactiveDescription = "Not seen"
    ) {
        val newWatched = !watched
        scope.launch {
            val ok = withContext(Dispatchers.IO) {
                if (newWatched) {
                    setEpisodeWatched(
                        context, episode.showId, episode.seasonNumber, episode.episodeNumber
                    )
                } else {
                    setEpisodeUnwatched(
                        context, episode.showId, episode.seasonNumber, episode.episodeNumber
                    )
                }
            }
            if (ok) {
                watched = newWatched
                onChanged()
            }
        }
    }
}