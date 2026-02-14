package fr.wared2003.fulcrumkiosk.ui.screens.settings

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.wared2003.fulcrumkiosk.data.ConfigManager
import kotlinx.coroutines.launch

private data class SettingsMenuItem(val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    configManager: ConfigManager,
    windowSizeClass: WindowSizeClass,
    onExitSettings: () -> Unit
) {
    val settingsItems = listOf(
        SettingsMenuItem("General", Icons.Default.Settings),
        SettingsMenuItem("Kiosk Mode", Icons.Default.Lock),
        SettingsMenuItem("NATS", Icons.Default.Cloud),
        SettingsMenuItem("Security", Icons.Default.Security),
        SettingsMenuItem("Display", Icons.Default.Monitor),
        SettingsMenuItem("Network", Icons.Default.Wifi),
        SettingsMenuItem("About", Icons.Default.Info)
    )

    var selectedItemIndex by remember { mutableIntStateOf(0) }
    val isExpandedScreen = windowSizeClass.widthSizeClass > WindowWidthSizeClass.Compact

    val settingsScreens: List<@Composable () -> Unit> = listOf(
        { GeneralSettingsScreen(configManager) },
        { KioskModeSettingsScreen() },
        { PlaceholderSettingsScreen("NATS") },
        { PlaceholderSettingsScreen("Security") },
        { PlaceholderSettingsScreen("Display") },
        { NetworkSettingsScreen() },
        { PlaceholderSettingsScreen("About") }
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isExpandedScreen) {
            PermanentNavigationDrawer(
                drawerContent = {
                    PermanentDrawerSheet(
                        modifier = Modifier
                            .width(260.dp)
                            .fillMaxHeight()
                            .padding(end = 16.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
                            )
                            .clip(RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)),
                        drawerContainerColor = Color.Transparent,
                        drawerTonalElevation = 0.dp
                    ) {
                        NavigationDrawerContent(settingsItems, selectedItemIndex, { selectedItemIndex = it }, onExitSettings)
                    }
                },
                content = {
                    Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                        ContentPane(selectedItemIndex, settingsScreens)
                    }
                }
            )
        } else {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                    ) {
                        NavigationDrawerContent(settingsItems, selectedItemIndex, {
                            selectedItemIndex = it
                            scope.launch { drawerState.close() }
                        }, onExitSettings)
                    }
                }
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(settingsItems[selectedItemIndex].label) },
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                                }
                            }
                        )
                    }
                ) { padding ->
                    Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                        ContentPane(selectedItemIndex, settingsScreens)
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigationDrawerContent(
    settingsItems: List<SettingsMenuItem>,
    selectedItemIndex: Int,
    onItemSelected: (Int) -> Unit,
    onExitKiosk: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "SYSTEM",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 12.dp, top = 16.dp, bottom = 24.dp),
            letterSpacing = 2.sp
        )

        settingsItems.forEachIndexed { index, item ->
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.label) },
                selected = index == selectedItemIndex,
                onClick = { onItemSelected(index) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(vertical = 2.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent,
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }

        Spacer(Modifier.weight(1f))

        NavigationDrawerItem(
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = MaterialTheme.colorScheme.onErrorContainer) },
            label = { Text("Back to Kiosk", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer) },
            selected = false,
            onClick = onExitKiosk,
            shape = CircleShape,
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = MaterialTheme.colorScheme.errorContainer
            )
        )
    }
}

@Composable
private fun ContentPane(selectedIndex: Int, screens: List<@Composable () -> Unit>) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Crossfade(targetState = selectedIndex, label = "settings-content") {
            screens.getOrNull(it)?.invoke()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GeneralSettingsScreen(configManager: ConfigManager) {
    var url by remember { mutableStateOf(configManager.getUrl() ?: "") }
    var showUrlDialog by remember { mutableStateOf(false) }

    if (showUrlDialog) {
        var tempUrl by remember { mutableStateOf(url) }
        AlertDialog(
            onDismissRequest = { showUrlDialog = false },
            title = { Text("Update Kiosk URL") },
            text = {
                OutlinedTextField(
                    value = tempUrl,
                    onValueChange = { tempUrl = it },
                    label = { Text("Target URL") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    url = tempUrl
                    configManager.saveUrl(tempUrl)
                    showUrlDialog = false
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showUrlDialog = false }) { Text("Cancel") } }
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            SettingsItem(
                icon = Icons.Default.Language,
                title = "PWA URL",
                subtitle = url.ifEmpty { "Enter server address" },
                onClick = { showUrlDialog = true }
            )
        }
    }
}

@Composable
fun KioskModeSettingsScreen() {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            SettingsItem(Icons.Default.Lock, "Lock Task Mode", "Status: Active") // Non-clickable
        }
    }
}

@Composable
private fun NetworkSettingsScreen() {
    var activeSubMenu by remember { mutableStateOf<String?>(null) }

    Crossfade(targetState = activeSubMenu, label = "network-nav") { screen ->
        when (screen) {
            "wifi" -> {
                SubMenuLayout(title = "Wi-Fi Settings", onBack = { activeSubMenu = null }) {
                    PlaceholderSettingsScreen("Wi-Fi Scanner")
                }
            }
            "nats" -> {
                SubMenuLayout(title = "NATS Server", onBack = { activeSubMenu = null }) {
                    PlaceholderSettingsScreen("NATS Config")
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        SettingsItem(
                            icon = Icons.Default.Wifi,
                            title = "Wi-Fi",
                            subtitle = "Connect to a wireless network",
                            onClick = { activeSubMenu = "wifi" }
                        )
                    }
                    item {
                        SettingsItem(
                            icon = Icons.Default.Cloud,
                            title = "NATS Broker",
                            subtitle = "Manage real-time server connection",
                            onClick = { activeSubMenu = "nats" }
                        )
                    }
                    item {
                        SettingsItem(
                            icon = Icons.Default.Security,
                            title = "Tailscale VPN",
                            subtitle = "Disconnected",
                            onClick = null // Non clickable si pas de clé par exemple
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceholderSettingsScreen(name: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Info, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        Spacer(Modifier.height(16.dp))
        Text(name, style = MaterialTheme.typography.headlineSmall)
        Text("Module coming soon", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsItem(
    icon: ImageVector? = null,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null
) {
    val isClickable = onClick != null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isClickable) 1f else 0.6f),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isClickable) 2.dp else 0.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        ListItem(
            modifier = if (isClickable) Modifier.clickable { onClick?.invoke() } else Modifier,
            headlineContent = { Text(title, fontWeight = FontWeight.SemiBold) },
            supportingContent = { Text(subtitle, style = MaterialTheme.typography.bodyMedium) },
            leadingContent = icon?.let { { Icon(it, null, tint = MaterialTheme.colorScheme.primary) } },
            trailingContent = if (isClickable) {
                { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) }
            } else null,
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubMenuLayout(title: String, onBack: () -> Unit, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(title, style = MaterialTheme.typography.titleMedium) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
        Box(modifier = Modifier.fillMaxSize()) { content() }
    }
}