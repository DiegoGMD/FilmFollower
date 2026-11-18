package com.diegogmd.filmfollower.ui.pages

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.diegogmd.filmfollower.R
import com.diegogmd.filmfollower.model.Episode
import com.diegogmd.filmfollower.viewmodels.FilmViewModel
import com.diegogmd.filmfollower.model.Film
import com.diegogmd.filmfollower.model.Season
import com.diegogmd.filmfollower.model.responses.MultiSearchResult
import com.diegogmd.filmfollower.model.TvShow
import com.diegogmd.filmfollower.model.eraseFilm
import com.diegogmd.filmfollower.model.eraseTvShow
import com.diegogmd.filmfollower.model.getFilm
import com.diegogmd.filmfollower.model.getTvShow
import com.diegogmd.filmfollower.model.isFavouriteFilm
import com.diegogmd.filmfollower.model.isFavouriteTvShow
import com.diegogmd.filmfollower.model.isFilmStatus
import com.diegogmd.filmfollower.model.isTvShowStatus
import com.diegogmd.filmfollower.model.setFavouriteFilm
import com.diegogmd.filmfollower.model.setFavouriteTvShow
import com.diegogmd.filmfollower.model.setFilmStatus
import com.diegogmd.filmfollower.model.setTvShowStatus
import com.diegogmd.filmfollower.model.unsetFavouriteFilm
import com.diegogmd.filmfollower.model.unsetFavouriteTvShow
import com.diegogmd.filmfollower.ui.components.CardActionButton
import com.diegogmd.filmfollower.ui.components.ContentCard
import com.diegogmd.filmfollower.ui.components.EpisodeContentCard
import com.diegogmd.filmfollower.ui.components.MediaType
import com.diegogmd.filmfollower.ui.components.rememberSeasonCardAction
import com.diegogmd.filmfollower.ui.theme.FilmTypography
import com.diegogmd.filmfollower.ui.theme.Graphite
import com.diegogmd.filmfollower.ui.theme.Gunmetal
import com.diegogmd.filmfollower.ui.theme.Silver
import com.diegogmd.filmfollower.ui.theme.placeholders
import com.diegogmd.filmfollower.viewmodels.FilmViewModelFactory
import com.diegogmd.filmfollower.viewmodels.TvShowViewModel
import com.diegogmd.filmfollower.viewmodels.TvShowViewModelFactory
import org.threeten.bp.LocalDate

private fun openTrailer(context: Context, key: String) {
    val app = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$key"))
    val web = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$key"))
    try {
        context.startActivity(app) // YouTube app if installed
    } catch (e: ActivityNotFoundException) {
        context.startActivity(web) // otherwise browser
    }
}

private fun isNextToWatch(
    episode: Episode,
    seasons: List<Season>,
    episodesBySeason: Map<Int, List<Episode>>
): Boolean {
    val ordered = seasons
        .sortedBy { if (it.seasonNumber == 0) Int.MAX_VALUE else it.seasonNumber }
        .flatMap { episodesBySeason[it.seasonNumber].orEmpty().sortedBy { ep -> ep.episodeNumber } }

    val lastWatchedIndex = ordered.indexOfLast { it.isWatched }
    val nextAfterLastWatched = ordered.drop(lastWatchedIndex + 1).firstOrNull { !it.isWatched }
    val firstUnwatched = ordered.firstOrNull { !it.isWatched }

    return nextAfterLastWatched == null ||
            episode == nextAfterLastWatched ||
            episode == firstUnwatched   // filling the earliest gap (e.g. ep 2) shouldn't warn
}

@Composable
fun ContentScreenFilm(
    filmId: Int,
    onBackClick: () -> Unit = {},
    navController: NavHostController,
    viewModel: FilmViewModel = viewModel(factory = FilmViewModelFactory())
) {
    val context = LocalContext.current
    val film by viewModel.film.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(filmId) {
        viewModel.loadFilm(context, filmId)
        viewModel.loadRecommendations(filmId)
    }

    when {
        isLoading -> CircularProgressIndicator()
        film != null -> FilmContentUI(onBackClick, navController, viewModel, film!!)
    }
}

