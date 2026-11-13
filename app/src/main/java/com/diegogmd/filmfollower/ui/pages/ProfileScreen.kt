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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.diegogmd.filmfollower.SecureStorage
import com.diegogmd.filmfollower.model.getAllFavouriteFilms
import com.diegogmd.filmfollower.model.getAllFavouriteTvShows
import com.diegogmd.filmfollower.model.getAllFilmStatus
import com.diegogmd.filmfollower.model.getAllTvShowStatus
import com.diegogmd.filmfollower.ui.components.ContentCard
import com.diegogmd.filmfollower.ui.components.ContentCardUi
import com.diegogmd.filmfollower.ui.components.MediaType
import com.diegogmd.filmfollower.ui.components.toCardUi
import com.diegogmd.filmfollower.ui.theme.FilmTypography

@Composable
fun ProfileScreen(modifier: Modifier, navController: NavHostController) {
    val context = LocalContext.current
    var refreshTick by remember { mutableIntStateOf(0) }
    val favouriteFilms =
        remember(refreshTick) { getAllFavouriteFilms(context)?.map { it.toCardUi() } }
    val favouriteShows =
        remember(refreshTick) { getAllFavouriteTvShows(context)?.map { it.toCardUi() } }
    val seenFilms =
        remember(refreshTick) { getAllFilmStatus(context, "seen")?.map { it.toCardUi() } }
    val seenShows =
        remember(refreshTick) { getAllTvShowStatus(context, "completed")?.map { it.toCardUi() } }

    Column {
        SecureStorage.getUsername(context)?.let { ProfileHeader(it, navController) }

        LazyColumn(modifier = Modifier.weight(1f)) {
            // TODO:
            //  I want it to be a list of all the last seen content (episodes and films)
            //  I don't want to add tv shows so there is no duplicates
            if (favouriteFilms != null) {
                item {
                    CarouselSection(
                        "Last Seen", favouriteFilms.count(), favouriteFilms, navController
                    ) { refreshTick++ }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    NavigationCard(
                        label = "Statistics", modifier = Modifier.weight(1f)
                    ) { /* Go to statistics page */ }

                    NavigationCard(
                        label = "Lists", modifier = Modifier.weight(1f)
                    ) { /* Go to lists page */ }
                }
            }

            if (favouriteFilms != null) {
                item {
                    CarouselSection(
                        "Favourite Films", favouriteFilms.count(), favouriteFilms, navController
                    ) { refreshTick++ }
                }
            }
            if (favouriteShows != null) {
                item {
                    CarouselSection(
                        "Favourite Tv Shows", favouriteShows.count(), favouriteShows, navController
                    ) { refreshTick++ }
                }
            }
            if (seenFilms != null) {
                item {
                    CarouselSection(
                        "Seen Films",
                        seenFilms.count(),
                        seenFilms,
                        navController
                    ) { refreshTick++ }
                }
            }
            if (seenShows != null) {
                item {
                    CarouselSection(
                        "Seen Tv Shows",
                        seenShows.count(),
                        seenShows,
                        navController
                    ) { refreshTick++ }
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
fun NavigationCard(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 16.sp)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Go to $label",
                modifier = Modifier.size(20.dp)
            )
        }
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

            HorizontalDivider(color = MaterialTheme.colorScheme.primary, thickness = 3.dp)
        }
    }
}

@Composable
fun CarouselSection(
    title: String,
    itemCount: Int?,
    content: List<ContentCardUi?>,
    navController: NavHostController,
    onChanged: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
    ) {
        val listState = rememberLazyListState()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                style = FilmTypography.titleMedium,
                modifier = Modifier.align(Alignment.Center)
            )

            if (itemCount != null) {
                Text(
                    text = "( $itemCount )",
                    fontSize = 20.sp,
                    style = FilmTypography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }

        LazyRow(
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp)
        ) {
            items(content, key = { "${it?.mediaType}-${it?.id}" }) { item ->
                if (item != null) {
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
    }
}

@Composable
@Preview(showBackground = true)
fun ProfileScreenPreview() {
    ProfileScreen(
        modifier = Modifier,
        navController = rememberNavController()
    )
}