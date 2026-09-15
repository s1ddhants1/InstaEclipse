package ps.reso.instaeclipse.ui.component.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ps.reso.instaeclipse.core.Hook
import ps.reso.instaeclipse.core.HookCategory
import ps.reso.instaeclipse.ui.theme.AppSpacing

@Composable
fun CustomShortcutCard(
    hook: Hook,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val contentAlpha = if (enabled) 1f else 0.38f

    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .toggleable(
                    value = isEnabled,
                    enabled = enabled,
                    role = Role.Switch,
                    onValueChange = onToggle
                )
                .padding(horizontal = AppSpacing.md, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = AppSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isEnabled) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = contentAlpha)
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = contentAlpha)
                    },
                    contentColor = if (isEnabled) {
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = contentAlpha)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha)
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = getShortcutIcon(hook),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = hook.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isEnabled) FontWeight.SemiBold else FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Switch(
                checked = isEnabled,
                enabled = enabled,
                onCheckedChange = null,
                thumbContent = if (isEnabled) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize)
                        )
                    }
                } else null,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    checkedIconColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    disabledCheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    disabledCheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.38f),
                    disabledCheckedIconColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.38f),
                    disabledUncheckedThumbColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.38f),
                    disabledUncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.38f)
                )
            )
        }
    }
}

fun getShortcutIcon(hook: Hook): ImageVector {
    return when (hook.category) {
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
