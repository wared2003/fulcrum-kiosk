package fr.wared2003.fulcrumkiosk

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.wared2003.fulcrumkiosk.domain.navigation.NavAction
import fr.wared2003.fulcrumkiosk.domain.navigation.NavManager
import fr.wared2003.fulcrumkiosk.domain.navigation.Screen
import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository
import fr.wared2003.fulcrumkiosk.ui.screens.kiosk.KioskScreen
import fr.wared2003.fulcrumkiosk.ui.screens.login.AdminLoginScreen
import fr.wared2003.fulcrumkiosk.ui.screens.settings.SettingsScreen
import fr.wared2003.fulcrumkiosk.ui.screens.welcome.WelcomeScreen
import fr.wared2003.fulcrumkiosk.ui.theme.FulcrumKioskTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val navManager: NavManager by inject()
    private val settingsRepository: SettingsRepository by inject()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FulcrumKioskTheme {
                val navController = rememberNavController()
                val windowSizeClass = calculateWindowSizeClass(this)

                // Gestion de la navigation via NavManager
                LaunchedEffect(navController) {
                    navManager.navEvents.collect { action ->
                        when (action) {
                            is NavAction.Navigate -> {
                                navController.navigate(action.screen.route) {
                                    action.popUpTo?.let { screen: Screen ->
                                        popUpTo(screen.route) { inclusive = action.inclusive }
                                    }
                                }
                            }
                            is NavAction.PopBackStack -> navController.popBackStack()
                        }
                    }
                }

                val kioskConfig by settingsRepository.kioskConfig.collectAsState(initial = null)

                if (kioskConfig == null) {
                    LoadingPlaceholder()
                } else {
                    // Si l'URL est vide, on va sur Welcome, sinon sur Kiosk
                    val startRoute = if (kioskConfig?.url.isNullOrBlank()) Screen.Welcome.route else Screen.Kiosk.route

                    NavHost(
                        navController = navController,
                        startDestination = startRoute
                    ) {
                        composable(Screen.Welcome.route) { WelcomeScreen() }
                        composable(Screen.Login.route) { AdminLoginScreen() }
                        composable(Screen.Settings.route) { SettingsScreen(windowSizeClass = windowSizeClass) }
                        composable(Screen.Kiosk.route) { KioskScreen() }
                    }
                }
            }
        }
    }

    /**
     * Active ou désactive le mode LockTask (Kiosque "Incassable").
     * Nécessite que l'app soit Device Owner via ADB.
     */
    fun setKioskMode(enable: Boolean) {
        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminName = ComponentName(this, DeviceAdminReceiver::class.java)

        try {
            if (dpm.isDeviceOwnerApp(packageName)) {
                if (enable) {
                    dpm.setLockTaskPackages(adminName, arrayOf(packageName))
                    startLockTask()
                    Log.d("Kiosk", "Mode LockTask (Device Owner) activé")
                } else {
                    stopLockTask()
                    Log.d("Kiosk", "Mode LockTask désactivé")
                }
            } else {
                // Mode dégradé (Pinning) si pas Device Owner
                if (enable) {
                    startLockTask()
                    Log.w("Kiosk", "L'app n'est pas Device Owner. Utilisation du Screen Pinning classique.")
                } else {
                    stopLockTask()
                }
            }
        } catch (e: Exception) {
            Log.e("Kiosk", "Erreur lors du changement de mode Kiosque : ${e.message}")
        }
    }
    fun unlockForAdmin() {
        stopLockTask()
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.show(WindowInsetsCompat.Type.systemBars())
    }
}

@Composable
fun LoadingPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}