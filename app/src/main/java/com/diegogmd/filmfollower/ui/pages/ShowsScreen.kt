package com.diegogmd.filmfollower.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.diegogmd.filmfollower.R
import com.diegogmd.filmfollower.model.Episode
import com.diegogmd.filmfollower.model.getAllTvShowStatus
import com.diegogmd.filmfollower.model.getNextEpisodesToWatch
import com.diegogmd.filmfollower.ui.components.EmptyContentCard
import com.diegogmd.filmfollower.ui.components.EpisodeContentCard
import com.diegogmd.filmfollower.ui.theme.FilmTypography
import com.diegogmd.filmfollower.ui.theme.Gunmetal

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

        val unreleasedNextEpisodes = remember(refreshTick) {
            val shows = getAllTvShowStatus(context, "watchlist", 0)
            if (shows != null) getNextEpisodesToWatch(context, shows.map { it.showId }) else null
        }

        val archivedReleasedNextEpisodes = remember(refreshTick) {
            val shows = getAllTvShowStatus(context, "archived", 1)
            if (shows != null) getNextEpisodesToWatch(context, shows.map { it.showId }) else null
        }

        when (selectedTab) {
            0 -> if (releasedNextEpisodes != null) {
                EpisodeList(
                    Modifier.weight(1f),
                    episodes = releasedNextEpisodes,
                    archivedEpisodes = archivedReleasedNextEpisodes,
                    navController,
                    onChanged = { refreshTick++ }
                )
            } else {
                EmptyContentCard(modifier, true)
            }

            1 -> if (unreleasedNextEpisodes != null) {
                EpisodeList(
                    Modifier.weight(1f),
                    episodes = unreleasedNextEpisodes,
                    archivedEpisodes = null,
                    navController,
                    onChanged = { refreshTick++ }
                )
            } else {
                EmptyContentCard(modifier, true)
            }
        }
    }
}

@Composable
private fun EpisodeList(
    modifier: Modifier,
    episodes: List<Episode>,
    archivedEpisodes: List<Episode>? = null,
    navController: NavHostController,
    onChanged: () -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        items(
            items = episodes,
            key = { "${it.showId}-${it.seasonNumber}-${it.episodeNumber}" }
        ) { episode ->
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

        if(!archivedEpisodes.isNullOrEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "Archived TV Shows",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            items(
                items = archivedEpisodes,
                key = { "${it.showId}-${it.seasonNumber}-${it.episodeNumber}" }
            ) { episode ->
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
}

@Composable
@Preview(showBackground = true)
fun ShowsScreenPreview() {
    ShowsScreen(
        modifier = Modifier,
        navController = rememberNavController()
    )
}