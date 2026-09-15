package ps.reso.instaeclipse.core

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ps.reso.instaeclipse.utils.backup.SettingsBackupManager
import ps.reso.instaeclipse.utils.feature.FeatureFlags

class ConfigBackupRestoreTest {

    private lateinit var fakePrefs: FakeSharedPreferences
    private lateinit var fakeContext: FakeContext

    @Before
    fun setUp() {
        fakePrefs = FakeSharedPreferences()
        fakeContext = FakeContext(fakePrefs)
        PreferencesManager.setTestPrefs(fakePrefs)
        resetFlags()
    }

    @After
    fun tearDown() {
        PreferencesManager.setTestPrefs(null)
        resetFlags()
    }

    @Test
    fun exportConfigJson_producesVersion1AndSettingsObjectMatchingPreviousCommit() {
        FeatureFlags.isGhostSeen = true
        FeatureFlags.isGhostTyping = true
        FeatureFlags.removeMetaAI = true
        FeatureFlags.themePresetId = 2
        FeatureFlags.forceReelQuality = 1080
        fakePrefs.edit().putBoolean("isGhostSeen", true).commit()
        fakePrefs.edit().putString("customKey", "customValue").commit()

        val jsonString = PreferencesManager.exportConfigJson(fakeContext)
        val root = JSONObject(jsonString)

        assertTrue(root.has("version"))
        assertEquals(1, root.getInt("version"))
        assertTrue(root.has("settings"))

        val settings = root.getJSONObject("settings")
        assertEquals(true, settings.getBoolean("isGhostSeen"))
        assertEquals(true, settings.getBoolean("isGhostTyping"))
        assertEquals(true, settings.getBoolean("removeMetaAI"))
        assertEquals(2, settings.getInt("themePresetId"))
        assertEquals(1080, settings.getInt("forceReelQuality"))
    }

    @Test
    fun importConfigJson_restoresFromPreviousCommitFormat() {
        val legacyJson = """
            {
              "version": 1,
              "settings": {
                "isGhostSeen": true,
                "isGhostTyping": false,
                "removeMetaAI": true,
                "spoofLat": "37.7749",
                "spoofLng": "-122.4194",
                "forceReelQuality": 1080,
                "themePresetId": 3,
                "customFontPath": "/storage/emulated/0/font.ttf"
              }
            }
        """.trimIndent()

        val count = PreferencesManager.importConfigJson(fakeContext, legacyJson)
        assertTrue(count > 0)

        assertEquals(true, fakePrefs.getBoolean("isGhostSeen", false))
        assertEquals(false, fakePrefs.getBoolean("isGhostTyping", true))
        assertEquals(true, fakePrefs.getBoolean("removeMetaAI", false))
        assertEquals("37.7749", fakePrefs.getString("spoofLat", null))
        assertEquals("-122.4194", fakePrefs.getString("spoofLng", null))
        assertEquals(1080, fakePrefs.getInt("forceReelQuality", 0))
        assertEquals(3, fakePrefs.getInt("themePresetId", 0))
        assertEquals("/storage/emulated/0/font.ttf", fakePrefs.getString("customFontPath", null))

        assertEquals(true, FeatureFlags.isGhostSeen)
        assertEquals(false, FeatureFlags.isGhostTyping)
        assertEquals(true, FeatureFlags.removeMetaAI)
        assertEquals(37.7749, FeatureFlags.spoofLat, 0.0001)
        assertEquals(-122.4194, FeatureFlags.spoofLng, 0.0001)
        assertEquals(1080, FeatureFlags.forceReelQuality)
        assertEquals(3, FeatureFlags.themePresetId)
        assertEquals("/storage/emulated/0/font.ttf", FeatureFlags.customFontPath)

        assertTrue(fakeContext.sentBroadcasts.isNotEmpty())
    }

    @Test
    fun importConfigJson_handlesLegacyForceReelQualityBoolean() {
        val legacyJsonTrue = """
            {
              "version": 1,
              "settings": {
                "forceReelQuality": true
              }
            }
        """.trimIndent()

        PreferencesManager.importConfigJson(fakeContext, legacyJsonTrue)
        assertEquals(1080, fakePrefs.getInt("forceReelQuality", 0))
        assertEquals(1080, FeatureFlags.forceReelQuality)

        val legacyJsonFalse = """
            {
              "version": 1,
              "settings": {
                "forceReelQuality": false
              }
            }
        """.trimIndent()

        PreferencesManager.importConfigJson(fakeContext, legacyJsonFalse)
        assertEquals(0, fakePrefs.getInt("forceReelQuality", -1))
        assertEquals(0, FeatureFlags.forceReelQuality)
    }

