package com.diegogmd.filmfollower.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.navigation.NavHostController
import com.diegogmd.filmfollower.R
import com.diegogmd.filmfollower.model.Film
import com.diegogmd.filmfollower.model.anyWatchlistedReleasedFilm
import com.diegogmd.filmfollower.model.anyWatchlistedUpcomingFilm
import com.diegogmd.filmfollower.model.getWatchlistedReleasedFilms
import com.diegogmd.filmfollower.model.getWatchlistedUpcomingFilms
import com.diegogmd.filmfollower.ui.components.ContentCard
import com.diegogmd.filmfollower.ui.components.EmptyContentCard
import com.diegogmd.filmfollower.ui.theme.DarkCoffee
import com.diegogmd.filmfollower.ui.theme.FilmTypography
import com.diegogmd.filmfollower.ui.theme.LightCaramel

@Composable
fun FilmsScreen(modifier: Modifier, navController: NavHostController) {
    val tabs = listOf(R.string.watch_list, R.string.upcoming)
    var selectedTab by remember { mutableIntStateOf(0) }
    var context = LocalContext.current

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
            contentColor = LightCaramel,
            containerColor = DarkCoffee,
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

        when (selectedTab) {
            0 -> if (anyWatchlistedReleasedFilm(context)) {
                FilmList(
                    Modifier.weight(1f),
                    films = getWatchlistedReleasedFilms(context),
                    navController
                )
            } else {
                EmptyContentCard(modifier, true)
            }

            1 -> if (anyWatchlistedUpcomingFilm(context)) {
                FilmList(
                    Modifier.weight(1f),
                    films = getWatchlistedUpcomingFilms(context),
                    navController
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
private fun FilmList(modifier: Modifier, films: List<Film>, navController: NavHostController) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        items(films, key = { it.filmId }) { film ->
            ContentCard(film, onClick = { id -> navController.navigate("film/$id") })
        }
    }
}