@Composable
fun ContentScreenTvShow(
    showId: Int,
    navController: NavHostController,
    onBackClick: () -> Unit = {},
    viewModel: TvShowViewModel = viewModel(factory = TvShowViewModelFactory())
) {
    val context = LocalContext.current
    val tvShow by viewModel.tvShow.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val seasons by viewModel.seasons.collectAsState()

    LaunchedEffect(showId) {
        viewModel.loadTvShow(context, showId)
        viewModel.loadRecommendations(showId)
        viewModel.loadSeasons(context, showId)
    }

    LaunchedEffect(seasons) {
        seasons.forEach { viewModel.loadEpisodes(context, showId, it.seasonNumber) }
    }

    when {
        isLoading -> CircularProgressIndicator()
        tvShow != null -> TvShowContentUI(onBackClick, navController, viewModel, tvShow!!)
    }
}

@Composable
private fun FilmContentUI(
    onBackClick: () -> Unit,
    navController: NavHostController,
    viewModel: FilmViewModel,
    film: Film
) {
    val context = LocalContext.current
    val genres by viewModel.genres.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()
    var watchlisted by remember(film.filmId) { mutableStateOf(getFilm(context, film.filmId) != null) }
    var seen by remember(film.filmId) { mutableStateOf(isFilmStatus(context, film.filmId, "seen")) }
    var archived by remember(film.filmId) { mutableStateOf(isFilmStatus(context, film.filmId, "archived")) }
    var favourite by remember(film.filmId) { mutableStateOf(isFavouriteFilm(context, film.filmId)) }
    val trailerKey by viewModel.trailerKey.collectAsState()

    LaunchedEffect(film.filmId) { viewModel.loadTrailer(film.filmId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onSurface)
    ) {
        ContentHeader(
            onBackClick = onBackClick,
            onReloadClick = { viewModel.loadFilm(context, film.filmId, true) },
            film.title,
            film.originalTitle,
            film.releaseDate,
            film.runtime,
            film.rating,
            film.posterPath
        )

        ContentBody(
            modifier = Modifier.weight(1f),
            genres = genres,
            overview = film.overview,
            recommendations = recommendations,
            navController = navController,
            buttonArea = {
                ButtonArea(
                    seen = seen,
                    favourite = favourite,
                    archived = archived,
                    watchlisted = watchlisted,
                    trailerAvailable = trailerKey != null,
                    onSeenClick = {
                        if (!seen && !watchlisted) {
                            viewModel.addFilmToWatchlist(context, film.filmId) { ok ->
                                if (ok) {
                                    setFilmStatus(context, film.filmId, "seen")
                                    watchlisted = true
                                    seen = true
                                    archived = false
                                }
                            }
                        } else {
                            val newStatus = if (!seen) "seen" else "watchlist"
                            setFilmStatus(context, film.filmId, newStatus)
                            if (favourite) {
                                if (unsetFavouriteFilm(context, film.filmId)) favourite = false
                            }
                            seen = newStatus == "seen"
                            archived = false
                        }
                    },
                    onFavouriteClick = {
                        if (!seen && !watchlisted) {
                            Toast.makeText(
                                context,
                                "You should see it before setting it favourite",
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (favourite) {
                            if (unsetFavouriteFilm(context, film.filmId)) favourite = false
                        } else {
                            if (setFavouriteFilm(context, film.filmId)) favourite = true
                        }
                    },
                    onArchiveClick = {
                        val newStatus = if (!archived) "archived" else "watchlist"
                        setFilmStatus(context, film.filmId, newStatus)
                        archived = newStatus == "archived"
                        seen = false
                    },
                    onWatchlistClick = {
                        if (watchlisted) {
                            eraseFilm(context, film.filmId)
                            watchlisted = false
                            seen = false
                            archived = false
                        } else {
                            viewModel.addFilmToWatchlist(context, film.filmId) { ok ->
                                if (ok) {
                                    watchlisted = true
                                    seen = false
                                    archived = false
                                }
                            }
                        }
                    },
                    onTrailerClick = { trailerKey?.let { openTrailer(context, it) } }
                )
            }
        )
    }
}

@Composable
private fun TvShowContentUI(
    onBackClick: () -> Unit,
    navController: NavHostController,
    viewModel: TvShowViewModel,
    tvShow: TvShow
) {
    val context = LocalContext.current
    val genres by viewModel.genres.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()
    val seasons by viewModel.seasons.collectAsState()
    val isLoadingSeasons by viewModel.isLoadingSeasons.collectAsState()
    val episodesBySeason by viewModel.episodesBySeason.collectAsState()
    var watchlisted by remember(tvShow.showId) {
        mutableStateOf(
            getTvShow(
                context, tvShow.showId
            ) != null
        )
    }
    var seen by remember(tvShow.showId) {
        mutableStateOf(
            isTvShowStatus(
                context, tvShow.showId, "completed"
            )
        )
    }
    var archived by remember(tvShow.showId) {
        mutableStateOf(
            isTvShowStatus(
                context, tvShow.showId, "archived"
            )
        )
    }
    var favourite by remember(tvShow.showId) {
        mutableStateOf(
            isFavouriteTvShow(
                context, tvShow.showId
            )
        )
    }
    val trailerKey by viewModel.trailerKey.collectAsState()
    var pendingEpisode by remember { mutableStateOf<Episode?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(R.string.about, R.string.episodes)

    LaunchedEffect(tvShow.showId) { viewModel.loadTrailer(tvShow.showId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onSurface)
    ) {
        ContentHeader(
            onBackClick = onBackClick,
            onReloadClick = { viewModel.loadTvShow(context, tvShow.showId, true) },
            tvShow.title,
            tvShow.originalTitle,
            tvShow.firstAirDate,
            null,
            tvShow.rating,
            tvShow.posterPath
        )

        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = {
                    Text(
                        text = stringResource(id = title), style = FilmTypography.labelMedium
                    )
                })
            }
        }

        when (selectedTab) {
            0 -> ContentBody(
                modifier = Modifier.weight(1f),
                genres = genres,
                overview = tvShow.overview,
                recommendations = recommendations,
                navController = navController,
                buttonArea = {
                    ButtonArea(seen = seen,
                        favourite = favourite,
                        archived = archived,
                        watchlisted = watchlisted,
                        trailerAvailable = trailerKey != null,
                        onSeenClick = {
                            if (!seen && !watchlisted) {
                                viewModel.addTvShowToWatchlist(context, tvShow.showId) { ok ->
                                    if (ok) {
                                        setTvShowStatus(context, tvShow.showId, "completed")
                                        watchlisted = true
                                        seen = true
                                        archived = false
                                    }
                                }
                            } else {
                                val newStatus = if (!seen) "completed" else "watchlist"
                                setTvShowStatus(context, tvShow.showId, newStatus)
                                seen = newStatus == "completed"
                                archived = false
                            }
                        },
                        onFavouriteClick = {
                            if (!seen && !watchlisted) {
                                Toast.makeText(
                                    context,
                                    "You should see it before setting it favourite",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else if (favourite) {
                                if (unsetFavouriteTvShow(context, tvShow.showId)) favourite = false
                            } else {
                                if (setFavouriteTvShow(context, tvShow.showId)) favourite = true
                            }
                        },
                        onArchiveClick = {
                            val newStatus = if (!archived) "archived" else "watchlist"
                            setTvShowStatus(context, tvShow.showId, newStatus)
                            archived = newStatus == "archived"
                            seen = false
                        },
                        onWatchlistClick = {
                            if (watchlisted) {
                                eraseTvShow(context, tvShow.showId)
                                watchlisted = false
                                seen = false
                                archived = false
                            } else {
                                viewModel.addTvShowToWatchlist(context, tvShow.showId) { ok ->
                                    if (ok) {
                                        watchlisted = true
                                        seen = false
                                        archived = false
                                    }
                                }
                            }
                        },
                        onTrailerClick = { trailerKey?.let { openTrailer(context, it) } })
                })

            1 -> EpisodesTab(
                showId = tvShow.showId,
                seasons = seasons,
                isLoadingSeasons = isLoadingSeasons,
                episodesBySeason = episodesBySeason,
                onEpisodeClick = { ep ->
                    when {
                        !watchlisted -> Toast.makeText(
                            context, "Add the show to your watchlist first", Toast.LENGTH_SHORT
                        ).show()

                        !ep.isWatched && !isNextToWatch(ep, seasons, episodesBySeason) ->
                            pendingEpisode = ep

                        else -> viewModel.toggleEpisodeWatched(context, tvShow.showId, ep)
                    }
                },
                onSeasonChanged = { n -> viewModel.loadEpisodes(context, tvShow.showId, n) },
                modifier = Modifier.weight(1f)
            )
        }

        pendingEpisode?.let { ep ->
            OutOfOrderEpisodeDialog(
                onConfirm = {
                    viewModel.toggleEpisodeWatched(context, tvShow.showId, ep)
                    pendingEpisode = null
                },
                onDismiss = { pendingEpisode = null }
            )
        }
    }
}

@Composable
private fun ButtonArea(
    seen: Boolean = false,
    favourite: Boolean = false,
    archived: Boolean = false,
    watchlisted: Boolean = false,
    trailerAvailable: Boolean = false,
    onSeenClick: () -> Unit = {},
    onFavouriteClick: () -> Unit = {},
    onArchiveClick: () -> Unit = {},
    onWatchlistClick: () -> Unit = {},
    onTrailerClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) { // Button row
        ActionIconButton(
            trailerAvailable,
            R.drawable.ic_play_arrow_24px,
            R.drawable.ic_play_arrow_24px,
            "Trailer button",
            "Trailer button",
            enabled = trailerAvailable,
            onAction = onTrailerClick
        )
        ActionIconButton(
            seen,
            R.drawable.ic_visibility_24px,
            R.drawable.ic_visibility_off_24px,
            "Completed",
            "Not completed",
            onAction = onSeenClick
        )
        ActionIconButton(
            favourite,
            R.drawable.ic_favorite_filled_24px,
            R.drawable.ic_favorite_24px,
            "Favourite",
            "Regular",
            onAction = onFavouriteClick
        )
        ActionIconButton(
            archived,
            R.drawable.ic_archive_24px,
            R.drawable.ic_unarchive_24px,
            "Archived",
            "Unarchived",
            enabled = watchlisted,
            onAction = onArchiveClick
        )
        ActionIconButton(
            watchlisted,
            R.drawable.ic_bookmark_filled_24px,
            R.drawable.ic_bookmark_24dp,
            "Watchlisted",
            "Not watchlisted",
            onAction = onWatchlistClick
        )
    }
}

@Composable
private fun ContentHeader(
    onBackClick: () -> Unit,
    onReloadClick: () -> Unit,
    title: String,
    originalTitle: String? = null,
    releaseDate: LocalDate,
    runtime: Int?,
    rating: Double?,
    posterPath: String?,
) {
    val showOriginal = !originalTitle.isNullOrBlank() &&
            !originalTitle.equals(title, ignoreCase = true)

    val titleSize = when {
        title.length > 40 -> 22.sp
        title.length > 25 -> 26.sp
        else -> 32.sp
    }

    Box(modifier = Modifier.fillMaxWidth()) {

        // 1. Poster
        AsyncImage(
            model = "https://image.tmdb.org/t/p/original$posterPath",
            contentDescription = "$title (poster)",
            contentScale = ContentScale.Crop,
            placeholder = painterResource(MaterialTheme.placeholders.poster),
            error = painterResource(MaterialTheme.placeholders.poster),
            modifier = Modifier
                .matchParentSize() // takes the size of the Column below, so it ends at the divider
        )

        // 2. Gradient overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                            Color.Black.copy(alpha = 0.85f),
                            Color.Black
                        )
                    )
                )
        )

        // 3. Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(28.dp)
                        .clickable(onClick = onBackClick)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(52.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onReloadClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Sync/Reload",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = titleSize,
                lineHeight = titleSize * 1.2f,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = FilmTypography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(2.dp))

            if (showOriginal) {
                Text(
                    text = originalTitle!!,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = FilmTypography.titleSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (runtime != null) {
                    Text(
                        text = "${runtime} min",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = FilmTypography.titleSmall,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .weight(1f)
                    )
                }
                Text(
                    text = releaseDate.toString(),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = FilmTypography.titleSmall,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .weight(1f)
                )
                Text(
                    text = "${rating} ★",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = FilmTypography.titleSmall,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .weight(1f)
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.primary, thickness = 3.dp)
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = FilmTypography.titleMedium
        )
        content()
    }
}

