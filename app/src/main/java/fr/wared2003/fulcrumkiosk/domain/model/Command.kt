package fr.wared2003.fulcrumkiosk.domain.model

import fr.wared2003.fulcrumkiosk.domain.navigation.Screen

/**
 * Represents a command that can be sent to the kiosk remotely.
 * This sealed class ensures type safety for all supported commands.
 */
sealed class Command {
    /**
     * Triggers a reboot of the Android device.
     */
    object Reboot : Command()

    /**
     * Sets the device's volume to a specific level.
     *
     * @property level The target volume level, typically as an integer (e.g., 0-100).
     */
    data class SetVolume(val level: Int) : Command()

    /**
     * Navigates the Kiosk's web view to a new URL or internal route.
     *
     * @property screen The destination URL or route identifier.
     */
    data class NavigateTo(val screen: Screen) : Command()

    fun fromString(rawCommand: String): Result<Command> {
        return runCatching {
            when {
                rawCommand == "reboot" -> Reboot

                rawCommand.startsWith("set_volume:") -> {
                    val level = rawCommand.substringAfter(":").toInt()
                    if (level !in 0..100) throw IllegalArgumentException("Volume out of range: $level")
                    SetVolume(level)
                }

                rawCommand.startsWith("navigate_to:") -> {
                    val routeName = rawCommand.substringAfter(":")
                    val screen = when (routeName) {
                        "welcome" -> Screen.Welcome
                        "settings" -> Screen.Settings
                        else -> throw NoSuchElementException("Unknown route: $routeName")
                    }
                    NavigateTo(screen)
                }

                else -> throw IllegalArgumentException("Unknown command format: $rawCommand")
            }
        }
    }
}
