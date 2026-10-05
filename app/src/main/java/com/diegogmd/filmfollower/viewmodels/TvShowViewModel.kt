package com.diegogmd.filmfollower.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.diegogmd.filmfollower.data.local.remote.tmdbApi
import com.diegogmd.filmfollower.data.repository.SearchRepository
import com.diegogmd.filmfollower.model.TvShow
import com.diegogmd.filmfollower.model.getTvShow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TvShowViewModel(private val repository: SearchRepository) : ViewModel() {
    private val _tvShow = MutableStateFlow<TvShow?>(null)
    val tvShow: StateFlow<TvShow?> = _tvShow.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadFilm(context: Context, id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _tvShow.value = withContext(Dispatchers.IO) {
                    getTvShow(context, id) // local DB lookup (returns Film?)
                } ?: repository.getTvShow(id) // fallback: TMDB
            } catch (e: Exception) {
                _tvShow.value = null // or error state
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addTvShowToWishlist(context: Context, id: Int) {
        viewModelScope.launch {
            val film = repository.getFilm(id)
            film.insertNewFilm(context)
        }
    }
}

class TvShowViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return TvShowViewModel(SearchRepository(tmdbApi)) as T
    }
}