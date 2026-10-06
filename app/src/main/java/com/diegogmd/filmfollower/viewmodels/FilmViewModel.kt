package com.diegogmd.filmfollower.viewmodels

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.diegogmd.filmfollower.data.local.remote.tmdbApi
import com.diegogmd.filmfollower.data.repository.SearchRepository
import com.diegogmd.filmfollower.model.Film
import com.diegogmd.filmfollower.model.Genre
import com.diegogmd.filmfollower.model.getFilm
import com.diegogmd.filmfollower.model.getFilmGenreNames
import com.diegogmd.filmfollower.util.isOnline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FilmViewModel(private val repository: SearchRepository) : ViewModel() {
    private val _film = MutableStateFlow<Film?>(null)
    val film: StateFlow<Film?> = _film.asStateFlow()
    private val _genres = MutableStateFlow<List<String>>(emptyList())
    val genres: StateFlow<List<String>> = _genres.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadFilm(context: Context, id: Int) {
        val appContext = context.applicationContext
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _film.value = if (isOnline(appContext)) {
                    try { // online: TMDB API
                        repository.getFilm(id).also { f ->
                            _genres.value = f.genres.map { it.name }
                        }
                    } catch (e: Exception){ // network failed: use DB
                        _genres.value = getLocalGenres(appContext, id)
                        getLocalFilm(appContext, id)
                    }
                } else { // offline: use DB
                    _genres.value = getLocalGenres(appContext, id)
                    getLocalFilm(appContext, id)
                }
            } catch (e: Exception) {
                _film.value = null // or error state
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun getLocalFilm(context: Context, id: Int): Film? =
        withContext(Dispatchers.IO) {
            _genres.value = getFilmGenreNames(context, id)
            getFilm(context, id)
        }

    private suspend fun getLocalGenres(context: Context, id: Int): List<String> =
        withContext(Dispatchers.IO) { getFilmGenreNames(context, id) }

    fun addFilmToWishlist(context: Context, id: Int) {
        viewModelScope.launch {
            val film = repository.getFilm(id)
            withContext(Dispatchers.IO) { film.insertNewFilm(context.applicationContext) }
        }
    }
}

class FilmViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FilmViewModel(SearchRepository(tmdbApi)) as T
    }
}