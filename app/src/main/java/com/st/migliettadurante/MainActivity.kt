package com.st.migliettadurante

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.humanactivityrecognition.screen.WelcomeScreen
import com.st.migliettadurante.audio.AudioScreen
import com.st.migliettadurante.device_detail.BleDeviceDetail
import com.st.migliettadurante.device_list.BleDeviceList
import com.st.migliettadurante.feature_detail.ActivityRecognition
import com.st.migliettadurante.feature_detail.FeatureDetail
import com.st.migliettadurante.ui.theme.StDemoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setContent {
            MainScreen()
        }
    }
}

@Composable
private fun MainScreen() {
    val navController = rememberNavController()

    StDemoTheme {
        NavHost(navController = navController, startDestination = "welcome") {

            composable(route = "welcome") {
                WelcomeScreen(
                    navController = navController
                )
            }

            composable(route = "info") {
                InfoApp(
                    navController = navController
                )
            }

            composable(route = "list") {
                BleDeviceList(
                    viewModel = hiltViewModel(),
                    navController = navController
                )
            }

            composable(
                route = "detail/{deviceId}",
                arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
            ) { backStackEntry ->
                backStackEntry.arguments?.getString("deviceId")?.let { deviceId ->
                    BleDeviceDetail(
                        viewModel = hiltViewModel(),
                        navController = navController,
                        deviceId = deviceId
                    )
                }
            }

            composable(
                route = "feature/{deviceId}/{featureName}",
                arguments = listOf(navArgument("deviceId") { type = NavType.StringType },
                    navArgument("featureName") { type = NavType.StringType })
            ) { backStackEntry ->
                backStackEntry.arguments?.getString("deviceId")?.let { deviceId ->
                    backStackEntry.arguments?.getString("featureName")?.let { featureName ->
                        FeatureDetail(
                            viewModel = hiltViewModel(),
                            navController = navController,
                            deviceId = deviceId,
                            featureName = featureName
                        )
                    }
                }
            }

            composable(
                route = "feature/{deviceId}/{featureName}/activity",
                arguments = listOf(navArgument("deviceId") { type = NavType.StringType },
                    navArgument("featureName") { type = NavType.StringType })
            ) { backStackEntry ->
                backStackEntry.arguments?.getString("deviceId")?.let { deviceId ->
                    backStackEntry.arguments?.getString("featureName")?.let { featureName ->
                        ActivityRecognition(
                            viewModel = hiltViewModel(),
                            navController = navController,
                            deviceId = deviceId,
                            featureName = featureName
                        )
                    }
                }
            }

            composable(
                route = "audio/{deviceId}",
                arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
            ) { backStackEntry ->
                backStackEntry.arguments?.getString("deviceId")?.let { deviceId ->
                    AudioScreen(
                        viewModel = hiltViewModel(),
                        navController = navController,
                        deviceId = deviceId
                    )
                }
            }
        }
    }
}