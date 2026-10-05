package com.diegogmd.filmfollower.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.diegogmd.filmfollower.data.local.remote.tmdbApi
import com.diegogmd.filmfollower.model.MultiSearchResult
import com.diegogmd.filmfollower.data.repository.SearchRepository
import com.diegogmd.filmfollower.model.Film
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class SearchViewModel(private val repository: SearchRepository) : ViewModel() {
    private val _results = MutableStateFlow<List<MultiSearchResult>>(emptyList())
    val results: StateFlow<List<MultiSearchResult>> = _results

    private val _trending = MutableStateFlow<List<MultiSearchResult>>(emptyList())
    val trending: StateFlow<List<MultiSearchResult>> = _trending

    private var searchJob: Job? = null

    init {
        loadTrending()
    }

    fun loadTrending() {
        viewModelScope.launch {
            try {
                _trending.value = repository.getTopTrending(limit = 10)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.d("SearchViewModel","offline or request failed: keep the current list")
            }
        }
    }

    fun onQueryChanged(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400) // debounce so you're not hitting the API on every keystroke
            try {
                _results.value = repository.search(query)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _results.value = emptyList()
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