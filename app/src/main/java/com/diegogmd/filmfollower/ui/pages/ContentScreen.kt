package com.diegogmd.filmfollower.ui.pages

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.diegogmd.filmfollower.R
import com.diegogmd.filmfollower.viewmodels.FilmViewModel
import com.diegogmd.filmfollower.model.Film
import com.diegogmd.filmfollower.model.TvShow
import com.diegogmd.filmfollower.model.getFilmGenre
import com.diegogmd.filmfollower.ui.theme.DarkCoffee
import com.diegogmd.filmfollower.ui.theme.FilmTypography
import com.diegogmd.filmfollower.ui.theme.LightCaramel
import com.diegogmd.filmfollower.viewmodels.FilmViewModelFactory
import com.diegogmd.filmfollower.viewmodels.TvShowViewModel
import com.diegogmd.filmfollower.viewmodels.TvShowViewModelFactory
import org.threeten.bp.LocalDate

// Remember, this page is for displaying the info from a film/show in full screen

@Composable
fun ContentScreenFilm(
    filmId: Int,
    onBackClick: () -> Unit = {},
    viewModel: FilmViewModel = viewModel(factory = FilmViewModelFactory())
) {
    val context = LocalContext.current
    val film by viewModel.film.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(filmId) {
        viewModel.loadFilm(context, filmId)
    }

    when {
        isLoading -> CircularProgressIndicator()
        film != null -> FilmContentUI(onBackClick, viewModel, film!!)
    }
}

@Composable
fun ContentScreenTvShow(
    showId: Int,
    onBackClick: () -> Unit = {},
    viewModel: TvShowViewModel = viewModel(factory = TvShowViewModelFactory())
) {
    val context = LocalContext.current
    val tvShow by viewModel.tvShow.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(showId) {
        viewModel.loadFilm(context, showId)
    }

    when {
        isLoading -> CircularProgressIndicator()
        tvShow != null -> TvShowContentUI(onBackClick, viewModel, tvShow!!)
    }
}

@Composable
private fun FilmContentUI(
    onBackClick: () -> Unit,
    viewModel: FilmViewModel,
    film: Film
) {
    val genres by viewModel.genres.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCoffee)
    ) {
        ContentHeader(
            onBackClick = onBackClick,
            //onReloadClick = { viewModel.loadFilm(film.filmId) }, // Online content
            film.title,
            film.originalTitle,
            film.releaseDate,
            film.runtime,
            film.rating,
            film.posterPath
        )

        ButtonArea()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, DarkCoffee)
        ) {
            if (genres.isNotEmpty()) {
                Text(
                    text = "Genres",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = DarkCoffee,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = FilmTypography.titleMedium
                )
                Text(
                    text = genres,
                    color = DarkCoffee,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp)
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, DarkCoffee)
        ) {
            Text(
                text = "Overview",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = DarkCoffee,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = FilmTypography.titleMedium
            )
            Text(
                text = film.overview,
                color = DarkCoffee,
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, DarkCoffee)
        ) {
            Text(
                text = "Similar",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = DarkCoffee,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = FilmTypography.titleMedium
            )
            Text(
                text = "HorizontalFilmCards Carousel, that slides horizontally with 5 options that re-appear in cycle",
                color = DarkCoffee,
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp)
            )
        }
    }
}

@Composable
private fun TvShowContentUI(
    onBackClick: () -> Unit,
    viewModel: TvShowViewModel,
    tvShow: TvShow
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCoffee)
    ) {
        ContentHeader(
            onBackClick = onBackClick,
            //onReloadClick = { viewModel.loadFilm(film.filmId) }, // Online content
            tvShow.title,
            tvShow.originalTitle,
            tvShow.firstAirDate,
            0,
            tvShow.rating,
            tvShow.posterPath
        )

        ButtonArea()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, DarkCoffee)
        ) {
            Text(
                text = "Genres",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = DarkCoffee,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = FilmTypography.titleMedium
            )
            Text(
                text = getFilmGenre(LocalContext.current, tvShow.showId),
                color = DarkCoffee,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, DarkCoffee)
        ) {
            Text(
                text = "Overview",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = DarkCoffee,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = FilmTypography.titleMedium
            )
            Text(
                text = tvShow.overview,
                color = DarkCoffee,
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, DarkCoffee)
        ) {
            Text(
                text = "Similar",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = DarkCoffee,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = FilmTypography.titleMedium
            )
            Text(
                text = "HorizontalFilmCards Carousel, that slides horizontally with 5 options that re-appear in cycle",
                color = DarkCoffee,
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp)
            )
        }
    }
}

@Composable
private fun ButtonArea() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) { // Button row
        ActionIconButton(R.drawable.ic_play_arrow_24px, "Trailer")
        ActionIconButton(R.drawable.ic_visibility_24px, "Seen")
        ActionIconButton(R.drawable.ic_favorite_24px, "Favourite")
        ActionIconButton(R.drawable.ic_archive_24px, "Archived")
        ActionIconButton(R.drawable.ic_bookmark_24dp, "Watchlist")
    }
}

@Composable
private fun ContentHeader(
    onBackClick: () -> Unit,
    //onReloadClick: () -> Unit,
    title: String,
    originalTitle: String? = null,
    releaseDate: LocalDate,
    runtime: Int,
    rating: Double?,
    posterPath: String?

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
            placeholder = painterResource(R.drawable.placeholder_poster),
            error = painterResource(R.drawable.placeholder_poster),
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
                            DarkCoffee.copy(alpha = 0.55f),
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
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = LightCaramel,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(28.dp)
                        .clickable(onClick = onBackClick)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(52.dp)
                        .clip(CircleShape),
                        //.clickable(onClick = onReloadClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reload",
                        tint = LightCaramel,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = title,
                color = LightCaramel,
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
                    color = LightCaramel,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = FilmTypography.titleSmall,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
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
                Text(
                    text = "${runtime} min",
                    color = LightCaramel,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = FilmTypography.titleSmall,
                    modifier = Modifier.padding(horizontal = 4.dp).weight(1f)
                )
                Text(
                    text = releaseDate.toString(),
                    color = LightCaramel,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = FilmTypography.titleSmall,
                    modifier = Modifier.padding(horizontal = 4.dp).weight(1f)
                )
                Text(
                    text = "${rating} ★",
                    color = LightCaramel,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = FilmTypography.titleSmall,
                    modifier = Modifier.padding(horizontal = 4.dp).weight(1f)
                )
            }

            HorizontalDivider(
                color = LightCaramel,
                thickness = 3.dp,
            )
        }
    }
}

@Composable
private fun ActionIconButton(
    @DrawableRes icon: Int, // R.drawable.ic_play_arrow_24px
    description: String,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(LightCaramel)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = description,
            tint = DarkCoffee,
            modifier = Modifier.size(25.dp)
        )
    }
}

fun ContentScreenEpisode(
    showId: Int,
    onBackClick: () -> Unit = {},
    //viewModel: FilmViewModel = viewModel(factory = FilmViewModelFactory())
) {
//    val context = LocalContext.current
//    val film by viewModel.film.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
//
//    LaunchedEffect(filmId) {
//        viewModel.loadFilm(context, filmId)
//    }
//
//    when {
//        isLoading -> CircularProgressIndicator()
//        film != null -> FilmContentUI(onBackClick, viewModel, film!!)
//        else -> Text("Film not found")
//    }
}

@Preview(showBackground = true)
@Composable
fun FilmDetailScreenPreview() {
    ContentScreenFilm(filmId = 11)
}