package fr.wared2003.fulcrumkiosk.ui.screens.settings

import SettingsItem
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun SecurityScreen(state: SettingsState, onEvent: (SettingsEvent) -> Unit) {

    AdminPinDialog(state = state, onEvent = onEvent)
    KioskPinDialog(state = state, onEvent = onEvent)


    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SettingsItem(
                icon = Icons.Default.AdminPanelSettings,
                title = "Administrator PIN",
                subtitle = if (state.isDefaultAdminPin) "Status: Default (Unsecured)" else "Status: Secured",
                titleColor = if (state.isDefaultAdminPin) MaterialTheme.colorScheme.error else Color.Unspecified,
                onClick = { onEvent(SettingsEvent.OnAdminPinClicked) }
            )
        }

        item {
            SettingsItem(
                icon = if (state.isLockOn) Icons.Default.Lock else Icons.Default.LockOpen,
                title = "Kiosk Mode Lock",
                subtitle = if (state.isLockOn) "Status: On (Secured)" else "Status: Off (unsecured)",
                titleColor = if (!state.isLockOn) MaterialTheme.colorScheme.error else Color.Unspecified,
                onClick = { onEvent(SettingsEvent.OnClickLockMode) }
            )
        }

//        item {
//            SettingsItem(
//                icon = Icons.AutoMirrored.Default.ExitToApp,
//                title = "Kiosk open PIN",
//                subtitle = if (state.isKioskPinSet) "Pin set" else "No pin set",
//                onClick = { onEvent(SettingsEvent.OnKioskPinClicked) }
//            )
//        }
    }
}

@Composable
fun AdminPinDialog(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit
) {
    if (!state.showAdminPinDialog) return

    AlertDialog(
        onDismissRequest = { onEvent(SettingsEvent.OnDismissAdminPinDialog) },
        title = { Text("Update Admin PIN") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "This PIN is used to access these settings.",
                    style = MaterialTheme.typography.bodySmall
                )
                OutlinedTextField(
                    value = state.newAdminPin,
                    onValueChange = { onEvent(SettingsEvent.OnAdminPinChange(it)) },
                    label = { Text("New PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("4 digits recommended") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.adminPinErrorMessage != null
                )

                // Show error message if validation failed in UseCase
                state.adminPinErrorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onEvent(SettingsEvent.OnSaveAdminPinClicked) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = { onEvent(SettingsEvent.OnDismissAdminPinDialog) }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun KioskPinDialog(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit
) {
    // Only render the dialog when the state demands it
    if (!state.showKioskPinDialog) return

    AlertDialog(
        onDismissRequest = { onEvent(SettingsEvent.OnDismissKioskPinDialog) },
        title = { Text("Update Kiosk PIN") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "This PIN is used to access the Kiosk.",
                    style = MaterialTheme.typography.bodySmall
                )
                OutlinedTextField(
                    value = state.newKioskPin,
                    onValueChange = { onEvent(SettingsEvent.OnKioskPinChange(it)) },
                    label = { Text("New PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("4 digits recommended") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.kioskPinErrorMessage != null
                )

                state.kioskPinErrorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },

        confirmButton = {
            Button(onClick = { onEvent(SettingsEvent.OnSaveKioskPinClicked) }) {
                Text("Save")
            }
        },
        dismissButton = {
            // Horizontal row for Dismiss and Clear actions

                // Button to remove the PIN protection
                if (state.isKioskPinSet) {
                    TextButton(
                        onClick = { onEvent(SettingsEvent.OnClearKioskPinClicked) },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Remove PIN")
                    }
                }

                TextButton(onClick = { onEvent(SettingsEvent.OnDismissKioskPinDialog) }) {
                    Text("Cancel")
                }
        }
    )
}