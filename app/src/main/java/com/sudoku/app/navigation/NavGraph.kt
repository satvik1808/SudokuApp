package com.sudoku.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sudoku.app.ui.screens.GameScreen
import com.sudoku.app.ui.screens.HomeScreen
import com.sudoku.app.ui.screens.StatsScreen
import com.sudoku.app.ui.screens.ThemeBuilderScreen

@Composable
fun NavGraph() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onStartGame = { difficulty, timed -> nav.navigate("game/$difficulty/$timed") },
                onShowStats = { nav.navigate("stats") },
                onEditCustomTheme = { nav.navigate("theme_builder") }
            )
        }
        composable(
            route = "game/{difficulty}/{timed}",
            arguments = listOf(
                navArgument("difficulty") { type = NavType.StringType },
                navArgument("timed") { type = NavType.StringType }
            )
        ) { back ->
            val diff = back.arguments?.getString("difficulty") ?: "EASY"
            val timed = back.arguments?.getString("timed") == "true"
            GameScreen(difficulty = diff, timedMode = timed, onBack = { nav.popBackStack() })
        }
        composable("stats") {
            StatsScreen(onBack = { nav.popBackStack() })
        }
        composable("theme_builder") {
            ThemeBuilderScreen(onBack = { nav.popBackStack() })
        }
    }
}
