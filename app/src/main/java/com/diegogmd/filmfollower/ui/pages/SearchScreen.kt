package com.diegogmd.filmfollower.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.diegogmd.filmfollower.R
import com.diegogmd.filmfollower.viewmodels.SearchViewModel
import com.diegogmd.filmfollower.ui.components.ContentCard
import com.diegogmd.filmfollower.ui.components.NoInternetCard
import com.diegogmd.filmfollower.ui.theme.DarkCoffee
import com.diegogmd.filmfollower.ui.theme.FadedCopper
import com.diegogmd.filmfollower.ui.theme.LightCaramel
import com.diegogmd.filmfollower.util.rememberIsOnline
import com.diegogmd.filmfollower.viewmodels.SearchViewModelFactory

@Composable
fun SearchScreen(
    modifier: Modifier,
    navController: NavHostController,
    viewModel: SearchViewModel = viewModel(factory = SearchViewModelFactory())
) {
    val isOnline by rememberIsOnline()
    val textFieldState = remember { TextFieldState() }
    val results by viewModel.results.collectAsState()
    val trending by viewModel.trending.collectAsState()

    LaunchedEffect(isOnline) {
        if (isOnline && trending.isEmpty()) viewModel.loadTrending()
    }

    val query = textFieldState.text.toString()
    val listToShow = if (query.isBlank()) trending else results

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { traversalIndex = 0f }
    ) {
        // Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkCoffee)
                .padding(8.dp)
        ) {
            TextField(
                value = query,
                onValueChange = { newQuery ->
                    textFieldState.edit { replace(0, length, newQuery) }
                    viewModel.onQueryChanged(newQuery) // debounced internally
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(30.dp)
                    ),
                shape = RoundedCornerShape(30.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LightCaramel,
                    unfocusedContainerColor = LightCaramel,
                    disabledContainerColor = FadedCopper,
                    cursorColor = DarkCoffee,
                    focusedTextColor = DarkCoffee,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                placeholder = {
                    Text(
                        text = stringResource(R.string.search_bar),
                        color = DarkCoffee
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = DarkCoffee
                    )
                }
            )
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .semantics { traversalIndex = 0f }
        ) {
            if (isOnline) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(listToShow) { item ->
                        ContentCard(
                            content = item,
                            orientation = false,
                            onClick = { id ->
                                if (item.media_type == "movie") navController.navigate("film/$id")
                                if (item.media_type == "tv") navController.navigate("show/$id")
                            }
                        )
                    }
                }
            } else {
                NoInternetCard(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
        }
    }
}