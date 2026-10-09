package com.diegogmd.filmfollower.ui.theme

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

object ThemeManager {
    const val OLD_FILM = 0
    const val DARKNESS = 1

    private const val PREFS_FILE = "filmfollower_settings"
    private const val KEY_THEME_ID = "theme_id"

    var themeId by mutableIntStateOf(OLD_FILM)
        private set

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)

    /** Call once at app start, before any UI is drawn. */
    fun init(context: Context) {
        themeId = prefs(context).getInt(KEY_THEME_ID, OLD_FILM)
    }

    fun setTheme(context: Context, id: Int) {
        themeId = id
        prefs(context).edit().putInt(KEY_THEME_ID, id).apply()
    }
}