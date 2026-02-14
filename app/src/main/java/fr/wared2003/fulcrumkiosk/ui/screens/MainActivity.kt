package fr.wared2003.fulcrumkiosk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.wared2003.fulcrumkiosk.data.ConfigManager
import fr.wared2003.fulcrumkiosk.data.VaultManager
import fr.wared2003.fulcrumkiosk.ui.screens.AdminLoginScreen
import fr.wared2003.fulcrumkiosk.ui.screens.WelcomeScreen
import fr.wared2003.fulcrumkiosk.ui.screens.settings.SettingsScreen
import fr.wared2003.fulcrumkiosk.ui.theme.FulcrumKioskTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Data Managers
        val vaultManager = VaultManager(this)
        val configManager = ConfigManager(this)

        enableEdgeToEdge()

        setContent {
            FulcrumKioskTheme {
                val windowSizeClass = calculateWindowSizeClass(this)
                val navController = rememberNavController()

                // Route Logic:
                // If everything is set, go to Kiosk (later).
                // Otherwise, start at Welcome.
                val startDestination = if (vaultManager.hasTailscaleKey() && configManager.hasUrl()) {
                    "kiosk"
                } else {
                    "welcome"
                }

                NavHost(navController = navController, startDestination = startDestination) {

                    // Step 1: Landing Page
                    composable("welcome") {
                        WelcomeScreen(onConfigureClick = {
                            navController.navigate("login")
                        })
                    }

                    // Step 2: Security Gate (PIN 1234)
                    composable("login") {
                        AdminLoginScreen(
                            vaultManager = vaultManager,
                            onAccessGranted = {
                                navController.navigate("settings") // Navigate to settings after login
                            }
                        )
                    }

                    // Step 3: Final Configuration (URL & Tailscale Key)
                    composable("settings") {
                        SettingsScreen(
                            configManager = configManager,
                            windowSizeClass = windowSizeClass,
                            onExitSettings = {
                                navController.navigate("welcome")
                            }
                        )
                    }
                }
            }
        }
    }
}
