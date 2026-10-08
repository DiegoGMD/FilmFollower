package com.diegogmd.filmfollower.ui.pages

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.navigation.compose.currentBackStackEntryAsState

data class NavItem(
    val route: String,
    @DrawableRes val icon: Int,
    @StringRes val label: Int
)

val navItems = listOf(
    NavItem(route = "ShowsScreen", icon = R.drawable.ic_shows_24dp, R.string.title_shows),
    NavItem(route = "FilmsScreen", icon = R.drawable.ic_films_24dp, R.string.title_films),
    NavItem(route = "SearchScreen", icon = R.drawable.ic_search_24dp, R.string.title_search),
    NavItem(route = "ProfileScreen", icon = R.drawable.ic_profile_24dp, R.string.title_profile)
)

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in navItems.map { it.route }
    var selectedDestination by rememberSaveable { mutableIntStateOf(1) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    windowInsets = NavigationBarDefaults.windowInsets,
                    modifier = Modifier,
                    //// containerColor = DarkCoffee
                ) {
                    navItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
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
                                    contentDescription = stringResource(id = item.label),
                                )
                            },
                            label = { Text(stringResource(id = item.label)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.secondary,
                                unselectedTextColor = MaterialTheme.colorScheme.secondary,
                            )
                        )
                    }
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