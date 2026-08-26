package com.diegogmd.filmfollower.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.diegogmd.filmfollower.R
import com.diegogmd.filmfollower.data.repository.SearchRepository
import com.diegogmd.filmfollower.viewmodels.SearchViewModel
import com.diegogmd.filmfollower.data.local.remote.tmdbApi
import com.diegogmd.filmfollower.ui.compònents.ContentCard
import com.diegogmd.filmfollower.ui.theme.DarkCoffee
import com.diegogmd.filmfollower.ui.theme.FadedCopper
import com.diegogmd.filmfollower.ui.theme.LightCaramel

@Composable
fun SearchPage(
    modifier: Modifier,
    navController: NavHostController,
    viewModel: SearchViewModel = viewModel(factory = SearchViewModelFactory())
) {
    val textFieldState = remember { TextFieldState() }
    val results by viewModel.results.collectAsState()
    val trending by viewModel.trending.collectAsState()

    val query = textFieldState.text.toString()
    val listToShow = if (query.isBlank()) trending else results

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { traversalIndex = 0f }
    ) {
        Box (
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
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            listToShow.forEach { item ->
                ContentCard(item, false, true)
            }
        }
    }
}

class SearchViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SearchViewModel(SearchRepository(tmdbApi)) as T
    }
}