package com.diegogmd.filmfollower;

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecureStorage {
    private const val PREFS_FILE = "filmfollower_secure"
    private const val KEY_USERNAME = "username"
    private const val KEY_API_KEY = "tmdb_api_key"

    private fun getPrefs(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

        return EncryptedSharedPreferences.create(
                context,
                PREFS_FILE,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveCredentials(context: Context, username: String, apiKey: String) {
        getPrefs(context).edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_API_KEY, apiKey)
                .apply()
    }

    fun hasCredentials(context: Context): Boolean =
        !getApiKey(context).isNullOrBlank()

    fun clearCredentials(context: Context) {
        getPrefs(context).edit().clear().apply()
    }

    fun getUsername(context: Context): String? =
    getPrefs(context).getString(KEY_USERNAME, null)

    fun getApiKey(context: Context): String? =
    getPrefs(context).getString(KEY_API_KEY, null)
}
