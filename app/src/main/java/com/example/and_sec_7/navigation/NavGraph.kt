package com.example.and_sec_7.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.and_sec_7.ui.Screens.StepInfoScreen

enum class Screens {
    StepsInfo
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
    }
}