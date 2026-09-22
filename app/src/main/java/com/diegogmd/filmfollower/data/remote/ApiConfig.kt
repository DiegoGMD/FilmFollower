package com.diegogmd.filmfollower.data.local.remote

import android.content.Context
import com.diegogmd.filmfollower.SecureStorage

object ApiConfig {

    /**
     * Reads the TMDB access token the user saved via SecureStorage
     * (EncryptedSharedPreferences). Returns null if nothing has been
     * saved yet, e.g. before the user enters a key in Settings.
     */
    fun getTmdbToken(context: Context): String? =
        SecureStorage.getApiKey(context)
}