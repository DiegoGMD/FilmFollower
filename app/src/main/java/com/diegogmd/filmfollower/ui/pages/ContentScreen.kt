package com.diegogmd.filmfollower.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.diegogmd.filmfollower.R
import com.diegogmd.filmfollower.data.repository.SearchRepository
import com.diegogmd.filmfollower.data.local.remote.tmdbApi
import com.diegogmd.filmfollower.viewmodels.FilmViewModel
import com.diegogmd.filmfollower.model.Film
import com.diegogmd.filmfollower.getPlaceholderFilm
import com.diegogmd.filmfollower.ui.theme.DarkCoffee
import com.diegogmd.filmfollower.ui.theme.OliveWood

@Composable
fun ContentPageFilm(
    modifier: Modifier,
    filmId: Int,
    viewModel: FilmViewModel = viewModel(factory = FilmViewModelFactory())
) {
    val film by viewModel.film.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(filmId) {
        viewModel.loadFilm(filmId)
    }

    if (film == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        FilmDetails(modifier = modifier, film = film!!)
    }
}

@Composable
private fun FilmDetails(modifier: Modifier, film: Film) {
    Column(modifier = modifier.fillMaxWidth()) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${film.posterPath}",
            contentDescription = film.title + " (Image)",
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder_poster),
            error = painterResource(R.drawable.placeholder_poster),
            modifier = Modifier.size(100.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = film.title)
            if (film.originalTitle != film.title) {
                Text(text = film.originalTitle)
            }
            Text(text = "${film.releaseDate} • ${film.runtime} min")
            Text(text = "★ ${film.rating}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = film.overview)
        }
    }
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
fun FilmDetailScreen(film: Film? = getPlaceholderFilm(120)) {
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
    FilmDetailScreen()
}

@Composable
fun ContentPageShowLayout() {
    TODO("Not yet implemented")
}

class FilmViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FilmViewModel(SearchRepository(tmdbApi)) as T
    }
}