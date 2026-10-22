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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.diegogmd.filmfollower.model.getAllFilmStatus
import com.diegogmd.filmfollower.ui.components.ContentCard
import com.diegogmd.filmfollower.ui.theme.FilmTypography

@Composable
fun ProfileScreen(modifier: Modifier, navController: NavHostController) { // Has to be scrollable
    var context = LocalContext.current
    var refreshTick by remember { mutableIntStateOf(0) }
    val favouriteFilms = remember(refreshTick) { getAllFilmStatus(context, "seen") }
    val favouriteShows = remember(refreshTick) { getAllFilmStatus(context, "seen") }
    val seenFilms = remember(refreshTick) { getAllFilmStatus(context, "seen") }
    val seenShows = remember(refreshTick) { getAllFilmStatus(context, "seen") }

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        SecureStorage.getUsername(context)?.let { ProfileHeader(it, navController) }

        Row {
            Card(
                modifier = Modifier.weight(1f).padding(8.dp),
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
                    Text(
                        text = (seenFilms?.count() ?: 0).toString(),
                        style = FilmTypography.titleMedium,
                        fontSize = 32.sp
                    )
                    Text(
                        text = "Seen Films",
                        style = FilmTypography.titleMedium,
                        fontSize = 14.sp
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f).padding(8.dp),
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
                    Text(
                        text = (seenShows?.count() ?: 0).toString(),
                        style = FilmTypography.titleMedium,
                        fontSize = 32.sp
                    )
                    Text(
                        text = "Seen Shows",
                        style = FilmTypography.titleMedium,
                        fontSize = 14.sp
                    )
                }
            }
        }

        if (favouriteFilms != null) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
            ) {
                Text(
                    text = "Favourite Films",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = FilmTypography.titleMedium
                )
                ContentCarousel(
                    modifier = modifier,
                    films = favouriteFilms,
                    navController = navController,
                    onChanged = { refreshTick++ }
                )
            }
        }

        if (favouriteShows != null) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
            ) {
                Text(
                    text = "Favourite Tv Shows",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = FilmTypography.titleMedium
                )
                ContentCarousel(
                    modifier = modifier,
                    films = favouriteShows,
                    navController = navController,
                    onChanged = { refreshTick++ }
                )
            }
        }

        if (seenFilms != null) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
            ) {
                Text(
                    text = "Seen Films",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = FilmTypography.titleMedium
                )
                ContentCarousel(
                    modifier = modifier,
                    films = seenFilms,
                    navController = navController,
                    onChanged = { refreshTick++ }
                )
            }
        }

//        if (seenShows != null) {
//            Card(
//                modifier = Modifier.fillMaxWidth().padding(8.dp),
//                shape = RoundedCornerShape(12.dp),
//                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
//                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
//            ) {
//                Text(
//                    text = "Seen Tv Shows",
//                    fontSize = 20.sp,
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier.align(Alignment.CenterHorizontally),
//                    style = FilmTypography.titleMedium
//                )
//                ContentCarousel(
//                    modifier = modifier,
//                    films = seenShows,
//                    navController = navController,
//                    onChanged = { refreshTick++ }
//                )
//            }
//        }
    }
}

@Composable
private fun ProfileHeader(
    username: String,
    navController: NavHostController

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

            HorizontalDivider( color = MaterialTheme.colorScheme.surface, thickness = 3.dp )
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
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp)
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