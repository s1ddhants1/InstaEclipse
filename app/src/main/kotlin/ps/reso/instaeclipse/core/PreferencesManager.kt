package ps.reso.instaeclipse.core

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import org.json.JSONObject
import ps.reso.instaeclipse.utils.feature.FeatureFlags
import java.util.concurrent.Executors

object PreferencesManager {

    private const val TAG = "InstaEclipse.Prefs"
    const val PREFS_NAME = "instaeclipse_prefs"

    const val KEY_APP_THEME_MODE = "app_theme_mode"
    const val KEY_APP_AMOLED_THEME = "app_amoled_theme"
    const val KEY_SHOW_APP_CONTROLS = "show_app_controls"
    const val KEY_CUSTOM_SHORTCUTS = "custom_dashboard_shortcuts"
    const val KEY_GHOST_MASTER_CHIPS = "ghost_master_chips"
    const val KEY_DOWNLOAD_SAF_URI = "downloaderCustomUri"
    const val KEY_DOWNLOAD_SAF_PATH = "downloaderCustomPath"

    val DEFAULT_GHOST_CHIPS = listOf(
        "isGhostSeen",
        "isGhostTyping",
        "isGhostScreenshot",
        "isGhostViewOnce",
        "isGhostStory",
        "isGhostLive"
    )

    enum class ThemeMode {
        SYSTEM, LIGHT, DARK
    }

    var appThemeMode: ThemeMode = ThemeMode.SYSTEM
    var isAppAmoledEnabled: Boolean = false
    var showAppControls: Boolean = true
    var downloadSafUri: String = ""

    var onPreferenceChangedListener: (() -> Unit)? = null

    @Volatile
    private var localPrefs: SharedPreferences? = null

    private val syncExecutor = Executors.newSingleThreadExecutor { r ->
        Thread(r, "InstaEclipse-PrefSync").apply { isDaemon = true }
    }

    fun init(context: Context) {
        if (localPrefs != null) return
        val targetContext = context.applicationContext ?: context
        try {
            val lp = targetContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            localPrefs = lp
            loadAll(targetContext)
        } catch (t: Throwable) {
            Log.w(TAG, "Failed initializing SharedPreferences: ${t.message}")
        }
    }

    fun getPrefs(context: Context? = null): SharedPreferences {
        localPrefs?.let { return it }
        if (context != null) {
            val lp = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            localPrefs = lp
            return lp
        }
        throw IllegalStateException("SharedPreferences not initialized")
    }

