package com.diegogmd.filmfollower.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegogmd.filmfollower.model.MultiSearchResult
import com.diegogmd.filmfollower.data.repository.SearchRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: SearchRepository) : ViewModel() {
    private val _results = MutableStateFlow<List<MultiSearchResult>>(emptyList())
    val results: StateFlow<List<MultiSearchResult>> = _results

    private val _trending = MutableStateFlow<List<MultiSearchResult>>(emptyList())
    val trending: StateFlow<List<MultiSearchResult>> = _trending

    private var searchJob: Job? = null

    init {
        loadTrending()
    }

    private fun loadTrending() {
        viewModelScope.launch {
            _trending.value = repository.getTopTrending(limit = 10)
        }
    }

    fun onQueryChanged(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400) // debounce so you're not hitting the API on every keystroke
            _results.value = repository.search(query)
        }
    }
}