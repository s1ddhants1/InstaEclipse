package ps.reso.instaeclipse.ui.component.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WebStories
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.WebStories
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.ui.graphics.vector.ImageVector
import ps.reso.instaeclipse.R
import ps.reso.instaeclipse.ui.viewmodel.AppNavDestination

data class AppNavEntry(
    val destination: AppNavDestination,
    @StringRes val labelRes: Int,
    @StringRes val shortLabelRes: Int,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)

val appNavEntries = listOf(
    AppNavEntry(
        destination = AppNavDestination.DOWNLOADS,
        labelRes = R.string.cat_downloads,
        shortLabelRes = R.string.cat_downloads_short,
        icon = Icons.Outlined.Download,
        selectedIcon = Icons.Filled.Download
    ),
    AppNavEntry(
        destination = AppNavDestination.GHOST,
        labelRes = R.string.cat_ghost,
        shortLabelRes = R.string.cat_ghost_short,
        icon = Icons.Outlined.VisibilityOff,
        selectedIcon = Icons.Filled.VisibilityOff
    ),
    AppNavEntry(
        destination = AppNavDestination.HOME,
        labelRes = R.string.nav_home,
        shortLabelRes = R.string.nav_home,
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    ),
    AppNavEntry(
        destination = AppNavDestination.FEED,
        labelRes = R.string.cat_feed,
        shortLabelRes = R.string.cat_feed_short,
        icon = Icons.Outlined.WebStories,
        selectedIcon = Icons.Filled.WebStories
    ),
    AppNavEntry(
        destination = AppNavDestination.MORE,
        labelRes = R.string.nav_more_full,
        shortLabelRes = R.string.nav_more,
        icon = Icons.Outlined.Widgets,
        selectedIcon = Icons.Filled.Widgets
    )
)
