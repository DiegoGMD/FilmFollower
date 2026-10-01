package com.diegogmd.filmfollower.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.diegogmd.filmfollower.model.Film
import com.diegogmd.filmfollower.model.getFilm
import com.diegogmd.filmfollower.ui.pages.ContentScreenFilm
import com.diegogmd.filmfollower.ui.pages.FilmsScreen
import com.diegogmd.filmfollower.ui.pages.SearchScreen
import com.diegogmd.filmfollower.ui.pages.ShowsScreen
import com.diegogmd.filmfollower.ui.pages.StartScreen
import org.threeten.bp.LocalDate

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
//        composable("ProfileScreen") {
//            Box(Modifier) {
//                Text("Coming soon")
//            }
//        }
        composable("ProfileScreen") { // Easier to test and revise in own phone while not coding
            val film = Film(120, "El Señor de los Anillos: La Comunidad del Anillo",
                "The Lord of the Rings: The Fellowship of the Ring",
                "Young hobbit Frodo Baggins, after inheriting a mysterious ring from his uncle " +
                        "Bilbo, must leave his home in order to keep it from falling into the hands of " +
                        "its evil creator. Along the way, a fellowship is formed to protect the ringbearer " +
                        "and make sure that the ring arrives at its final destination: Mt. Doom, the only " +
                        "place where it can be destroyed.",
                LocalDate.of(2001, 12, 10), 208,
                "/9xtH1RmAzQ0rrMBNUMXstb2s3er.jpg","Released",
                LocalDate.of(2026, 7, 31), 8.4, "Watchlist",
                LocalDate.of(2025, 4, 14), 2,
                LocalDate.of(2026, 7, 31)
            )

            ContentScreenFilm(film = film)
        }
//        composable(
//            route = "film/{filmId}",
//            arguments = listOf(navArgument("filmId") { type = NavType.IntType })
//        ) { backStackEntry ->
//            val filmId = backStackEntry.arguments?.getInt("filmId") ?: return@composable
//            ContentScreenFilm(modifier = Modifier, filmId)
//        }
    }
}