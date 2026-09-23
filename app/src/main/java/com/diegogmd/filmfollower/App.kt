package com.diegogmd.filmfollower

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        appContext = applicationContext
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}