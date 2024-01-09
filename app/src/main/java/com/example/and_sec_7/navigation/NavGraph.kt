package com.example.and_sec_7.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.and_sec_7.ui.Screens.AddSteps.AddStepsScreen
import com.example.and_sec_7.ui.Screens.StepInfo.StepInfoScreen

enum class Screens {
    StepsInfo, AddSteps, StepList
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screens.StepsInfo.name
    ) {
        composable(Screens.StepsInfo.name) {
            StepInfoScreen(navController = navController)
        }

        composable(Screens.AddSteps.name) {
            AddStepsScreen(navController = navController)
        }
    }
}