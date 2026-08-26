package com.diegogmd.filmfollower

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.diegogmd.filmfollower.ui.pages.MainScreen
import com.diegogmd.filmfollower.ui.compònents.SplashScreen
import com.diegogmd.filmfollower.ui.theme.DarkCoffee
import com.diegogmd.filmfollower.ui.theme.FilmFollowerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(DarkCoffee.toArgb())
        )
        setContent {
            FilmFollowerTheme {
                val rootNavController = rememberNavController()
                NavHost(navController = rootNavController,
                    startDestination = "SplashScreen",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("SplashScreen") {
                        SplashScreen(rootNavController)
                    }
                    composable("Main") {
                        MainScreen()
                    }
                }
            }
        }
    }
}