package ps.reso.instaeclipse.ui.component.navigation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.HighQuality
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ps.reso.instaeclipse.core.HookCategory
import ps.reso.instaeclipse.ui.component.common.EmptySearchState
import ps.reso.instaeclipse.ui.component.common.QualityDropdownRow
import ps.reso.instaeclipse.ui.component.common.SwitchRow
import ps.reso.instaeclipse.ui.component.dashboard.AppQuickControls
import ps.reso.instaeclipse.ui.component.dashboard.DownloadFolderCard
import ps.reso.instaeclipse.ui.component.dashboard.GhostMasterShortcutCard
import ps.reso.instaeclipse.ui.component.dashboard.HomeDashboard
import ps.reso.instaeclipse.ui.component.dashboard.SectionCategoryCard
import ps.reso.instaeclipse.ui.component.faq.FaqScreen
import ps.reso.instaeclipse.ui.component.logs.LogsScreen
import ps.reso.instaeclipse.ui.component.settings.SettingsScreen
import ps.reso.instaeclipse.ui.theme.AppSpacing
import ps.reso.instaeclipse.ui.viewmodel.AppNavDestination
import ps.reso.instaeclipse.ui.viewmodel.DashboardUiEffect
import ps.reso.instaeclipse.ui.viewmodel.DashboardUiState
import ps.reso.instaeclipse.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalLayoutApi::class)
@Composable
fun EclipseApp(
    viewModel: DashboardViewModel,
    uiState: DashboardUiState,
    darkTheme: Boolean,
    onPickFolder: () -> Unit,
    onExportConfig: () -> Unit,
    onImportConfig: () -> Unit,
    onOpenLocationPicker: () -> Unit,
    onOpenThemeCustomizer: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as ComponentActivity)
    val isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium ||
        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

    val isImeVisible = WindowInsets.isImeVisible

    BackHandler(enabled = uiState.isSearchActive || uiState.showSettings || uiState.showLogs || uiState.showFaq) {
        viewModel.handleBack()
    }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is DashboardUiEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is DashboardUiEffect.ShowToast -> {
                    android.widget.Toast.makeText(context, effect.message, android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppTopBar(
                currentDestination = uiState.currentDestination,
                showSettings = uiState.showSettings,
                settingsSubpage = uiState.settingsSubpage,
                showLogs = uiState.showLogs,
                showFaq = uiState.showFaq,
                searchActive = uiState.isSearchActive,
                query = uiState.searchQuery,
                onQueryChange = { viewModel.setSearchQuery(it) },
                onSearchActiveChange = { viewModel.setSearchActive(it) },
                onBack = { viewModel.handleBack() },
                onNavigateToSettings = { viewModel.showSettingsScreen() },
                onNavigateToLogs = { viewModel.showLogsScreen() },
                onNavigateToFaq = { viewModel.showFaqScreen() }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = !uiState.showSettings && !uiState.showLogs && !uiState.showFaq && !isImeVisible && (uiState.showAppControls || !isExpanded),
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Column {
                    if (uiState.showAppControls) {
                        AppQuickControls(
                            onForceStop = { viewModel.forceStopInstagram() },
                            onOpenApp = { viewModel.openInstagram() }
                        )
                    }
                    if (!isExpanded) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ) {
                            appNavEntries.forEach { entry ->
                                val selected = entry.destination == uiState.currentDestination
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { viewModel.selectDestination(entry.destination) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) entry.selectedIcon else entry.icon,
                                            contentDescription = stringResource(entry.labelRes)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = stringResource(entry.shortLabelRes),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isExpanded && !uiState.showSettings && !uiState.showLogs && !uiState.showFaq) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Spacer(Modifier.height(AppSpacing.md))
                    appNavEntries.forEach { entry ->
                        val selected = entry.destination == uiState.currentDestination
                        NavigationRailItem(
                            selected = selected,
                            onClick = { viewModel.selectDestination(entry.destination) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) entry.selectedIcon else entry.icon,
                                    contentDescription = stringResource(entry.labelRes)
                                )
                            },
                            label = { Text(stringResource(entry.shortLabelRes)) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            AnimatedContent(
                targetState = when {
                    uiState.showSettings -> "settings"
                    uiState.showLogs -> "logs"
                    uiState.showFaq -> "faq"
                    uiState.isSearchActive -> "search"
                    else -> "main"
                },
                label = "ScreenTransition",
                modifier = Modifier.weight(1f)
            ) { screen ->
                when (screen) {
                    "faq" -> {
                        FaqScreen()
                    }
                    "settings" -> {
                        SettingsScreen(
                            currentSubpage = uiState.settingsSubpage,
                            onNavigateToSubpage = { viewModel.selectSettingsSubpage(it) },
                            themeMode = uiState.themeMode,
                            onThemeModeChange = { viewModel.setThemeMode(it) },
                            amoledEnabled = uiState.isAppAmoledEnabled,
                            onAmoledToggle = { viewModel.setAppAmoled(it) },
                            onExportConfig = onExportConfig,
                            onImportConfig = onImportConfig
                        )
                    }
                    "logs" -> {
                        LogsScreen(
                            logContent = uiState.logContent,
                            lineCount = uiState.logLineCount,
                            searchQuery = uiState.logSearchQuery,
                            isLoading = uiState.isLogLoading,
                            onSearchQueryChange = { viewModel.setLogSearchQuery(it) },
                            onRefresh = { viewModel.refreshLogs() },
                            onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("InstaEclipse Logs", uiState.logContent))
                                viewModel.notifyEffect(DashboardUiEffect.ShowToast("Logs copied to clipboard"))
                            },
                            onClear = { viewModel.clearLogs() },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = AppSpacing.md, vertical = AppSpacing.xs)
                        )
                    }
                    "search" -> {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                start = AppSpacing.md,
                                end = AppSpacing.md,
                                top = AppSpacing.xs,
                                bottom = AppSpacing.md
                            ),
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (uiState.searchQuery.isNotBlank()) {
                                if (uiState.searchResults.isNotEmpty()) {
                                    item(key = "search_results_card") {
                                        Card(
                                            shape = MaterialTheme.shapes.extraLarge,
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                                contentColor = MaterialTheme.colorScheme.onSurface
                                            ),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(vertical = AppSpacing.xs)) {
                                                uiState.searchResults.forEachIndexed { index, hook ->
                                                    if (hook.id == "forceReelQuality") {
                                                        QualityDropdownRow(
                                                            currentQuality = uiState.forceReelQuality,
                                                            onQualitySelected = { viewModel.setForceReelQuality(it) },
                                                            showDivider = index < uiState.searchResults.lastIndex,
                                                            categoryBadge = hook.category.title
                                                        )
                                                    } else {
                                                        val checked = uiState.isHookEnabled(hook.id)
                                                        val isCustomThemeHook = hook.id == "customThemeEnabled"
                                                        SwitchRow(
                                                            title = hook.name,
                                                            checked = checked,
                                                            onCheckedChange = { viewModel.setHookEnabled(hook.id, it) },
                                                            showDivider = index < uiState.searchResults.lastIndex,
                                                            categoryBadge = hook.category.title,
                                                        content = if (isCustomThemeHook) {
                                                            {
                                                                AnimatedVisibility(
                                                                    visible = checked,
                                                                    enter = expandVertically() + fadeIn(),
                                                                    exit = shrinkVertically() + fadeOut()
                                                                ) {
                                                                    Box(
                                                                        modifier = Modifier
                                                                            .fillMaxWidth()
                                                                            .padding(
                                                                                start = AppSpacing.md,
                                                                                end = AppSpacing.md,
                                                                                top = 2.dp,
                                                                                bottom = AppSpacing.sm
                                                                            )
                                                                    ) {
                                                                        FilledTonalButton(
                                                                            onClick = onOpenThemeCustomizer,
                                                                            shape = MaterialTheme.shapes.medium,
                                                                            modifier = Modifier.fillMaxWidth()
                                                                        ) {
                                                                            Icon(Icons.Filled.Palette, contentDescription = null, modifier = Modifier.size(18.dp))
                                                                            Spacer(Modifier.width(AppSpacing.xs))
                                                                            Text("Customize Theme…", fontWeight = FontWeight.SemiBold)
                                                                            Spacer(Modifier.weight(1f))
                                                                            Icon(Icons.AutoMirrored.Filled.Launch, contentDescription = null, modifier = Modifier.size(16.dp))
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        } else null
                                                    )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    item(key = "empty_search") {
                                        EmptySearchState(query = uiState.searchQuery)
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                start = AppSpacing.md,
                                end = AppSpacing.md,
                                top = AppSpacing.xs,
                                bottom = AppSpacing.md
                            ),
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            when (uiState.currentDestination) {
                                AppNavDestination.HOME -> {
                                    item(key = "home_dashboard") {
                                        HomeDashboard(
                                            uiState = uiState,
                                            darkTheme = darkTheme,
                                            onToggleHook = { id, enabled -> viewModel.setHookEnabled(id, enabled) },
                                            onToggleAllGhost = { ids, enabled -> viewModel.setAllHooksEnabled(ids, enabled) },
                                            onToggleShortcutSelection = { viewModel.toggleCustomShortcut(it) },
                                            onToggleGhostChipSelection = { viewModel.toggleGhostMasterChip(it) },
                                            onToggleAppControls = { viewModel.setAppControlsEnabled(it) },
                                            onPickFolder = onPickFolder,
                                            onLaunchInstagram = { viewModel.openInstagram() },
                                            onOpenInstagramAppInfo = { viewModel.openInstagramAppInfo() },
                                            onSelectVariant = { viewModel.setActiveInstagramPackage(it) },
                                            onShowVariantDialog = { viewModel.setShowVariantDialog(it) }
                                        )
                                    }
                                }
                                AppNavDestination.DOWNLOADS -> {
                                    val hooks = uiState.hooksByCategory[HookCategory.DOWNLOADER].orEmpty()

                                    item(key = "downloads_folder_card") {
                                        DownloadFolderCard(
                                            uri = uiState.downloadSafUri,
                                            onPickFolder = onPickFolder
                                        )
                                    }
                                    item(key = "downloads_toggles") {
                                        SectionCategoryCard(
                                            category = HookCategory.DOWNLOADER,
                                            icon = Icons.Outlined.Download,
                                            hooks = hooks,
                                            isHookEnabled = { uiState.isHookEnabled(it) },
                                            onToggle = { id, enabled -> viewModel.setHookEnabled(id, enabled) }
                                        )
                                    }
                                }
                                AppNavDestination.GHOST -> {
                                    val ghostHooks = uiState.hooksByCategory[HookCategory.GHOST].orEmpty()
                                    val lockHooks = uiState.hooksByCategory[HookCategory.LOCK].orEmpty()
                                    val hideChatsHooks = uiState.hooksByCategory[HookCategory.HIDE_CHATS].orEmpty()

                                    if (ghostHooks.isNotEmpty()) {
                                        item(key = "ghost_master_card") {
                                            GhostMasterShortcutCard(
                                                allGhostHooks = ghostHooks,
                                                selectedChipIds = uiState.ghostMasterChipIds,
                                                isHookEnabled = { uiState.isHookEnabled(it) },
                                                onToggleHook = { id, enabled -> viewModel.setHookEnabled(id, enabled) },
                                                onToggleAll = { ids, enabled -> viewModel.setAllHooksEnabled(ids, enabled) },
                                                onToggleChipSelection = { viewModel.toggleGhostMasterChip(it) }
                                            )
                                        }
                                    }
                                    item(key = "ghost_toggles") {
                                        SectionCategoryCard(
                                            category = HookCategory.GHOST,
                                            icon = Icons.Outlined.VisibilityOff,
                                            hooks = ghostHooks,
                                            isHookEnabled = { uiState.isHookEnabled(it) },
                                            onToggle = { id, enabled -> viewModel.setHookEnabled(id, enabled) }
                                        )
                                    }
                                    if (lockHooks.isNotEmpty()) {
                                        item(key = "lock_section") {
                                            SectionCategoryCard(
                                                category = HookCategory.LOCK,
                                                icon = Icons.Outlined.Lock,
                                                hooks = lockHooks,
                                                isHookEnabled = { uiState.isHookEnabled(it) },
                                                onToggle = { id, enabled -> viewModel.setHookEnabled(id, enabled) }
                                            )
                                        }
                                    }
                                    if (hideChatsHooks.isNotEmpty()) {
                                        item(key = "hide_chats_section") {
                                            SectionCategoryCard(
                                                category = HookCategory.HIDE_CHATS,
                                                icon = Icons.AutoMirrored.Outlined.Chat,
                                                hooks = hideChatsHooks,
                                                isHookEnabled = { uiState.isHookEnabled(it) },
                                                onToggle = { id, enabled -> viewModel.setHookEnabled(id, enabled) }
                                            )
                                        }
                                    }
                                }
                                AppNavDestination.FEED -> {
                                    val cleanFeedHooks = uiState.hooksByCategory[HookCategory.CLEAN_FEED].orEmpty()
                                    val distractionHooks = uiState.hooksByCategory[HookCategory.DISTRACTION_FREE].orEmpty()
                                    val qualityHooks = uiState.hooksByCategory[HookCategory.QUALITY].orEmpty()

                                    item(key = "clean_feed_section") {
                                        SectionCategoryCard(
                                            category = HookCategory.CLEAN_FEED,
                                            icon = Icons.Outlined.AutoAwesome,
                                            hooks = cleanFeedHooks,
                                            isHookEnabled = { uiState.isHookEnabled(it) },
                                            onToggle = { id, enabled -> viewModel.setHookEnabled(id, enabled) }
                                        )
                                    }
                                    item(key = "distraction_free_section") {
                                        SectionCategoryCard(
                                            category = HookCategory.DISTRACTION_FREE,
                                            icon = Icons.Outlined.Block,
                                            hooks = distractionHooks,
                                            isHookEnabled = { uiState.isHookEnabled(it) },
                                            onToggle = { id, enabled -> viewModel.setHookEnabled(id, enabled) }
                                        )
                                    }
                                    item(key = "quality_section") {
                                        SectionCategoryCard(
                                            category = HookCategory.QUALITY,
                                            icon = Icons.Outlined.HighQuality,
                                            hooks = qualityHooks,
                                            isHookEnabled = { uiState.isHookEnabled(it) },
                                            onToggle = { id, enabled -> viewModel.setHookEnabled(id, enabled) },
                                            forceReelQuality = uiState.forceReelQuality,
                                            onQualitySelected = { viewModel.setForceReelQuality(it) }
                                        )
                                    }
                                }
                                AppNavDestination.MORE -> {
                                    val themeHooks = uiState.hooksByCategory[HookCategory.THEME].orEmpty()
                                    val adsHooks = uiState.hooksByCategory[HookCategory.ADS].orEmpty()
                                    val locationHooks = uiState.hooksByCategory[HookCategory.LOCATION].orEmpty()
                                    val miscHooks = uiState.hooksByCategory[HookCategory.MISC].orEmpty()
                                    val devHooks = uiState.hooksByCategory[HookCategory.DEV_OPTIONS].orEmpty()

                                    item(key = "theme_section") {
                                        SectionCategoryCard(
                                            category = HookCategory.THEME,
                                            icon = Icons.Outlined.Palette,
                                            hooks = themeHooks,
                                            isHookEnabled = { uiState.isHookEnabled(it) },
                                            onToggle = { id, enabled -> viewModel.setHookEnabled(id, enabled) },
                                            onOpenThemeCustomizer = onOpenThemeCustomizer
                                        )
                                    }
                                    item(key = "ads_section") {
                                        SectionCategoryCard(
                                            category = HookCategory.ADS,
                                            icon = Icons.Outlined.Shield,
                                            hooks = adsHooks,
                                            isHookEnabled = { uiState.isHookEnabled(it) },
                                            onToggle = { id, enabled -> viewModel.setHookEnabled(id, enabled) }
                                        )
                                    }
                                    item(key = "location_section") {
                                        SectionCategoryCard(
                                            category = HookCategory.LOCATION,
                                            icon = Icons.Outlined.Place,
                                            hooks = locationHooks,
                                            isHookEnabled = { uiState.isHookEnabled(it) },
                                            onToggle = { id, enabled -> viewModel.setHookEnabled(id, enabled) },
                                            onOpenLocationPicker = onOpenLocationPicker
                                        )
                                    }
                                    item(key = "misc_section") {
                                        SectionCategoryCard(
                                            category = HookCategory.MISC,
                                            icon = Icons.Outlined.Widgets,
                                            hooks = miscHooks,
                                            isHookEnabled = { uiState.isHookEnabled(it) },
                                            onToggle = { id, enabled -> viewModel.setHookEnabled(id, enabled) }
                                        )
                                    }
                                    item(key = "dev_section") {
                                        SectionCategoryCard(
                                            category = HookCategory.DEV_OPTIONS,
                                            icon = Icons.Outlined.Code,
                                            hooks = devHooks,
                                            isHookEnabled = { uiState.isHookEnabled(it) },
                                            onToggle = { id, enabled -> viewModel.setHookEnabled(id, enabled) }
                                        )
                                    }
                                }
                            }

                            item(key = "bottom_spacer") { Spacer(Modifier.height(AppSpacing.xs)) }
                        }
                    }
                }
            }
        }
    }
}
