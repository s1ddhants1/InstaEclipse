package ps.reso.instaeclipse.ui.viewmodel

import androidx.compose.runtime.Immutable
import ps.reso.instaeclipse.core.Hook
import ps.reso.instaeclipse.core.HookCategory
import ps.reso.instaeclipse.core.PreferencesManager

enum class AppNavDestination {
    DOWNLOADS,
    GHOST,
    HOME,
    FEED,
    MORE
}

enum class SettingsSubpage {
    APPEARANCE,
    BACKUP,
    ABOUT
}

const val GHOST_MASTER_SHORTCUT_ID = "ghost_master"

@Immutable
data class InstalledInstagramInfo(
    val packageName: String,
    val versionName: String,
    val variantLabel: String
)

@Immutable
data class DashboardUiState(
    val isModuleActive: Boolean = false,
    val detectedFramework: String? = null,
    val currentDestination: AppNavDestination = AppNavDestination.HOME,
    val showSettings: Boolean = false,
    val settingsSubpage: SettingsSubpage? = null,
    val showLogs: Boolean = false,
    val showFaq: Boolean = false,
    val themeMode: PreferencesManager.ThemeMode = PreferencesManager.ThemeMode.SYSTEM,
    val isAppAmoledEnabled: Boolean = false,
    val showAppControls: Boolean = true,
    val downloadSafUri: String = "",

    val activeInstagram: InstalledInstagramInfo? = null,
    val installedInstagramVariants: List<InstalledInstagramInfo> = emptyList(),
    val showVariantDialog: Boolean = false,

    val logContent: String = "",
    val logLineCount: Int = 0,
    val logSearchQuery: String = "",
    val isLogLoading: Boolean = false,

    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val enabledHookIds: Set<String> = emptySet(),
    val customShortcutIds: List<String> = emptyList(),
    val ghostMasterChipIds: List<String> = emptyList(),
    val allHooks: List<Hook> = emptyList(),
    val forceReelQuality: Int = 0
) {
    fun isDarkTheme(systemDark: Boolean): Boolean = when (themeMode) {
        PreferencesManager.ThemeMode.LIGHT -> false
        PreferencesManager.ThemeMode.DARK -> true
        PreferencesManager.ThemeMode.SYSTEM -> systemDark
    }

    val searchResults: List<Hook>
        get() = if (searchQuery.isBlank()) emptyList()
        else allHooks.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                it.category.title.contains(searchQuery, ignoreCase = true)
        }

    val hooksByCategory: Map<HookCategory, List<Hook>>
        get() = allHooks.groupBy { it.category }

    fun isHookEnabled(id: String): Boolean = enabledHookIds.contains(id)
}

sealed interface DashboardUiEffect {
    data class ShowToast(val message: String) : DashboardUiEffect
    data class ShowSnackbar(val message: String) : DashboardUiEffect
}
