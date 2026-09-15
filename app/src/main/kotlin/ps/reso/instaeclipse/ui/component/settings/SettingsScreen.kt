package ps.reso.instaeclipse.ui.component.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import ps.reso.instaeclipse.core.PreferencesManager.ThemeMode
import ps.reso.instaeclipse.ui.viewmodel.SettingsSubpage

@Composable
fun SettingsScreen(
    currentSubpage: SettingsSubpage?,
    onNavigateToSubpage: (SettingsSubpage) -> Unit,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    amoledEnabled: Boolean,
    onAmoledToggle: (Boolean) -> Unit,
    onExportConfig: () -> Unit,
    onImportConfig: () -> Unit
) {
    AnimatedContent(
        targetState = currentSubpage,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "SettingsSubpageTransition"
    ) { subpage ->
        when (subpage) {
            null -> SettingsIndexPage(onNavigateToSubpage = onNavigateToSubpage)
            SettingsSubpage.APPEARANCE -> AppearanceSettingsPage(
                themeMode = themeMode,
                onThemeModeChange = onThemeModeChange,
                amoledEnabled = amoledEnabled,
                onAmoledToggle = onAmoledToggle
            )
            SettingsSubpage.BACKUP -> BackupSettingsPage(
                onExportClick = onExportConfig,
                onImportClick = onImportConfig
            )
            SettingsSubpage.ABOUT -> AboutSettingsPage()
        }
    }
}
