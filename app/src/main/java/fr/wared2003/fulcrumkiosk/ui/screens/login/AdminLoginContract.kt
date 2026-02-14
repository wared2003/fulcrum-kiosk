package fr.wared2003.fulcrumkiosk.ui.screens.login

/**
 * Represents the state of the AdminLoginScreen.
 *
 * @param pin The current PIN entered by the user.
 * @param isError Whether the entered PIN is incorrect.
 * @param isDefaultPinHintVisible Whether to show the hint for the default PIN.
 */
data class AdminLoginState(
    val pin: String = "",
    val isError: Boolean = false,
    val isDefaultPinHintVisible: Boolean = false,
    val defaultPin: String = ""
)

/**
 * Defines all possible user actions (events) on the AdminLoginScreen.
 */
sealed interface AdminLoginEvent {
    data class OnPinChange(val newPin: String) : AdminLoginEvent
    object OnLoginClick : AdminLoginEvent
}
