package fr.wared2003.fulcrumkiosk.ui.screens.settings

import SettingsItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PowerSavingScreen(state: SettingsState, onEvent: (SettingsEvent) -> Unit) {
    val actions = listOf("dim", "off")
    var expanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.padding(PaddingValues(24.dp)),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("Power Saving Delay", fontWeight = FontWeight.SemiBold) },
                        supportingContent = { Text("${state.powerSavingDelayMinutes} minutes") },
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Bedtime, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    )
                    Slider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        value = state.powerSavingDelayMinutes.toFloat(),
                        onValueChange = { onEvent(SettingsEvent.OnPowerSavingDelayChanged(it.toInt())) },
                        valueRange = 1f..60f
                    )
                }
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    ListItem(
                        headlineContent = { Text("Power Saving Action", fontWeight = FontWeight.SemiBold) },
                        supportingContent = { Text("Action to perform after delay") },
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.BatterySaver, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    )
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextField(
                                readOnly = true,
                                value = state.powerSavingAction,
                                onValueChange = {},
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                            ) {
                                actions.forEach { action ->
                                    DropdownMenuItem(
                                        text = { Text(action) },
                                        onClick = {
                                            onEvent(SettingsEvent.OnPowerSavingActionChanged(action))
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        if (state.powerSavingAction == "dim") {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        val dimValue = state.powerSavingDimValue.coerceIn(0.1f, 0.2f)
                        ListItem(
                            headlineContent = { Text("Dim Value", fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text("Brightness when dimmed: ${(dimValue * 100).toInt()}% ") },
                            leadingContent = {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.BrightnessLow, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                        )
                        Slider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            value = dimValue,
                            onValueChange = { onEvent(SettingsEvent.OnPowerSavingDimValueChanged(it)) },
                            valueRange = 0.1f..0.2f,
                            steps = 0
                        )
                    }
                }
            }
        }
    }
}