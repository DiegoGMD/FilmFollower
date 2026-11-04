package com.diegogmd.filmfollower.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.diegogmd.filmfollower.data.local.remote.tmdbApi
import com.diegogmd.filmfollower.data.repository.SearchRepository
import com.diegogmd.filmfollower.model.responses.MultiSearchResult
import com.diegogmd.filmfollower.model.TvShow
import com.diegogmd.filmfollower.model.getTvShow
import com.diegogmd.filmfollower.model.getTvShowGenreNames
import com.diegogmd.filmfollower.model.syncTvShow
import com.diegogmd.filmfollower.util.isOnline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class TvShowViewModel(private val repository: SearchRepository) : ViewModel() {
    private val _tvShow = MutableStateFlow<TvShow?>(null)
    val tvShow: StateFlow<TvShow?> = _tvShow.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _genres = MutableStateFlow<List<String>>(emptyList())
    val genres: StateFlow<List<String>> = _genres.asStateFlow()

    private val _trailerKey = MutableStateFlow<String?>(null)
    val trailerKey = _trailerKey.asStateFlow()

    private val _recommendations = MutableStateFlow<List<MultiSearchResult>>(emptyList())
    val recommendations: StateFlow<List<MultiSearchResult>> = _recommendations.asStateFlow()

    fun loadTvShow(context: Context, id: Int, force: Boolean = false) {
        viewModelScope.launch {
            val appContext = context.applicationContext
            _isLoading.value = true
            try {
                _tvShow.value = if (isOnline(appContext)) {
                    syncTvShow(appContext, id, repository, force) // updates DB
                    getLocalTvShow(appContext, id) ?: repository.getTvShow(id).also { s ->
                        _genres.value = s.genres.map { it.name }
                    }
                } else {
                    _genres.value = getLocalGenres(appContext, id)
                    getLocalTvShow(appContext, id)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _tvShow.value = null // or error state
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun getLocalTvShow(context: Context, id: Int): TvShow? = withContext(Dispatchers.IO) {
        _genres.value = getTvShowGenreNames(context, id)
        getTvShow(context, id)
    }

    private suspend fun getLocalGenres(context: Context, id: Int): List<String> =
        withContext(Dispatchers.IO) { getTvShowGenreNames(context, id) }

    fun addTvShowToWatchlist(context: Context, id: Int, onResult: (Boolean) -> Unit = {}) {
        val appContext = context.applicationContext
        viewModelScope.launch {
            val ok = try {
                val tvShow = repository.getTvShow(id)
                withContext(Dispatchers.IO) { tvShow.insertNewTvShow(appContext) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("TvShowViewModel", "Error adding tv show to watchlist", e)
                false
            }
            onResult(ok)
        }
    }

    fun loadTrailer(showId: Int) {
        viewModelScope.launch {
            _trailerKey.value = null // don't show the previous tv show's trailer
            _trailerKey.value = try {
                repository.getTvShowTrailerKey(showId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                null // offline / API Error: button stays disabled
            }
        }
    }

    fun loadRecommendations(filmId: Int) {
        viewModelScope.launch {
            _recommendations.value = emptyList() // don't show the previous tv show's recommendations
            _recommendations.value = try {
                repository.getTvShowRecommendations(filmId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                emptyList() // offline / API error: carousel stays disabled
            }
        }
    }
}

class TvShowViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return TvShowViewModel(SearchRepository(tmdbApi)) as T
    }
}