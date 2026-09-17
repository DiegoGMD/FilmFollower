package com.diegogmd.filmfollower.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.diegogmd.filmfollower.data.local.remote.tmdbApi
import com.diegogmd.filmfollower.data.repository.SearchRepository
import com.diegogmd.filmfollower.model.Film
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FilmViewModel(private val repository: SearchRepository) : ViewModel() {
    private val _film = MutableStateFlow<Film?>(null)
    val film: StateFlow<Film?> = _film.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadFilm(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _film.value = repository.getFilm(id)
            _isLoading.value = false
        }
    }

    fun addFilmToWishlist(context: Context, id: Int) {
        viewModelScope.launch {
            val film = repository.getFilm(id)
            film.insertNewFilm(context)
        }
    }
}

class FilmViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FilmViewModel(SearchRepository(tmdbApi)) as T
    }
}