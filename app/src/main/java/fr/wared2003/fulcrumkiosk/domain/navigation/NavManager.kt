package fr.wared2003.fulcrumkiosk.domain.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class NavManager {
    // On utilise un Flow d'actions plus complexe pour gérer différents types de navigation
    private val _navEvents = MutableSharedFlow<NavAction>(extraBufferCapacity = 1)
    val navEvents = _navEvents.asSharedFlow()

    fun navigate(screen: Screen, popUpTo: Screen? = null, inclusive: Boolean = false) {
        _navEvents.tryEmit(NavAction.Navigate(screen, popUpTo, inclusive))
    }

    fun popBackStack() {
        _navEvents.tryEmit(NavAction.PopBackStack)
    }
}

sealed class NavAction {
    data class Navigate(val screen: Screen, val popUpTo: Screen? = null, val inclusive: Boolean = false) : NavAction()
    object PopBackStack : NavAction()
}

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Settings : Screen("settings")
    object Kiosk : Screen("kiosk")
}