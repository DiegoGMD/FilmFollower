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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.diegogmd.filmfollower.R
import com.diegogmd.filmfollower.model.Episode
import com.diegogmd.filmfollower.model.getTvShow
import com.diegogmd.filmfollower.samplePlaceholderEpisodes
import com.diegogmd.filmfollower.ui.compònents.EpisodeContentCard
import com.diegogmd.filmfollower.ui.theme.DarkCoffee
import com.diegogmd.filmfollower.ui.theme.LightCaramel

@Composable
fun ShowsPage(modifier: Modifier, navController: NavHostController){
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
                    onClick = {selectedTab = index},
                    text = { Text(stringResource(id = title)) }
                )
            }
        }

        when (selectedTab) {
            0 -> EpisodeList(Modifier.weight(1f), samplePlaceholderEpisodes())
            1 -> EpisodeList(Modifier.weight(1f), samplePlaceholderEpisodes())
        }
    }
}

@Composable
private fun EpisodeList(modifier: Modifier, episodes: List<Episode>) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)
    ) {
        items(episodes, key = { it.showId }) { episode ->
            val context = LocalContext.current
            val tvShow = getTvShow(context, episode.showId)
            if (tvShow != null) {
                val tvShowName = tvShow.title
                val tvShowPoster = tvShow.poster_path

                EpisodeContentCard(tvShowName, episode.seasonNumber, episode.episodeNumber,
                    episode.title, tvShowPoster)
            }
        }
    }
}