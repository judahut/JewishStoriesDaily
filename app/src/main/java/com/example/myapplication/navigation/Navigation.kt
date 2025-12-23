package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.AppViewModel
import com.example.myapplication.screens.FavoritesScreen
import com.example.myapplication.screens.HomeScreen

@Composable
fun Navigation(navController: NavHostController, viewModel: AppViewModel) {
    NavHost(
        navController = navController,
        startDestination = "HomeScreen"
    ) {
        composable(route = "HomeScreen") {
            HomeScreen(
                viewModel = viewModel,
                onFavoritesClick = {
                    navController.navigate("FavoritesScreen")
                }
            )
        }

        composable(route = "FavoritesScreen") {
            FavoritesScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onStoryClick = { story ->
                    viewModel.loadSpecificStory(story)
                    navController.popBackStack()
                }
            )
        }
    }
}