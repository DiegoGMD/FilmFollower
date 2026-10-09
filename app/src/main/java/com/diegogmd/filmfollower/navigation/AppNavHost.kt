package com.diegogmd.filmfollower.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.diegogmd.filmfollower.ui.pages.ContentScreenFilm
import com.diegogmd.filmfollower.ui.pages.ContentScreenTvShow
import com.diegogmd.filmfollower.ui.pages.FilmsScreen
import com.diegogmd.filmfollower.ui.pages.ProfileScreen
import com.diegogmd.filmfollower.ui.pages.SearchScreen
import com.diegogmd.filmfollower.ui.pages.SettingsScreen
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
            ProfileScreen(
                modifier = Modifier.fillMaxSize(),
                navController = navController)
        }
        composable("SettingsScreen") {
            SettingsScreen(
                modifier = Modifier.fillMaxSize(),
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "film/{filmId}",
            arguments = listOf(navArgument("filmId") { type = NavType.IntType })
        ) { backStackEntry ->
            val filmId = backStackEntry.arguments?.getInt("filmId") ?: return@composable
            ContentScreenFilm(
                filmId = filmId,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "show/{showId}",
            arguments = listOf(navArgument("showId") { type = NavType.IntType })
        ) { backStackEntry ->
            val showId = backStackEntry.arguments?.getInt("showId") ?: return@composable
            ContentScreenTvShow(
                showId = showId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}