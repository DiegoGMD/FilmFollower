package com.diegogmd.filmfollower;

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecureStorage {
    private const val PREFS_FILE = "filmfollower_secure"
    private const val KEY_USERNAME = "username"
    private const val KEY_API_READ_ACCESS_TOKEN = "tmdb_api_read_access_token"

    private fun getPrefs(context: Context): SharedPreferences {
        return try {
            createEncryptedPrefs(context)
        } catch (e: Exception) {
            // Corrupt/undecryptable keyset (e.g. restored from backup with a
            // stale Keystore key). Wipe and recreate rather than crash.
            context.deleteSharedPreferences(PREFS_FILE)
            createEncryptedPrefs(context)
        }
    }

    private fun createEncryptedPrefs(context: Context): SharedPreferences {
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

    fun saveCredentials(context: Context, username: String, apiReadAccessToken: String) {
        getPrefs(context).edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_API_READ_ACCESS_TOKEN, apiReadAccessToken)
                .apply()
    }

    fun hasCredentials(context: Context): Boolean =
        !getApiReadAccessToken(context).isNullOrBlank()

    fun clearCredentials(context: Context) {
        getPrefs(context).edit().clear().apply()
    }

    fun getUsername(context: Context): String? =
    getPrefs(context).getString(KEY_USERNAME, null)

    fun getApiReadAccessToken(context: Context): String? =
    getPrefs(context).getString(KEY_API_READ_ACCESS_TOKEN, null)

    fun changeUsername(context: Context, newUsername: String) {
        val currentToken = getApiReadAccessToken(context) ?: ""
        saveCredentials(context, newUsername.trim(), currentToken)
    }

    fun changeApiReadAccessToken(context: Context, newToken: String) {
        val currentUsername = getUsername(context) ?: ""
        saveCredentials(context, currentUsername, newToken.trim())
    }
}
