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
import com.diegogmd.filmfollower.model.Episode
import com.diegogmd.filmfollower.model.getAllFilmStatus
import com.diegogmd.filmfollower.model.getAllTvShowStatus
import com.diegogmd.filmfollower.model.getNextEpisodesToWatch
import com.diegogmd.filmfollower.samplePlaceholderEpisodes
import com.diegogmd.filmfollower.ui.components.CardType
import com.diegogmd.filmfollower.ui.components.ContentCard
import com.diegogmd.filmfollower.ui.components.EmptyContentCard
import com.diegogmd.filmfollower.ui.components.EpisodeContentCard
import com.diegogmd.filmfollower.ui.theme.FilmTypography

@Composable
fun ShowsScreen(modifier: Modifier, navController: NavHostController){
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
                    onClick = {selectedTab = index},
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
        val releasedNextEpisodes = remember(refreshTick) {
            val shows = getAllTvShowStatus(context, "watchlist", 1)
            if (shows != null) getNextEpisodesToWatch(context, shows.map { it.showId }) else null
        }

        when (selectedTab) {
            0 -> if (releasedNextEpisodes != null) {
                EpisodeList(Modifier.weight(1f),
                    episodes = releasedNextEpisodes,
                    navController,
                    onChanged = { refreshTick++ })
            } else {
                EmptyContentCard(modifier, true)
            }

            1 -> EmptyContentCard(modifier, true)
        }
    }
}

@Composable
private fun EpisodeList(
    modifier: Modifier,
    episodes: List<Episode>,
    navController: NavHostController,
    onChanged: () -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        items(episodes, key = { it.showId }) { episode ->
            EpisodeContentCard(
                episode = episode,
                onClick = { item ->
                    navController.navigate(
                        "show/${episode.showId}"
                    )
                },
                onChanged = onChanged
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ShowsScreenPreview() {
    val navController = NavHostController(LocalContext.current)
    ShowsScreen(
        modifier = Modifier,
        navController = navController
    )
}