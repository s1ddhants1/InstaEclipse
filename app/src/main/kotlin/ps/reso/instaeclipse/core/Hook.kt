package ps.reso.instaeclipse.core

import ps.reso.instaeclipse.R

enum class HookCategory(val title: String, val labelRes: Int) {
    DOWNLOADER("Downloader", R.string.ig_dialog_section_downloader),
    GHOST("Ghost Mode", R.string.ig_dialog_section_ghost_mode),
    CLEAN_FEED("Clean Feed", R.string.ig_dialog_section_clean_feed),
    DISTRACTION_FREE("Distraction-Free", R.string.ig_dialog_section_distraction_free),
    QUALITY("Video Quality", R.string.ig_dialog_section_quality),
    THEME("Custom Theme", R.string.theme_title),
    ADS("Ad & Analytics Block", R.string.ig_dialog_section_ad_analytics),
    LOCK("App Lock", R.string.ig_dialog_misc_lock_section),
    HIDE_CHATS("Hide Specific Chats", R.string.ig_hide_chats_title),
    LOCATION("Location", R.string.ig_dialog_section_location),
    MISC("Miscellaneous", R.string.ig_dialog_section_misc),
    DEV_OPTIONS("Developer Options", R.string.ig_dialog_section_dev_options)
}

data class Hook(
    val id: String,
    val name: String,
    val description: String,
    val category: HookCategory,
    val defaultEnabled: Boolean = false,
    val isExtreme: Boolean = false,
    val requiresAnyOf: List<String> = emptyList(),
    val disabledWhenTrue: String? = null,
    val dependsOn: String? = null,
    val cascadeOffKey: String? = null
)
