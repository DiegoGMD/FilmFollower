package com.diegogmd.filmfollower

import android.app.Application
import android.content.Context
import com.diegogmd.filmfollower.ui.theme.ThemeManager
import com.jakewharton.threetenabp.AndroidThreeTen

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this) // Change this to java.time.* lib
        ThemeManager.init(this)
        appContext = applicationContext
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}