    fun loadAll(context: Context) {
        val prefs = getPrefs(context)

        appThemeMode = parseThemeMode(prefs.getString(KEY_APP_THEME_MODE, null))
        isAppAmoledEnabled = prefs.getBoolean(KEY_APP_AMOLED_THEME, false)
        showAppControls = prefs.getBoolean(KEY_SHOW_APP_CONTROLS, true)
        downloadSafUri = prefs.getString(KEY_DOWNLOAD_SAF_URI, "") ?: ""
        FeatureFlags.isDevEnabled = prefs.getBoolean("isDevEnabled", FeatureFlags.isDevEnabled)
        FeatureFlags.isGhostModeEnabled = prefs.getBoolean("isGhostModeEnabled", FeatureFlags.isGhostModeEnabled)
        FeatureFlags.isGhostSeen = prefs.getBoolean("isGhostSeen", FeatureFlags.isGhostSeen)
        FeatureFlags.isGhostTyping = prefs.getBoolean("isGhostTyping", FeatureFlags.isGhostTyping)
        FeatureFlags.isGhostScreenshot = prefs.getBoolean("isGhostScreenshot", FeatureFlags.isGhostScreenshot)
        FeatureFlags.isGhostViewOnce = prefs.getBoolean("isGhostViewOnce", FeatureFlags.isGhostViewOnce)
        FeatureFlags.isGhostStory = prefs.getBoolean("isGhostStory", FeatureFlags.isGhostStory)
        FeatureFlags.isGhostLive = prefs.getBoolean("isGhostLive", FeatureFlags.isGhostLive)
        FeatureFlags.allowScreenshots = prefs.getBoolean("allowScreenshots", FeatureFlags.allowScreenshots)
        FeatureFlags.keepEphemeralMessages = prefs.getBoolean("keepEphemeralMessages", FeatureFlags.keepEphemeralMessages)
        FeatureFlags.permanentViewMode = prefs.getBoolean("permanentViewMode", FeatureFlags.permanentViewMode)
        FeatureFlags.keepUnsentMessages = prefs.getBoolean("keepUnsentMessages", FeatureFlags.keepUnsentMessages)
        FeatureFlags.lockDirectMessages = prefs.getBoolean("lockDirectMessages", FeatureFlags.lockDirectMessages)
        FeatureFlags.hideSpecificChats = prefs.getBoolean("hideSpecificChats", FeatureFlags.hideSpecificChats)

        val ghostChips = getGhostMasterChips(context).toSet()
        FeatureFlags.quickToggleSeen = "isGhostSeen" in ghostChips
        FeatureFlags.quickToggleTyping = "isGhostTyping" in ghostChips
        FeatureFlags.quickToggleScreenshot = "isGhostScreenshot" in ghostChips
        FeatureFlags.quickToggleViewOnce = "isGhostViewOnce" in ghostChips
        FeatureFlags.quickToggleStory = "isGhostStory" in ghostChips
        FeatureFlags.quickToggleLive = "isGhostLive" in ghostChips
        FeatureFlags.quickToggleEphemeral = "keepEphemeralMessages" in ghostChips
        FeatureFlags.quickTogglePermanentView = "permanentViewMode" in ghostChips
        FeatureFlags.quickToggleAllowScreenshots = "allowScreenshots" in ghostChips

        FeatureFlags.enablePostDownload = prefs.getBoolean("enablePostDownload", FeatureFlags.enablePostDownload)
        FeatureFlags.enableStoryDownload = prefs.getBoolean("enableStoryDownload", FeatureFlags.enableStoryDownload)
        FeatureFlags.enableReelDownload = prefs.getBoolean("enableReelDownload", FeatureFlags.enableReelDownload)
        FeatureFlags.enableProfileDownload = prefs.getBoolean("enableProfileDownload", FeatureFlags.enableProfileDownload)
        FeatureFlags.saveInstants = prefs.getBoolean("saveInstants", FeatureFlags.saveInstants)
        FeatureFlags.uploadInstants = prefs.getBoolean("uploadInstants", FeatureFlags.uploadInstants)
        FeatureFlags.copyMediaLink = prefs.getBoolean("copyMediaLink", FeatureFlags.copyMediaLink)
        FeatureFlags.cacheStories = prefs.getBoolean("cacheStories", FeatureFlags.cacheStories)
        FeatureFlags.downloaderUsernameFolder = prefs.getBoolean("downloaderUsernameFolder", FeatureFlags.downloaderUsernameFolder)
        FeatureFlags.downloaderAddTimestamp = prefs.getBoolean("downloaderAddTimestamp", FeatureFlags.downloaderAddTimestamp)

        FeatureFlags.hideSuggestionsInFeed = prefs.getBoolean("hideSuggestionsInFeed", FeatureFlags.hideSuggestionsInFeed)
        FeatureFlags.hideThreadsSuggestions = prefs.getBoolean("hideThreadsSuggestions", FeatureFlags.hideThreadsSuggestions)
        FeatureFlags.limitFollowingFeed = prefs.getBoolean("limitFollowingFeed", FeatureFlags.limitFollowingFeed)
        FeatureFlags.disableFeed = prefs.getBoolean("disableFeed", FeatureFlags.disableFeed)
        FeatureFlags.disableStories = prefs.getBoolean("disableStories", FeatureFlags.disableStories)
        FeatureFlags.disableReels = prefs.getBoolean("disableReels", FeatureFlags.disableReels)
        FeatureFlags.disableReelsExceptDM = prefs.getBoolean("disableReelsExceptDM", FeatureFlags.disableReelsExceptDM)
        FeatureFlags.disableExplore = prefs.getBoolean("disableExplore", FeatureFlags.disableExplore)
        FeatureFlags.disableComments = prefs.getBoolean("disableComments", FeatureFlags.disableComments)
        FeatureFlags.enablePhotoZoom = prefs.getBoolean("enablePhotoZoom", FeatureFlags.enablePhotoZoom)
        FeatureFlags.isExtremeMode = prefs.getBoolean("isExtremeMode", FeatureFlags.isExtremeMode)
        FeatureFlags.isDistractionFree = prefs.getBoolean("isDistractionFree", FeatureFlags.isDistractionFree)

        FeatureFlags.forceReelQuality = getForceReelQuality(context)
        FeatureFlags.disableVideoAutoPlay = prefs.getBoolean("disableVideoAutoPlay", FeatureFlags.disableVideoAutoPlay)
        FeatureFlags.disableStoryFlipping = prefs.getBoolean("disableStoryFlipping", FeatureFlags.disableStoryFlipping)
        FeatureFlags.enableStoryMentions = prefs.getBoolean("enableStoryMentions", FeatureFlags.enableStoryMentions)

        FeatureFlags.disableDoubleTapLike = prefs.getBoolean("disableDoubleTapLike", FeatureFlags.disableDoubleTapLike)
        FeatureFlags.disableRepost = prefs.getBoolean("disableRepost", FeatureFlags.disableRepost)
        FeatureFlags.disableDiscoverPeople = prefs.getBoolean("disableDiscoverPeople", FeatureFlags.disableDiscoverPeople)
        FeatureFlags.enableCopyComment = prefs.getBoolean("enableCopyComment", FeatureFlags.enableCopyComment)
        FeatureFlags.enableCaptionCopy = prefs.getBoolean("enableCaptionCopy", FeatureFlags.enableCaptionCopy)

        FeatureFlags.isMiscEnabled = prefs.getBoolean("isMiscEnabled", FeatureFlags.isMiscEnabled)
        FeatureFlags.spoofLocation = prefs.getBoolean("spoofLocation", FeatureFlags.spoofLocation)
        FeatureFlags.spoofLastSeen = prefs.getBoolean("spoofLastSeen", FeatureFlags.spoofLastSeen)
        val latStr = prefs.getString("spoofLat", null)
        if (latStr != null) {
            try { FeatureFlags.spoofLat = latStr.toDouble() } catch (ignored: Throwable) {}
        }
        val lngStr = prefs.getString("spoofLng", null)
        if (lngStr != null) {
            try { FeatureFlags.spoofLng = lngStr.toDouble() } catch (ignored: Throwable) {}
        }
        FeatureFlags.isAdBlockEnabled = prefs.getBoolean("isAdBlockEnabled", FeatureFlags.isAdBlockEnabled)
        FeatureFlags.isAnalyticsBlocked = prefs.getBoolean("isAnalyticsBlocked", FeatureFlags.isAnalyticsBlocked)
        FeatureFlags.disableTrackingLinks = prefs.getBoolean("disableTrackingLinks", FeatureFlags.disableTrackingLinks)

        FeatureFlags.customThemeEnabled = prefs.getBoolean("customThemeEnabled", FeatureFlags.customThemeEnabled)
        FeatureFlags.themePresetId = prefs.getInt("themePresetId", FeatureFlags.themePresetId)
        FeatureFlags.themePaletteJson = prefs.getString("themePaletteJson", FeatureFlags.themePaletteJson) ?: ""
        FeatureFlags.customFontEnabled = prefs.getBoolean("customFontEnabled", FeatureFlags.customFontEnabled)
        FeatureFlags.customFontPath = prefs.getString("customFontPath", FeatureFlags.customFontPath) ?: ""
        FeatureFlags.customEmojiEnabled = prefs.getBoolean("customEmojiEnabled", FeatureFlags.customEmojiEnabled)
        FeatureFlags.customEmojiPath = prefs.getString("customEmojiPath", FeatureFlags.customEmojiPath) ?: ""
        FeatureFlags.removeMetaAI = prefs.getBoolean("removeMetaAI", FeatureFlags.removeMetaAI)

        FeatureFlags.removeBuildExpiredPopup = prefs.getBoolean("removeBuildExpiredPopup", FeatureFlags.removeBuildExpiredPopup)
        FeatureFlags.autoClearCache = prefs.getBoolean("autoClearCache", FeatureFlags.autoClearCache)
        FeatureFlags.autoClearCacheSizeMb = prefs.getInt("autoClearCacheSizeMb", FeatureFlags.autoClearCacheSizeMb)
        FeatureFlags.showFeatureToasts = prefs.getBoolean("showFeatureToasts", FeatureFlags.showFeatureToasts)
        FeatureFlags.showFollowerToast = prefs.getBoolean("showFollowerToast", FeatureFlags.showFollowerToast)
        FeatureFlags.downloaderCustomUri = prefs.getString(KEY_DOWNLOAD_SAF_URI, "") ?: ""
        FeatureFlags.downloaderCustomPath = prefs.getString(KEY_DOWNLOAD_SAF_PATH, "") ?: ""
        FeatureFlags.lockDirectAlways = prefs.getBoolean("lockDirectAlways", FeatureFlags.lockDirectAlways)
        FeatureFlags.lockWholeApp = prefs.getBoolean("lockWholeApp", FeatureFlags.lockWholeApp)
        FeatureFlags.lockUseFingerprint = prefs.getBoolean("lockUseFingerprint", FeatureFlags.lockUseFingerprint)
    }