    @Test
    fun importConfigJson_supportsFlatFormatGracefully() {
        val flatJson = """
            {
              "isGhostSeen": true,
              "removeMetaAI": true,
              "themePresetId": 4
            }
        """.trimIndent()

        val count = PreferencesManager.importConfigJson(fakeContext, flatJson)
        assertTrue(count > 0)

        assertEquals(true, fakePrefs.getBoolean("isGhostSeen", false))
        assertEquals(true, fakePrefs.getBoolean("removeMetaAI", false))
        assertEquals(4, fakePrefs.getInt("themePresetId", 0))
    }

    @Test
    fun bidirectionalCompatibility_SettingsBackupManager_and_PreferencesManager() {
        FeatureFlags.isGhostSeen = true
        FeatureFlags.autoClearCache = true
        FeatureFlags.autoClearCacheSizeMb = 250
        FeatureFlags.customThemeEnabled = true
        FeatureFlags.themePresetId = 5
        FeatureFlags.downloaderCustomPath = "/storage/emulated/0/Download/Insta"

        val sbmJson = SettingsBackupManager.toJson()

        val sbmRoot = JSONObject(sbmJson)
        assertEquals(1, sbmRoot.getInt("version"))
        assertTrue(sbmRoot.has("settings"))

        PreferencesManager.importConfigJson(fakeContext, sbmJson)

        assertEquals(true, fakePrefs.getBoolean("isGhostSeen", false))
        assertEquals(true, fakePrefs.getBoolean("autoClearCache", false))
        assertEquals(250, fakePrefs.getInt("autoClearCacheSizeMb", 0))
        assertEquals(true, fakePrefs.getBoolean("customThemeEnabled", false))
        assertEquals(5, fakePrefs.getInt("themePresetId", 0))
        assertEquals("/storage/emulated/0/Download/Insta", fakePrefs.getString("downloaderCustomPath", null))

        val pmJson = PreferencesManager.exportConfigJson(fakeContext)

        resetFlags()
        assertFalse(FeatureFlags.isGhostSeen)

        SettingsBackupManager.fromJson(pmJson)

        assertTrue(FeatureFlags.isGhostSeen)
        assertTrue(FeatureFlags.autoClearCache)
        assertEquals(250, FeatureFlags.autoClearCacheSizeMb)
        assertTrue(FeatureFlags.customThemeEnabled)
        assertEquals(5, FeatureFlags.themePresetId)
    }

    private fun resetFlags() {
        FeatureFlags.isDevEnabled = false
        FeatureFlags.isGhostModeEnabled = false
        FeatureFlags.isGhostSeen = false
        FeatureFlags.isGhostTyping = false
        FeatureFlags.isGhostScreenshot = false
        FeatureFlags.isGhostViewOnce = false
        FeatureFlags.isGhostStory = false
        FeatureFlags.isGhostLive = false
        FeatureFlags.allowScreenshots = false
        FeatureFlags.keepEphemeralMessages = false
        FeatureFlags.permanentViewMode = false
        FeatureFlags.keepUnsentMessages = false
        FeatureFlags.autoClearCache = false
        FeatureFlags.autoClearCacheSizeMb = 100
        FeatureFlags.removeMetaAI = false
        FeatureFlags.lockDirectMessages = false
        FeatureFlags.lockDirectPasscode = ""
        FeatureFlags.lockDirectSalt = ""
        FeatureFlags.lockDirectAlways = false
        FeatureFlags.lockWholeApp = false
        FeatureFlags.lockUseFingerprint = true
        FeatureFlags.hideSpecificChats = false
        FeatureFlags.quickToggleSeen = false
        FeatureFlags.quickToggleTyping = false
        FeatureFlags.quickToggleScreenshot = false
        FeatureFlags.quickToggleViewOnce = false
        FeatureFlags.quickToggleStory = false
        FeatureFlags.quickToggleLive = false
        FeatureFlags.quickToggleEphemeral = false
        FeatureFlags.quickTogglePermanentView = false
        FeatureFlags.quickToggleAllowScreenshots = false
        FeatureFlags.hideSuggestionsInFeed = false
        FeatureFlags.hideThreadsSuggestions = false
        FeatureFlags.limitFollowingFeed = false
        FeatureFlags.disableStories = false
        FeatureFlags.disableFeed = false
        FeatureFlags.disableExplore = false
        FeatureFlags.disableComments = false
        FeatureFlags.disableReels = false
        FeatureFlags.disableReelsExceptDM = false
        FeatureFlags.isExtremeMode = false
        FeatureFlags.isDistractionFree = false
        FeatureFlags.isMiscEnabled = false
        FeatureFlags.disableVideoAutoPlay = false
        FeatureFlags.disableStoryFlipping = false
        FeatureFlags.enableStoryMentions = false
        FeatureFlags.disableDoubleTapLike = false
        FeatureFlags.disableRepost = false
        FeatureFlags.disableDiscoverPeople = false
        FeatureFlags.enableCopyComment = false
        FeatureFlags.enableCaptionCopy = false
        FeatureFlags.spoofLocation = false
        FeatureFlags.spoofLat = 0.0
        FeatureFlags.spoofLng = 0.0
        FeatureFlags.spoofLastSeen = false
        FeatureFlags.isAdBlockEnabled = false
        FeatureFlags.isAnalyticsBlocked = false
        FeatureFlags.disableTrackingLinks = false
        FeatureFlags.forceReelQuality = 0
        FeatureFlags.enablePhotoZoom = false
        FeatureFlags.removeBuildExpiredPopup = false
        FeatureFlags.enablePostDownload = false
        FeatureFlags.enableStoryDownload = false
        FeatureFlags.enableReelDownload = false
        FeatureFlags.enableProfileDownload = false
        FeatureFlags.downloaderUsernameFolder = false
        FeatureFlags.downloaderAddTimestamp = false
        FeatureFlags.copyMediaLink = false
        FeatureFlags.saveInstants = false
        FeatureFlags.uploadInstants = false
        FeatureFlags.cacheStories = false
        FeatureFlags.customThemeEnabled = false
        FeatureFlags.themePresetId = 1
        FeatureFlags.themePaletteJson = ""
        FeatureFlags.customFontEnabled = false
        FeatureFlags.customFontPath = ""
        FeatureFlags.customEmojiEnabled = false
        FeatureFlags.customEmojiPath = ""
        FeatureFlags.downloaderCustomPath = ""
        FeatureFlags.downloaderCustomUri = ""
        FeatureFlags.showFeatureToasts = true
        FeatureFlags.showFollowerToast = true
    }

