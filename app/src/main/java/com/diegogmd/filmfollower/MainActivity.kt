package com.diegogmd.filmfollower

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.diegogmd.filmfollower.ui.pages.MainScreen
import com.diegogmd.filmfollower.ui.pages.StartScreen
import com.diegogmd.filmfollower.ui.theme.FilmFollowerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            //statusBarStyle = SystemBarStyle.dark(MaterialTheme.colorScheme.primaryContainer.toArgb())
        )
        setContent {
            FilmFollowerTheme {
                val rootNavController = rememberNavController()
                val startDestination = remember {
                    if (SecureStorage.hasCredentials(applicationContext)) "Main" else "Start"
                }

                Box (Modifier.fillMaxSize()) {
                    NavHost(navController = rootNavController,
                        startDestination = startDestination,
                        modifier = Modifier.fillMaxSize()
                    ) {
    //                    Thinking on deleting the splash screen
    //                    composable("SplashScreen") {
    //                        SplashScreen(rootNavController)
    //                    }
                        composable("Start") {
                            StartScreen(modifier = Modifier.fillMaxSize(), navController = rootNavController)
                        }
                        composable("Main") {
                            MainScreen()
                        }
                    }

                    Box(
                        Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .windowInsetsTopHeight(WindowInsets.statusBars)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    )
                }
            }
        }
    }
}