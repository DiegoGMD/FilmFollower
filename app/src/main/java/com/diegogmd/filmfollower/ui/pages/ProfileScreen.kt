package com.diegogmd.filmfollower.ui.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.diegogmd.filmfollower.SecureStorage
import com.diegogmd.filmfollower.model.Film
import com.diegogmd.filmfollower.model.TvShow
import com.diegogmd.filmfollower.model.getAllFavouriteFilms
import com.diegogmd.filmfollower.model.getAllFilmStatus
import com.diegogmd.filmfollower.ui.components.ContentCard
import com.diegogmd.filmfollower.ui.theme.FilmTypography

@Composable
fun ProfileScreen(modifier: Modifier, navController: NavHostController) {
    var context = LocalContext.current
    var refreshTick by remember { mutableIntStateOf(0) }
    val favouriteFilms = remember(refreshTick) { getAllFavouriteFilms(context) }
    val favouriteShows = remember(refreshTick) { getAllFavouriteFilms(context) }
    val seenFilms = remember(refreshTick) { getAllFilmStatus(context, "seen") }
    val seenShows = remember(refreshTick) { getAllFilmStatus(context, "seen") }

    Column {
        SecureStorage.getUsername(context)?.let { ProfileHeader(it, navController) }

        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                Row {
                    StatCard(
                        count = seenFilms?.count() ?: 0,
                        label = "Seen Films",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        count = seenShows?.count() ?: 0,
                        label = "Seen Shows",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (favouriteFilms != null) {
                item {
                    FilmCarouselSection(
                        "Favourite Films", favouriteFilms, navController
                    ) { refreshTick++ }
                }
            }
            if (favouriteShows != null) {
                item {
                    FilmCarouselSection(
                        "Favourite Tv Shows", favouriteShows, navController
                    ) { refreshTick++ }
                }
            }
            if (seenFilms != null) {
                item {
                    FilmCarouselSection("Seen Films", seenFilms, navController) { refreshTick++ }
                }
            }
        }
    }
}

@Composable
fun StatCard(count: Int, label: String, modifier: Modifier) {
    Card(
        modifier = modifier.padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count.toString(), style = FilmTypography.titleMedium, fontSize = 32.sp)
            Text(text = label, style = FilmTypography.titleMedium, fontSize = 14.sp)
        }
    }
}

@Composable
fun FilmCarouselSection(
    title: String, films: List<Film>, navController: NavHostController, onChanged: () -> Unit
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
        ContentCarousel(films = films, navController = navController, onChanged = onChanged)
    }
}

@Composable
fun TvShowCarouselSection(
    title: String, tvShows: List<TvShow>, navController: NavHostController, onChanged: () -> Unit
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
        //ContentCarousel(tvShows = tvShows, navController = navController, onChanged = onChanged)
    }
}

@Composable
private fun ProfileHeader(
    username: String, navController: NavHostController

) {
    val titleSize = when {
        username.length > 40 -> 22.sp
        username.length > 25 -> 26.sp
        else -> 32.sp
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        // 1. Gradient overlay
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

        // 2. Content
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(52.dp)
                        .clip(CircleShape)
                        .clickable { navController.navigate("SettingsScreen") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = username,
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

            HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 3.dp)
        }
    }
}

@Composable
private fun ContentCarousel(
    modifier: Modifier = Modifier,
    films: List<Film>,
    navController: NavHostController,
    onChanged: () -> Unit
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
        items(films, key = { it.filmId }) { film ->
            ContentCard(
                content = film,
                orientation = true,
                cardType = 0,
                onClick = { id -> navController.navigate("film/$id") },
                onChanged = onChanged
            )
        }
    }
}

//@Composable
//private fun ContentCarousel(
//    modifier: Modifier = Modifier,
//    tvShows: List<TvShow>,
//    navController: NavHostController,
//    onChanged: () -> Unit
//) {
//    val listState = rememberLazyListState()
//
//    LazyRow(
//        state = listState,
//        flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
//        contentPadding = PaddingValues(horizontal = 24.dp),
//        horizontalArrangement = Arrangement.spacedBy(16.dp),
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(top = 8.dp, bottom = 8.dp)
//    ) {
//        items(tvShows, key = { it.showId }) { tvShow ->
//            ContentCard(
//                content = tvShow,
//                orientation = true,
//                cardType = 0,
//                onClick = { id -> navController.navigate("show/$id") },
//                onChanged = onChanged
//            )
//        }
//    }
//}