package com.govtjobalert

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.govtjobalert.ui.screens.*
import com.govtjobalert.ui.theme.GovtJobAlertTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GovtJobAlertTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") { SplashScreen(navController) }
                        composable("home") { HomeScreen(navController) }
                        composable("jobDetails/{jobId}") { backStackEntry ->
                            val jobId = backStackEntry.arguments?.getString("jobId")
                            JobDetailsScreen(navController, jobId)
                        }
                        composable("adminLogin") { AdminLoginScreen(navController) }
                        composable("adminDashboard") { AdminDashboardScreen(navController) }
                        composable("addJob") { AddJobScreen(navController) }
                        composable("editJob/{jobId}") { backStackEntry ->
                            val jobId = backStackEntry.arguments?.getString("jobId")
                            AddJobScreen(navController, jobId) // reuse screen for edit
                        }
                    }
                }
            }
        }
    }
}
