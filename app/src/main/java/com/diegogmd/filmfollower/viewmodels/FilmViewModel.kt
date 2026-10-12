package com.diegogmd.filmfollower.viewmodels

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.diegogmd.filmfollower.data.local.remote.tmdbApi
import com.diegogmd.filmfollower.data.repository.SearchRepository
import com.diegogmd.filmfollower.model.Film
import com.diegogmd.filmfollower.model.Genre
import com.diegogmd.filmfollower.model.getFilm
import com.diegogmd.filmfollower.model.getFilmGenreNames
import com.diegogmd.filmfollower.model.syncFilm
import com.diegogmd.filmfollower.util.isOnline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class FilmViewModel(private val repository: SearchRepository) : ViewModel() {
    private val _film = MutableStateFlow<Film?>(null)
    val film: StateFlow<Film?> = _film.asStateFlow()

    private val _genres = MutableStateFlow<List<String>>(emptyList())
    val genres: StateFlow<List<String>> = _genres.asStateFlow()

    private val _trailerKey = MutableStateFlow<String?>(null)
    val trailerKey = _trailerKey.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadFilm(context: Context, id: Int) {
        val appContext = context.applicationContext
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _film.value = if (isOnline(appContext)) {
                    syncFilm(appContext, id, repository) // updates DB
                    getLocalFilm(appContext, id)
                        ?: repository.getFilm(id).also { f -> // not stored: show TMDB data
                            _genres.value = f.genres.map { it.name }
                        }
                } else {
                    _genres.value = getLocalGenres(appContext, id)
                    getLocalFilm(appContext, id)
                }
            } catch (e: CancellationException) {
                throw e
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

    fun addFilmToWatchlist(context: Context, id: Int, onResult: (Boolean) -> Unit = {}) {
        val appContext = context.applicationContext
        viewModelScope.launch {
            val ok = try {
                val film = repository.getFilm(id)
                withContext(Dispatchers.IO) { film.insertNewFilm(appContext) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("FilmViewModel", "Error adding film to watchlist", e)
                false
            }
            onResult(ok)
        }
    }

    fun loadTrailer(filmId: Int) {
        viewModelScope.launch {
            _trailerKey.value = null // don't show the previous film's trailer
            _trailerKey.value = try {
                repository.getFilmTrailerKey(filmId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                null // offline / API error: button stays disabled
            }
        }
    }
}

class FilmViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FilmViewModel(SearchRepository(tmdbApi)) as T
    }
}