    fun isHookEnabled(context: Context, key: String): Boolean {
        if (key == "forceReelQuality") {
            return getForceReelQuality(context) > 0
        }
        val hook = HookRegistry.getHook(key)
        val defaultVal = hook?.defaultEnabled ?: false
        return try {
            getPrefs(context).getBoolean(key, defaultVal)
        } catch (t: Throwable) {
            defaultVal
        }
    }

    fun getForceReelQuality(context: Context): Int {
        val prefs = getPrefs(context)
        return try {
            prefs.getInt("forceReelQuality", FeatureFlags.forceReelQuality)
        } catch (t: ClassCastException) {
            val migrated = try {
                if (prefs.getBoolean("forceReelQuality", false)) 1080 else 0
            } catch (t2: Throwable) {
                0
            }
            try {
                prefs.edit().putInt("forceReelQuality", migrated).apply()
            } catch (t3: Throwable) {
                Log.e(TAG, "Error migrating forceReelQuality pref: ${t3.message}")
            }
            migrated
        } catch (t: Throwable) {
            FeatureFlags.forceReelQuality
        }
    }

    fun setBoolean(context: Context, key: String, value: Boolean, notifyIpc: Boolean = true) {
        try {
            getPrefs(context).edit().putBoolean(key, value).apply()
            updateFieldDirectly(key, value)
        } catch (t: Throwable) {
            Log.e(TAG, "Error writing boolean preference $key: ${t.message}")
        }
        if (notifyIpc) {
            broadcastPrefChange(context, key, value)
            syncToCompanionContentProvider(context, key, value)
        }
    }

