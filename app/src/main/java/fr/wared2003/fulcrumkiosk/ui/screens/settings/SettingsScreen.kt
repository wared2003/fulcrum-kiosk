package fr.wared2003.fulcrumkiosk.ui.screens.settings

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.wared2003.fulcrumkiosk.MainActivity
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
private data class SettingsMenuItem(val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

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
        { GeneralSettingsScreen(state, viewModel::onEvent) },
        { KioskModeSettingsScreen() },
        { PlaceholderSettingsScreen("NATS") },
        { PlaceholderSettingsScreen("Security") },
        { PlaceholderSettingsScreen("Display") },
        { NetworkSettingsScreen() },
        { PlaceholderSettingsScreen("About") }
    )

    val context = LocalContext.current

    // Dès que l'écran s'affiche, on libère la tablette
    LaunchedEffect(Unit) {
        (context as? MainActivity)?.unlockForAdmin()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
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
                        NavigationDrawerContent(
                            settingsItems,
                            selectedItemIndex,
                            { selectedItemIndex = it },
                            { viewModel.onEvent(SettingsEvent.OnExitSettingsClicked) }
                        )
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
                        NavigationDrawerContent(
                            settingsItems,
                            selectedItemIndex,
                            {
                                selectedItemIndex = it
                                scope.launch { drawerState.close() }
                            },
                            { viewModel.onEvent(SettingsEvent.OnExitSettingsClicked) }
                        )
                    }
                },
                content = {
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
                        },
                        content = { padding ->
                            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                                ContentPane(selectedItemIndex, settingsScreens)
                            }
                        }
                    )
                }
            )
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
private fun GeneralSettingsScreen(state: SettingsState, onEvent: (SettingsEvent) -> Unit) {
    if (state.showUrlDialog) {
        AlertDialog(
            onDismissRequest = { onEvent(SettingsEvent.OnDismissUrlDialog) },
            title = { Text("Update Kiosk URL") },
            text = {
                OutlinedTextField(
                    value = state.url,
                    onValueChange = { onEvent(SettingsEvent.OnUrlChanged(it)) },
                    label = { Text("Target URL") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = { onEvent(SettingsEvent.OnSaveUrlClicked) }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { onEvent(SettingsEvent.OnDismissUrlDialog) }) { Text("Cancel") } }
        )
    }

    if (state.showTailscaleDialog) {
        AlertDialog(
            onDismissRequest = { onEvent(SettingsEvent.OnDismissTailscaleDialog) },
            title = { Text("Tailscale Key") },
            text = {
                OutlinedTextField(
                    value = state.tailscaleKey,
                    onValueChange = { onEvent(SettingsEvent.OnTailscaleKeyChanged(it)) },
                    label = { Text("tskey-auth-...") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = { onEvent(SettingsEvent.OnSaveTailscaleKeyClicked) }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { onEvent(SettingsEvent.OnDismissTailscaleDialog) }) { Text("Cancel") } }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SettingsItem(
                icon = Icons.Default.Language,
                title = "PWA URL",
                subtitle = state.url.ifEmpty { "Enter server address" },
                onClick = { onEvent(SettingsEvent.OnPwaUrlClicked) }
            )
        }
        item {
            SettingsItem(
                icon = Icons.Default.Key,
                title = "Tailscale Key",
                subtitle = if (state.tailscaleKey.isEmpty()) "Not configured" else "••••••••••••••",
                onClick = { onEvent(SettingsEvent.OnTailscaleKeyClicked) }
            )
        }
    }
}

@Composable
fun KioskModeSettingsScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SettingsItem(Icons.Default.Lock, "Lock Task Mode", "Status: Active", onClick = null)
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
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
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
        Icon(
            Icons.Default.Info,
            null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
        Spacer(Modifier.height(16.dp))
        Text(name, style = MaterialTheme.typography.headlineSmall)
        Text("Module coming soon", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isClickable) 2.dp else 0.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        ListItem(
            modifier = if (isClickable) Modifier.clickable { onClick?.invoke() } else Modifier,
            headlineContent = { Text(title, fontWeight = FontWeight.SemiBold) },
            supportingContent = { Text(subtitle, style = MaterialTheme.typography.bodySmall) },
            trailingContent = {
                if (isClickable) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                }
            },
            leadingContent = icon?.let {
                {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(it, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
        )
    }
}
