package com.diegogmd.filmfollower.ui.pages

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.diegogmd.filmfollower.R
import com.diegogmd.filmfollower.ui.AppNavHost

import androidx.compose.foundation.background
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.ui.graphics.Color
import com.diegogmd.filmfollower.ui.theme.DarkCoffee
import com.diegogmd.filmfollower.ui.theme.FadedCopper
import com.diegogmd.filmfollower.ui.theme.LightCaramel
import com.diegogmd.filmfollower.ui.theme.OliveWood


data class NavItem(
    val route: String,
    @DrawableRes val icon: Int,
    @StringRes val label: Int
)

val navItems = listOf(
    NavItem(route = "ShowsPage", icon = R.drawable.ic_shows_black_24dp, R.string.title_shows),
    NavItem(route = "FilmsPage", icon = R.drawable.ic_films_black_24dp, R.string.title_films),
    NavItem(route = "SearchPage", icon = R.drawable.ic_search_black_24dp, R.string.title_search),
    NavItem(route = "ProfilePage", icon = R.drawable.ic_profile_black_24dp, R.string.title_profile)
)

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var selectedDestination by rememberSaveable { mutableIntStateOf(1) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(
                windowInsets = NavigationBarDefaults.windowInsets,
                modifier = Modifier,
                containerColor = DarkCoffee
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedDestination == index,
                        onClick = {
                            selectedDestination = index
                            navController.navigate(route = item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = stringResource(id = item.label)
                            )
                        },
                        label = { Text(stringResource(id = item.label)) },
                        colors = NavigationBarItemColors(
                            selectedIndicatorColor = OliveWood,
                            selectedIconColor = LightCaramel,
                            selectedTextColor = LightCaramel,
                            unselectedIconColor = FadedCopper,
                            unselectedTextColor = FadedCopper,
                            disabledIconColor = FadedCopper,
                            disabledTextColor = FadedCopper
                        )
                    )
                }
            }
        }
    ) { contentPadding ->
        AppNavHost(
            navController = navController,
            startDestination = navItems[1].route,
            modifier = Modifier
                .padding(contentPadding)
        )
    }
}