package com.diegogmd.filmfollower.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.diegogmd.filmfollower.R
import com.diegogmd.filmfollower.viewmodels.FilmViewModel
import com.diegogmd.filmfollower.model.Film
import com.diegogmd.filmfollower.getPlaceholderFilm
import com.diegogmd.filmfollower.ui.theme.Black
import com.diegogmd.filmfollower.ui.theme.DarkCoffee
import com.diegogmd.filmfollower.ui.theme.LightCaramel
import com.diegogmd.filmfollower.ui.theme.OliveWood
import com.diegogmd.filmfollower.viewmodels.FilmViewModelFactory

// Remember, this page is for displaying the info from a film/show in full screen

// TODO: consider moving these into ui.theme alongside DarkCoffee / OliveWood

@Composable
fun ContentScreenFilm(
    modifier: Modifier = Modifier,
    filmId: Int,
    onBackClick: () -> Unit = {},
    onTrailerClick: () -> Unit = {},
    onArchiveClick: () -> Unit = {},
    onSeenClick: () -> Unit = {},
    onWatchlistClick: () -> Unit = {},
    viewModel: FilmViewModel = viewModel(factory = FilmViewModelFactory())
) {
    val film by viewModel.film.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(filmId) {
        viewModel.loadFilm(filmId)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LightCaramel)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ContentHeader(
                onBackClick = onBackClick,
                onReloadClick = { viewModel.loadFilm(filmId) }
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (isLoading || film == null) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    FilmDetails(
                        modifier = Modifier.fillMaxSize(),
                        film = film!!
                    )
                }
            }
        }

        if (!isLoading && film != null) {
            PosterSection(
                film = film!!,
                onTrailerClick = onTrailerClick,
                onArchiveClick = onArchiveClick,
                onSeenClick = onSeenClick,
                onWatchlistClick = onWatchlistClick,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 70.dp)   // tune this: how far down from the very top it starts
            )
        }
    }
}

@Composable
private fun ContentHeader(
    onBackClick: () -> Unit,
    onReloadClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkCoffee)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = OliveWood,
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
                contentDescription = "Reload",
                tint = OliveWood,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun FilmDetails(
    modifier: Modifier,
    film: Film
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(280.dp))
        FilmInfoCard(film = film)
        FilmOverview(overview = film.overview)
    }
}

@Composable
private fun PosterSection(
    film: Film,
    onTrailerClick: () -> Unit,
    onArchiveClick: () -> Unit,
    onSeenClick: () -> Unit,
    onWatchlistClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ActionIconButton(
                icon = Icons.Default.PlayArrow,
                contentDescription = "Trailer",
                onClick = onTrailerClick
            )
            ActionIconButton(
                icon = Icons.Default.DateRange,
                contentDescription = "Archive",
                onClick = onArchiveClick
            )
        }

        AsyncImage(
            model = "https://image.tmdb.org/t/p/original${film.posterPath}",
            contentDescription = film.title + " (Image)",
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder_poster),
            error = painterResource(R.drawable.placeholder_poster),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
                .aspectRatio(2f / 3f)
                .offset(y = (-60).dp)
                .clip(RoundedCornerShape(6.dp))
                .border(2.dp, OliveWood, RoundedCornerShape(6.dp))
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ActionIconButton(
                icon = Icons.Default.Face,
                contentDescription = "Seen",
                onClick = onSeenClick
            )
            ActionIconButton(
                icon = Icons.Default.DateRange,
                contentDescription = "Watchlist",
                onClick = onWatchlistClick
            )
        }
    }
}

@Composable
private fun ActionIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(OliveWood)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = LightCaramel,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun FilmInfoCard(film: Film) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 16.dp)
    ) {
        // Background Image
        AsyncImage(
            model = R.drawable.ticket, // Replace with your image URL/model
            contentDescription = "${film.title} poster",
            modifier = Modifier
                .fillMaxSize()
                //.background(Color.LightGray.copy(alpha = 0.5f)) // Optional overlay tint
                .clip(RoundedCornerShape(8.dp))
        )

        // Content overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .padding(vertical = 20.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = film.title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${film.releaseDate}",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Black
                )
                Text(
                    text = "${film.rating}",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Black
                )
            }
        }
    }
}

@Composable
private fun FilmOverview(overview: String) {
    Text(
        text = overview,
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        color = Black,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    )
}

@Composable
fun FilmInfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

// Main screen composables
@Composable
fun FilmDetailScreen(film: Film? = getPlaceholderFilm(11)) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OliveWood)
    ) {
        // Fixed header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkCoffee)
                .padding(vertical = 24.dp, horizontal = 16.dp)
        ) {
            Text(
                text = film?.title ?: "Unknown Film",
                color = Color.White,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            )
        }

        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Rating section
            Text(
                text = "⭐ Rating: ${film?.rating ?: 0.0}",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Film details grid
            FilmInfoRow("Title", film?.title ?: "")
            FilmInfoRow("Original Title", film?.originalTitle ?: "")
            FilmInfoRow("Release Date", film?.releaseDate?.toString() ?: "N/A")
            FilmInfoRow("Runtime", "${film?.runtime ?: 0} min")
            FilmInfoRow("TMDB Status", film?.tmdbStatus ?: "")
            FilmInfoRow("Watch Status", film?.watchStatus ?: "")

            // Nullable watched date
            FilmInfoRow(
                "Watched Date",
                film?.watchedDate?.toString() ?: "Not watched yet"
            )

            FilmInfoRow("Times Watched", "${film?.timesWatched ?: 0}")
            FilmInfoRow("Added At", film?.addedAt?.toString() ?: "")

            // Overview
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Overview",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = film?.overview ?: "No overview available.",
                fontSize = 15.sp,
                lineHeight = 24.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilmDetailScreenPreview() {
    //FilmDetailScreen()
    ContentScreenFilm(filmId = 11)
}

@Composable
fun ContentPageShowLayout() {
    TODO("Not yet implemented")
}