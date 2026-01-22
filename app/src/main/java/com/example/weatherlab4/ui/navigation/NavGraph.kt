package com.example.weatherlab4.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherlab4.ui.screens.home.HomeScreen
import com.example.weatherlab4.ui.screens.search.SearchScreen
import com.example.weatherlab4.ui.screens.favorites.FavoritesScreen
import com.example.weatherlab4.ui.screens.settings.SettingsScreen

object Routes {
    const val HOME = "home"
    const val SEARCH = "search"
    const val FAVORITES = "favorites"
    const val SETTINGS = "settings"
}

@Composable
fun WeatherAppRoot() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.HOME) {
        composable(Routes.HOME) { HomeScreen(nav) }
        composable(Routes.SEARCH) { SearchScreen(nav) }
        composable(Routes.FAVORITES) { FavoritesScreen(nav) }
        composable(Routes.SETTINGS) { SettingsScreen(nav) }
    }
}