@Composable
private fun ContentBody(
    genres: List<String>,
    overview: String,
    recommendations: List<MultiSearchResult>,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    buttonArea: @Composable () -> Unit
) {
    LazyColumn (modifier = modifier.fillMaxWidth()) {
        item { buttonArea() }

        if (genres.isNotEmpty()) {
            item {
                SectionCard("Genres") {
                    Text(
                        text = genres.joinToString(", "),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp)
                    )
                }
            }
        }

        if (overview.isNotEmpty()) {
            item {
                SectionCard("Overview") {
                    Text(
                        text = overview,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp)
                    )
                }
            }
        }

        if (recommendations.isNotEmpty()) {
            item {
                SectionCard("Recommendations") {
                    ContentCarousel(
                        content = recommendations,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionIconButton(
    boolean: Boolean,
    @DrawableRes activeIcon: Int,
    @DrawableRes inactiveIcon: Int,
    activeDescription: String,
    inactiveDescription: String,
    enabled: Boolean = true,
    onAction: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(enabled = enabled, onClick = onAction),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = if (boolean) activeIcon else inactiveIcon),
            contentDescription = if (boolean) activeDescription else inactiveDescription,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(25.dp)
        )
    }
}

@Composable
private fun ContentCarousel(
    content: List<MultiSearchResult>,
    navController: NavHostController,
) {
    val listState = rememberLazyListState()

    LazyRow(
        state = listState,
        flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp)
    ) {
        items(content, key = { "${it.media_type}-${it.id}" }) { item ->
            ContentCard(
                content = item,
                orientation = true,
                onClick = { ui ->
                    val route = when (ui.mediaType) {
                        MediaType.Film -> "film/${ui.id}"
                        MediaType.TvShow -> "show/${ui.id}"
                    }
                    navController.navigate(route)
                }
            )
        }
    }
}

