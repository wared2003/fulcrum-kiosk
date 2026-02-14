package fr.wared2003.fulcrumkiosk.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import fr.wared2003.fulcrumkiosk.data.VaultManager

@Composable
fun AdminLoginScreen(
    vaultManager: VaultManager,
    onAccessGranted: () -> Unit
) {
    var pinInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val correctPin = vaultManager.getAdminPin()
    val isDefault = vaultManager.isDefaultPin()

    // Surface uses the background color defined in your Theme.kt
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()), // Responsive for smaller screens or landscape
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // --- Visual Icon ---
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Security Icon", // Important for accessibility
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Header Section ---
            Text(
                text = "Admin Access",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Text(
                text = "Please enter your security PIN to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // --- Form Container (Responsive width) ---
            Column(
                modifier = Modifier.widthIn(max = 400.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = {
                        // Allow only digits and limit length if needed
                        if (it.all { char -> char.isDigit() }) {
                            pinInput = it
                        }
                        isError = false
                    },
                    label = { Text("PIN Code") },
                    isError = isError,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp), // Modern M3 rounded corners
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                // --- Error / Hint Messages ---
                if (isError) {
                    Text(
                        text = "Invalid PIN code. Please try again.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                    )
                } else if (isDefault) {
                    Text(
                        text = "Hint: Default PIN is $correctPin",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // --- Action Button (M3 Filled Button) ---
                Button(
                    onClick = {
                        if (pinInput == correctPin) {
                            onAccessGranted()
                        } else {
                            isError = true
                        }
                    },
                    // The button is enabled only when the user has entered at least 4 digits.
                    enabled = pinInput.length >= 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Unlock Settings",
                        // Using a standard M3 typography style for buttons
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
