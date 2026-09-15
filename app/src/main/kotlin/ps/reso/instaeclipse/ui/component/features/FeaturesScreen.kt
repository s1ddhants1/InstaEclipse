package ps.reso.instaeclipse.ui.component.features

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import ps.reso.instaeclipse.core.HookCategory
import ps.reso.instaeclipse.ui.component.dashboard.SectionCategoryCard
import ps.reso.instaeclipse.ui.theme.AppSpacing
import ps.reso.instaeclipse.ui.viewmodel.DashboardUiState

@Composable
fun FeaturesScreen(
    uiState: DashboardUiState,
    onToggleHook: (String, Boolean) -> Unit,
    onOpenLocationPicker: () -> Unit,
    onOpenThemeCustomizer: () -> Unit,
    onQualitySelected: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategoryFilter by remember { mutableStateOf<HookCategory?>(null) }

    val categories = remember { HookCategory.entries }

    val displayedCategories = if (selectedCategoryFilter != null) {
        listOf(selectedCategoryFilter!!)
    } else {
        categories
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
        ) {
            FilterChip(
                selected = selectedCategoryFilter == null,
                onClick = { selectedCategoryFilter = null },
                label = { Text("All") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )

            categories.forEach { cat ->
                FilterChip(
                    selected = selectedCategoryFilter == cat,
                    onClick = {
                        selectedCategoryFilter = if (selectedCategoryFilter == cat) null else cat
                    },
                    label = { Text(cat.title) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        displayedCategories.forEach { category ->
            val hooks = uiState.hooksByCategory[category] ?: emptyList()
            SectionCategoryCard(
                category = category,
                icon = getCategoryIcon(category),
                hooks = hooks,
                isHookEnabled = { uiState.isHookEnabled(it) },
                onToggle = onToggleHook,
                onOpenLocationPicker = if (category == HookCategory.LOCATION) onOpenLocationPicker else null,
                onOpenThemeCustomizer = if (category == HookCategory.THEME) onOpenThemeCustomizer else null,
                forceReelQuality = uiState.forceReelQuality,
                onQualitySelected = onQualitySelected
            )
        }
    }
}

fun getCategoryIcon(category: HookCategory): ImageVector {
    return when (category) {
        HookCategory.DOWNLOADER -> Icons.Outlined.Download
        HookCategory.GHOST -> Icons.Outlined.VisibilityOff
        HookCategory.LOCK -> Icons.Outlined.Lock
        HookCategory.HIDE_CHATS -> Icons.AutoMirrored.Outlined.Chat
        HookCategory.CLEAN_FEED -> Icons.Outlined.AutoAwesome
        HookCategory.DISTRACTION_FREE -> Icons.Outlined.Block
        HookCategory.QUALITY -> Icons.Outlined.HighQuality
        HookCategory.THEME -> Icons.Outlined.Palette
        HookCategory.ADS -> Icons.Outlined.Shield
        HookCategory.LOCATION -> Icons.Outlined.Place
        HookCategory.MISC -> Icons.Outlined.Widgets
        HookCategory.DEV_OPTIONS -> Icons.Outlined.Code
    }
}