    class FakeContext(private val prefs: FakeSharedPreferences) : ContextWrapper(null) {
        val sentBroadcasts = mutableListOf<Intent>()

        override fun getApplicationContext(): Context = this
        override fun getPackageName(): String = "ps.reso.instaeclipse"
        override fun getSharedPreferences(name: String?, mode: Int): SharedPreferences = prefs
        override fun getPackageManager(): android.content.pm.PackageManager? = null
        override fun sendBroadcast(intent: Intent?) {
            if (intent != null) {
                sentBroadcasts.add(intent)
            }
        }
    }

    class FakeSharedPreferences : SharedPreferences {
        private val map = mutableMapOf<String, Any?>()

        override fun getAll(): Map<String, *> = HashMap(map)

        override fun getString(key: String?, defValue: String?): String? =
            map[key]?.toString() ?: defValue

        @Suppress("UNCHECKED_CAST")
        override fun getStringSet(key: String?, defValues: Set<String>?): Set<String>? =
            (map[key] as? Set<String>) ?: defValues

        override fun getInt(key: String?, defValue: Int): Int =
            (map[key] as? Number)?.toInt() ?: defValue

        override fun getLong(key: String?, defValue: Long): Long =
            (map[key] as? Number)?.toLong() ?: defValue

        override fun getFloat(key: String?, defValue: Float): Float =
            (map[key] as? Number)?.toFloat() ?: defValue

        override fun getBoolean(key: String?, defValue: Boolean): Boolean =
            (map[key] as? Boolean) ?: defValue

        override fun contains(key: String?): Boolean = map.containsKey(key)

        override fun edit(): SharedPreferences.Editor = FakeEditor(map)

        override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}

        override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}

        class FakeEditor(private val sharedMap: MutableMap<String, Any?>) : SharedPreferences.Editor {
            private val tempMap = mutableMapOf<String, Any?>()
            private val removedKeys = mutableSetOf<String>()
            private var clearFlag = false

            override fun putString(key: String?, value: String?): SharedPreferences.Editor {
                if (key != null) tempMap[key] = value
                return this
            }

            override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor {
                if (key != null) tempMap[key] = values
                return this
            }

            override fun putInt(key: String?, value: Int): SharedPreferences.Editor {
                if (key != null) tempMap[key] = value
                return this
            }

            override fun putLong(key: String?, value: Long): SharedPreferences.Editor {
                if (key != null) tempMap[key] = value
                return this
            }

            override fun putFloat(key: String?, value: Float): SharedPreferences.Editor {
                if (key != null) tempMap[key] = value
                return this
            }

            override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor {
                if (key != null) tempMap[key] = value
                return this
            }

            override fun remove(key: String?): SharedPreferences.Editor {
                if (key != null) removedKeys.add(key)
                return this
            }

            override fun clear(): SharedPreferences.Editor {
                clearFlag = true
                return this
            }

            override fun commit(): Boolean {
                apply()
                return true
            }

            override fun apply() {
                if (clearFlag) {
                    sharedMap.clear()
                }
                for (k in removedKeys) {
                    sharedMap.remove(k)
                }
                sharedMap.putAll(tempMap)
            }
        }
    }
}