@Composable
private fun EpisodesTab(
    showId: Int,
    isLoadingSeasons: Boolean,
    episodesBySeason: Map<Int, List<Episode>>,
    onEpisodeClick: (Episode) -> Unit,
    onSeasonChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    seasons: List<Season>
) {
    var expandedSeasons by remember { mutableStateOf<Set<Int>>(emptySet()) }

    when {
        isLoadingSeasons -> Box(
            modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }

        seasons.isEmpty() -> Box(
            modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) { Text("No seasons available", textAlign = TextAlign.Center) }

        else -> LazyColumn(modifier = modifier.fillMaxWidth()) {
            items(seasons, key = { it.seasonNumber }) { season ->
                val n = season.seasonNumber
                val isExpanded = n in expandedSeasons
                episodesBySeason[n]?.let {
                    SeasonDropdown(
                        showId = showId,
                        season = season,
                        episodes = it,
                        expanded = isExpanded,
                        onHeaderClick = {
                            expandedSeasons =
                                if (isExpanded) expandedSeasons - n else expandedSeasons + n
                        },
                        onEpisodeClick = onEpisodeClick,
                        onSeasonChanged = { onSeasonChanged(n) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SeasonDropdown(
    showId: Int,
    season: Season,
    episodes: List<Episode>,
    expanded: Boolean,
    onHeaderClick: () -> Unit,
    onEpisodeClick: (Episode) -> Unit,
    onSeasonChanged: () -> Unit
) {
    val total = episodes.size
    val watched = episodes.count { it.isWatched }
    val seasonAction = rememberSeasonCardAction(
        showId = showId,
        seasonNumber = season.seasonNumber,
        episodes = episodes,
        onChanged = onSeasonChanged
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onHeaderClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = season.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$watched / $total watched",
                    fontSize = 14.sp,
                )
            }
            CardActionButton(
                action = seasonAction,
                modifier = Modifier.size(width = 56.dp, height = 40.dp),
                containerColor = Color.Transparent,
                iconTint = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse season" else "Expand season"
            )
        }

        // TODO:
        //  I want to make a pop-up message when you set seen an episode
        //  that is not the next one to the previous one seen. Do not block, make the warning
        AnimatedVisibility(visible = expanded) {
            Column {
                HorizontalDivider()
                when {
                    episodes == null -> Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }

                    episodes.isEmpty() -> Text(
                        text = "No episodes available for this season",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        style = FilmTypography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    )

                    else -> episodes.forEach { episode ->
                        EpisodeContentCard(
                            episode = episode,
                            isShowScreen = false,
                            onChanged = onSeasonChanged
                        )
                    }
                }
            }
        }
    }
}

// Not working as intended, doesn't pop up/appear when you skip an episode
@Composable
private fun OutOfOrderEpisodeDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.border(1.dp, Gunmetal, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        onDismissRequest = onDismiss,
        containerColor = Graphite,
        titleContentColor = Silver,
        textContentColor = Silver.copy(alpha = 0.7f),
        title = { Text("Not the next episode") },
        text = {
            Text("There are earlier episodes you haven't marked as watched. Do you want to mark this one anyway?")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Silver,
                    contentColor = Graphite
                ),
                modifier = Modifier.height(48.dp),
                shape = RoundedCornerShape(16.dp)
            ) { Text("Mark anyway", color = Graphite) }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Silver,
                    contentColor = Graphite
                ),
                modifier = Modifier.height(48.dp),
                shape = RoundedCornerShape(16.dp)
            ) { Text("Cancel", color = Graphite) }
        }
    )
}

@Composable
@Preview(showBackground = true)
fun FilmDetailScreenPreview() {
    val navController = rememberNavController()
    ContentScreenFilm(filmId = 11, navController = navController)
}