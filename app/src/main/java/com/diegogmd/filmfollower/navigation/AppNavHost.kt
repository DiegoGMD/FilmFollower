package com.diegogmd.filmfollower.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.diegogmd.filmfollower.ui.pages.ContentPageFilm
import com.diegogmd.filmfollower.ui.pages.FilmsScreen
import com.diegogmd.filmfollower.ui.pages.SearchScreen
import com.diegogmd.filmfollower.ui.pages.ShowsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier.fillMaxSize()
    ) {
        composable("ShowsScreen") {
            ShowsScreen(Modifier.fillMaxSize(), navController)
        }
        composable("FilmsScreen") {
            FilmsScreen(Modifier.fillMaxSize(), navController)
        }
        composable("SearchScreen") {
            SearchScreen(
                modifier = Modifier.fillMaxSize(),
                navController = navController)
        }
        composable("ProfileScreen") {
            Box(Modifier) {
                Text("Coming soon")
            }
        }
//        composable(
//            route = "film/{filmId}",
//            arguments = listOf(navArgument("filmId") { type = NavType.IntType })
//        ) { backStackEntry ->
//            val filmId = backStackEntry.arguments?.getInt("filmId") ?: return@composable
//            ContentPageFilm(modifier = Modifier, filmId)
//        }
    }
}