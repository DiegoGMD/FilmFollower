package com.diegogmd.filmfollower.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.diegogmd.filmfollower.R
import com.diegogmd.filmfollower.model.Film
import com.diegogmd.filmfollower.model.getAllFilmStatus
import com.diegogmd.filmfollower.ui.components.ContentCard
import com.diegogmd.filmfollower.ui.components.EmptyContentCard
import com.diegogmd.filmfollower.ui.theme.FilmTypography

@Composable
fun FilmsScreen(modifier: Modifier, navController: NavHostController) {
    val tabs = listOf(R.string.watch_list, R.string.upcoming)
    var selectedTab by remember { mutableIntStateOf(0) }
    var context = LocalContext.current

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = stringResource(id = title),
                            style = FilmTypography.labelMedium
                        )
                    }
                )
            }
        }

        var refreshTick by remember { mutableIntStateOf(0) }
        val watchlistedReleasedFilms =
            remember(refreshTick) { getAllFilmStatus(context, "watchlist", 1) }
        val watchlistedUpcomingFilms =
            remember(refreshTick) { getAllFilmStatus(context, "watchlist", 0) }

        when (selectedTab) {
            0 -> if (watchlistedReleasedFilms != null) {
                FilmList(
                    Modifier.weight(1f),
                    films = watchlistedReleasedFilms,
                    navController,
                    onChanged = { refreshTick++ }
                )
            } else {
                EmptyContentCard(modifier, true)
            }

            1 -> if (watchlistedUpcomingFilms != null) {
                FilmList(
                    Modifier.weight(1f),
                    films = watchlistedUpcomingFilms,
                    navController,
                    onChanged = { refreshTick++ }
                )
            } else {
                EmptyContentCard(modifier, true)
            }
        }

//        when (selectedTab) {
//            0 -> FilmList(Modifier.weight(1f), films = samplePlaceholderFilms(), navController)
//            1 -> FilmList(Modifier.weight(1f), films = samplePlaceholderUpcomingFilms(), navController)
//        }
    }
}

@Composable
private fun FilmList(
    modifier: Modifier,
    films: List<Film>,
    navController: NavHostController,
    onChanged: () -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        items(films, key = { it.filmId }) { film ->
            ContentCard(
                content = film,
                cardType = 1,
                onClick = { id -> navController.navigate("film/$id") },
                onChanged = onChanged
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun FilmsScreenPreview() {
    val navController = NavHostController(LocalContext.current)
    FilmsScreen(
        modifier = Modifier,
        navController = navController
    )
}