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
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.diegogmd.filmfollower.R
import com.diegogmd.filmfollower.model.Film
import com.diegogmd.filmfollower.samplePlaceholderFilms
import com.diegogmd.filmfollower.ui.compònents.ContentCard
import com.diegogmd.filmfollower.ui.theme.DarkCoffee
import com.diegogmd.filmfollower.ui.theme.LightCaramel

@Composable
fun FilmsPage(modifier: Modifier, navController: NavHostController) {
    val tabs = listOf(R.string.watch_list, R.string.upcoming)
    var selectedTab by remember { mutableIntStateOf(0) }

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
                    text = { Text(stringResource(id = title)) }
                )
            }
        }

        when (selectedTab) {
            0 -> FilmList(Modifier.weight(1f), films = samplePlaceholderFilms())
            1 -> FilmList(Modifier.weight(1f), films = samplePlaceholderFilms())
        }
    }
}

@Composable
private fun FilmList(modifier: Modifier, films: List<Film>) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        items(films, key = { it.filmId }) { film ->
            ContentCard(film.title, film.releaseDate, film.rating, film.posterPath)
        }
    }
}