    fun setString(context: Context, key: String, value: String, notifyIpc: Boolean = true) {
        try {
            getPrefs(context).edit().putString(key, value).apply()
            if (key == KEY_DOWNLOAD_SAF_URI) {
                downloadSafUri = value
                FeatureFlags.downloaderCustomUri = value
            } else if (key == KEY_DOWNLOAD_SAF_PATH) {
                FeatureFlags.downloaderCustomPath = value
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Error writing string preference $key: ${t.message}")
        }
        if (notifyIpc) {
            broadcastPrefStringChange(context, key, value)
        }
    }

    fun setInt(context: Context, key: String, value: Int, notifyIpc: Boolean = true) {
        try {
            getPrefs(context).edit().putInt(key, value).apply()
            if (key == "forceReelQuality") {
                FeatureFlags.forceReelQuality = value
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Error writing int preference $key: ${t.message}")
        }
        if (notifyIpc) {
            broadcastPrefIntChange(context, key, value)
            syncIntToCompanionContentProvider(context, key, value)
        }
    }

    private fun broadcastPrefChange(context: Context, key: String, value: Boolean) {
        try {
            val intent = Intent("ps.reso.instaeclipse.ACTION_UPDATE_PREF").apply {
                putExtra("key", key)
                putExtra("value", value)
            }
            context.sendBroadcast(intent)
            ps.reso.instaeclipse.utils.core.CommonUtils.broadcastToInstagram(context, intent)
        } catch (t: Throwable) {
            Log.w(TAG, "Broadcast pref change failed: ${t.message}")
        }
    }

    private fun broadcastPrefStringChange(context: Context, key: String, value: String) {
        try {
            val intent = Intent("ps.reso.instaeclipse.ACTION_UPDATE_PREF_STRING").apply {
                putExtra("key", key)
                putExtra("value", value)
            }
            context.sendBroadcast(intent)
            ps.reso.instaeclipse.utils.core.CommonUtils.broadcastToInstagram(context, intent)
        } catch (t: Throwable) {
            Log.w(TAG, "Broadcast pref string change failed: ${t.message}")
        }
    }

    private fun broadcastPrefIntChange(context: Context, key: String, value: Int) {
        try {
            val intent = Intent("ps.reso.instaeclipse.ACTION_UPDATE_PREF_INT").apply {
                putExtra("key", key)
                putExtra("value", value)
            }
            context.sendBroadcast(intent)
            ps.reso.instaeclipse.utils.core.CommonUtils.broadcastToInstagram(context, intent)
        } catch (t: Throwable) {
            Log.w(TAG, "Broadcast pref int change failed: ${t.message}")
        }
    }

    private fun syncToCompanionContentProvider(context: Context, key: String, value: Boolean) {
        if (context.packageName != "ps.reso.instaeclipse") {
            syncExecutor.execute {
                try {
                    val bundle = Bundle().apply { putBoolean("value", value) }
                    context.contentResolver.call(
                        Uri.parse("content://ps.reso.instaeclipse.preferences"),
                        "setBoolean",
                        key,
                        bundle
                    )
                } catch (ignored: Throwable) {}
            }
        }
    }

    private fun syncIntToCompanionContentProvider(context: Context, key: String, value: Int) {
        if (context.packageName != "ps.reso.instaeclipse") {
            syncExecutor.execute {
                try {
                    val bundle = Bundle().apply { putInt("value", value) }
                    context.contentResolver.call(
                        Uri.parse("content://ps.reso.instaeclipse.preferences"),
                        "setInt",
                        key,
                        bundle
                    )
                } catch (ignored: Throwable) {}
            }
        }
    }

    fun getCustomShortcuts(context: Context): List<String> {
        val raw = try {
            getPrefs(context).getString(KEY_CUSTOM_SHORTCUTS, null)
        } catch (t: Throwable) {
            null
        }
        if (raw == null || raw.isBlank() || raw == "ghost_master,enablePostDownload,enableStoryDownload,enableReelDownload,isAdBlockEnabled,isAnalyticsBlocked") {
            return emptyList()
        }
        return raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    fun setCustomShortcuts(context: Context, shortcuts: List<String>) {
        try {
            getPrefs(context).edit().putString(KEY_CUSTOM_SHORTCUTS, shortcuts.joinToString(",")).apply()
        } catch (t: Throwable) {
            Log.e(TAG, "Error writing custom shortcuts: ${t.message}")
        }
    }

    fun getGhostMasterChips(context: Context): List<String> {
        val raw = try {
            getPrefs(context).getString(KEY_GHOST_MASTER_CHIPS, "") ?: ""
        } catch (t: Throwable) {
            ""
        }
        return if (raw.isBlank()) DEFAULT_GHOST_CHIPS else raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    fun setGhostMasterChips(context: Context, chips: List<String>) {
        try {
            val editor = getPrefs(context).edit()
            editor.putString(KEY_GHOST_MASTER_CHIPS, chips.joinToString(","))
            val chipSet = chips.toSet()
            val quickToggles = mapOf(
                "quickToggleSeen" to ("isGhostSeen" in chipSet),
                "quickToggleTyping" to ("isGhostTyping" in chipSet),
                "quickToggleScreenshot" to ("isGhostScreenshot" in chipSet),
                "quickToggleViewOnce" to ("isGhostViewOnce" in chipSet),
                "quickToggleStory" to ("isGhostStory" in chipSet),
                "quickToggleLive" to ("isGhostLive" in chipSet),
                "quickToggleEphemeral" to ("keepEphemeralMessages" in chipSet),
                "quickTogglePermanentView" to ("permanentViewMode" in chipSet),
                "quickToggleAllowScreenshots" to ("allowScreenshots" in chipSet)
            )
            quickToggles.forEach { (k, v) ->
                editor.putBoolean(k, v)
                updateFieldDirectly(k, v)
                broadcastPrefChange(context, k, v)
            }
            editor.apply()
        } catch (t: Throwable) {
            Log.e(TAG, "Error writing ghost master chips: ${t.message}")
        }
    }

    fun setTestPrefs(prefs: SharedPreferences?) {
        localPrefs = prefs
        ps.reso.instaeclipse.utils.core.SettingsManager.setPrefs(prefs)
    }

    fun exportConfigJson(context: Context): String {
        loadAll(context)
        return ps.reso.instaeclipse.utils.backup.SettingsBackupManager.toJson()
    }

    fun importConfigJson(context: Context, jsonString: String): Int {
        return try {
            ps.reso.instaeclipse.utils.backup.SettingsBackupManager.fromJson(jsonString)
            val targetContext = context.applicationContext ?: context
            ps.reso.instaeclipse.utils.core.SettingsManager.init(targetContext)
            ps.reso.instaeclipse.utils.core.SettingsManager.saveAllFlags()
            loadAll(targetContext)
            onPreferenceChangedListener?.invoke()

            val restoreIntent = Intent("ps.reso.instaeclipse.ACTION_RESTORE_SETTINGS").apply {
                putExtra("json_content", jsonString)
            }
            try {
                targetContext.sendBroadcast(restoreIntent)
                ps.reso.instaeclipse.utils.core.CommonUtils.broadcastToInstagram(targetContext, restoreIntent)
            } catch (ignored: Throwable) {}

            1
        } catch (t: Throwable) {
            Log.e(TAG, "Error importing settings config: ${t.message}", t)
            0
        }
    }

    fun updateFieldDirectly(key: String, value: Boolean) {
        when (key) {
            "isDevEnabled" -> FeatureFlags.isDevEnabled = value
            "isGhostModeEnabled" -> FeatureFlags.isGhostModeEnabled = value
            "isGhostSeen" -> FeatureFlags.isGhostSeen = value
            "isGhostTyping" -> FeatureFlags.isGhostTyping = value
            "isGhostScreenshot" -> FeatureFlags.isGhostScreenshot = value
            "isGhostViewOnce" -> FeatureFlags.isGhostViewOnce = value
            "isGhostStory" -> FeatureFlags.isGhostStory = value
            "isGhostLive" -> FeatureFlags.isGhostLive = value
            "allowScreenshots" -> FeatureFlags.allowScreenshots = value
            "keepEphemeralMessages" -> FeatureFlags.keepEphemeralMessages = value
            "permanentViewMode" -> FeatureFlags.permanentViewMode = value
            "keepUnsentMessages" -> FeatureFlags.keepUnsentMessages = value
            "quickToggleSeen" -> FeatureFlags.quickToggleSeen = value
            "quickToggleTyping" -> FeatureFlags.quickToggleTyping = value
            "quickToggleScreenshot" -> FeatureFlags.quickToggleScreenshot = value
            "quickToggleViewOnce" -> FeatureFlags.quickToggleViewOnce = value
            "quickToggleStory" -> FeatureFlags.quickToggleStory = value
            "quickToggleLive" -> FeatureFlags.quickToggleLive = value
            "quickToggleEphemeral" -> FeatureFlags.quickToggleEphemeral = value
            "quickTogglePermanentView" -> FeatureFlags.quickTogglePermanentView = value
            "quickToggleAllowScreenshots" -> FeatureFlags.quickToggleAllowScreenshots = value
            "lockDirectMessages" -> FeatureFlags.lockDirectMessages = value
            "lockDirectAlways" -> FeatureFlags.lockDirectAlways = value
            "lockWholeApp" -> FeatureFlags.lockWholeApp = value
            "hideSpecificChats" -> FeatureFlags.hideSpecificChats = value
            "lockUseFingerprint" -> FeatureFlags.lockUseFingerprint = value
            "enablePostDownload" -> FeatureFlags.enablePostDownload = value
            "enableStoryDownload" -> FeatureFlags.enableStoryDownload = value
            "enableReelDownload" -> FeatureFlags.enableReelDownload = value
            "enableProfileDownload" -> FeatureFlags.enableProfileDownload = value
            "saveInstants" -> FeatureFlags.saveInstants = value
            "uploadInstants" -> FeatureFlags.uploadInstants = value
            "copyMediaLink" -> FeatureFlags.copyMediaLink = value
            "cacheStories" -> FeatureFlags.cacheStories = value
            "downloaderUsernameFolder" -> FeatureFlags.downloaderUsernameFolder = value
            "downloaderAddTimestamp" -> FeatureFlags.downloaderAddTimestamp = value
            "hideSuggestionsInFeed" -> FeatureFlags.hideSuggestionsInFeed = value
            "hideThreadsSuggestions" -> FeatureFlags.hideThreadsSuggestions = value
            "limitFollowingFeed" -> FeatureFlags.limitFollowingFeed = value
            "disableFeed" -> FeatureFlags.disableFeed = value
            "disableStories" -> FeatureFlags.disableStories = value
            "disableReels" -> FeatureFlags.disableReels = value
            "disableReelsExceptDM" -> FeatureFlags.disableReelsExceptDM = value
            "disableExplore" -> FeatureFlags.disableExplore = value
            "disableComments" -> FeatureFlags.disableComments = value
            "enablePhotoZoom" -> FeatureFlags.enablePhotoZoom = value
            "isExtremeMode" -> {
                FeatureFlags.isExtremeMode = value
                if (value) {
                    FeatureFlags.isDistractionFree = true
                }
            }
            "isDistractionFree" -> FeatureFlags.isDistractionFree = value
            "isMiscEnabled" -> FeatureFlags.isMiscEnabled = value
            "disableVideoAutoPlay" -> FeatureFlags.disableVideoAutoPlay = value
            "disableStoryFlipping" -> FeatureFlags.disableStoryFlipping = value
            "enableStoryMentions" -> FeatureFlags.enableStoryMentions = value
            "disableDoubleTapLike" -> FeatureFlags.disableDoubleTapLike = value
            "disableRepost" -> FeatureFlags.disableRepost = value
            "disableDiscoverPeople" -> FeatureFlags.disableDiscoverPeople = value
            "enableCopyComment" -> FeatureFlags.enableCopyComment = value
            "enableCaptionCopy" -> FeatureFlags.enableCaptionCopy = value
            "spoofLocation" -> FeatureFlags.spoofLocation = value
            "spoofLastSeen" -> FeatureFlags.spoofLastSeen = value
            "isAdBlockEnabled" -> FeatureFlags.isAdBlockEnabled = value
            "isAnalyticsBlocked" -> FeatureFlags.isAnalyticsBlocked = value
            "disableTrackingLinks" -> FeatureFlags.disableTrackingLinks = value
            "customThemeEnabled" -> FeatureFlags.customThemeEnabled = value
            "customFontEnabled" -> FeatureFlags.customFontEnabled = value
            "customEmojiEnabled" -> FeatureFlags.customEmojiEnabled = value
            "removeMetaAI" -> FeatureFlags.removeMetaAI = value
            "removeBuildExpiredPopup" -> FeatureFlags.removeBuildExpiredPopup = value
            "autoClearCache" -> FeatureFlags.autoClearCache = value
            "showFeatureToasts" -> FeatureFlags.showFeatureToasts = value
            "showFollowerToast" -> FeatureFlags.showFollowerToast = value
            KEY_APP_AMOLED_THEME -> isAppAmoledEnabled = value
            KEY_SHOW_APP_CONTROLS -> showAppControls = value
        }
    }

    private fun parseThemeMode(name: String?): ThemeMode = when (name?.uppercase()) {
        "LIGHT" -> ThemeMode.LIGHT
        "DARK" -> ThemeMode.DARK
        else -> ThemeMode.SYSTEM
    }
}
