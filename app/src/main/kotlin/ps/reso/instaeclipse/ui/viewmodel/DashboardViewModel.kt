package ps.reso.instaeclipse.ui.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ps.reso.instaeclipse.R
import ps.reso.instaeclipse.core.HookRegistry
import ps.reso.instaeclipse.core.ModuleStatus
import ps.reso.instaeclipse.core.PreferencesManager
import ps.reso.instaeclipse.utils.AppUtils
import ps.reso.instaeclipse.utils.core.CommonUtils
import ps.reso.instaeclipse.utils.log.Logging

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _effects = Channel<DashboardUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private val mainHandler = Handler(Looper.getMainLooper())

    private val logReplyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if ("ps.reso.instaeclipse.ACTION_UPDATE_PREF_INT" == action) {
                val key = intent.getStringExtra("key")
                val value = intent.getIntExtra("value", 0)
                if (key != null) {
                    PreferencesManager.setInt(context, key, value, notifyIpc = false)
                    if (key == "forceReelQuality") {
                        _uiState.update { it.copy(forceReelQuality = value) }
                    }
                }
                refreshState()
            } else if ("ps.reso.instaeclipse.ACTION_UPDATE_PREF" == action) {
                val key = intent.getStringExtra("key")
                if (key != null) {
                    val value = intent.getBooleanExtra("value", false)
                    PreferencesManager.setBoolean(context, key, value, notifyIpc = false)
                }
                refreshState()
            } else if ("ps.reso.instaeclipse.ACTION_UPDATE_PREF_STRING" == action) {
                val key = intent.getStringExtra("key")
                val value = intent.getStringExtra("value")
                if (key != null && value != null) {
                    PreferencesManager.setString(context, key, value, notifyIpc = false)
                }
                refreshState()
            } else if ("ps.reso.instaeclipse.ACTION_SEND_PREFS" == action) {
                val extras = intent.extras
                if (extras != null) {
                    val editor = PreferencesManager.getPrefs(context).edit()
                    for (key in extras.keySet()) {
                        when (val v = extras.get(key)) {
                            is Boolean -> {
                                editor.putBoolean(key, v)
                                PreferencesManager.updateFieldDirectly(key, v)
                            }
                            is Int -> editor.putInt(key, v)
                            is Long -> editor.putLong(key, v)
                            is Float -> editor.putFloat(key, v)
                            is String -> editor.putString(key, v)
                        }
                    }
                    editor.apply()
                    PreferencesManager.loadAll(context)
                }
                refreshState()
            } else if (CommonUtils.ACTION_LOGS_REPLY == action) {
                val logText = intent.getStringExtra(CommonUtils.EXTRA_LOG_TEXT) ?: ""
                val error = intent.getStringExtra(CommonUtils.EXTRA_LOG_ERROR)
                val source = intent.getStringExtra(CommonUtils.EXTRA_LOG_SOURCE) ?: "Instagram"

                val companionLogs = Logging.getSnapshotForIpc()
                val combined = buildString {
                    append("=== Instagram Module Logs [$source] ===\n")
                    if (!error.isNullOrEmpty()) {
                        append("⚠️ $error\n")
                    } else if (logText.isBlank()) {
                        append("(No logs recorded in Instagram process yet)\n")
                    } else {
                        append(logText.trim()).append("\n")
                    }
                    append("\n=== Companion App Logs ===\n")
                    if (companionLogs.isBlank()) {
                        append("(Companion log buffer clean)\n")
                    } else {
                        append(companionLogs.trim()).append("\n")
                    }
                }
                val lineCount = combined.lines().size
                _uiState.update { it.copy(logContent = combined, logLineCount = lineCount, isLogLoading = false) }
            }
        }
    }

    init {
        PreferencesManager.init(application)
        PreferencesManager.loadAll(application)
        ModuleStatus.checkStatus()

        registerLogReceiver()
        detectInstagramStatus()
        refreshState()
        requestPrefsFromInstagram()

        PreferencesManager.onPreferenceChangedListener = {
            viewModelScope.launch {
                PreferencesManager.loadAll(getApplication())
                refreshState()
            }
        }
    }

    private fun registerLogReceiver() {
        val filter = IntentFilter().apply {
            addAction(CommonUtils.ACTION_LOGS_REPLY)
            addAction("ps.reso.instaeclipse.ACTION_UPDATE_PREF")
            addAction("ps.reso.instaeclipse.ACTION_UPDATE_PREF_INT")
            addAction("ps.reso.instaeclipse.ACTION_UPDATE_PREF_STRING")
            addAction("ps.reso.instaeclipse.ACTION_SEND_PREFS")
        }
        val app = getApplication<Application>()
        if (Build.VERSION.SDK_INT >= 33) {
            app.registerReceiver(logReplyReceiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            ContextCompat.registerReceiver(app, logReplyReceiver, filter, ContextCompat.RECEIVER_EXPORTED)
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            getApplication<Application>().unregisterReceiver(logReplyReceiver)
        } catch (ignored: Throwable) {}
    }

    fun requestPrefsFromInstagram() {
        val app = getApplication<Application>()
        val intent = Intent("ps.reso.instaeclipse.ACTION_REQUEST_PREFS")
        CommonUtils.broadcastToInstagram(app, intent)
        app.sendBroadcast(intent)
    }

    fun refreshStatus() {
        ModuleStatus.checkStatus()
        PreferencesManager.loadAll(getApplication())
        detectInstagramStatus()
        refreshState()
        requestPrefsFromInstagram()
    }

    fun refreshState() {
        val app = getApplication<Application>()
        val allHooks = HookRegistry.getAllHooks()
        val enabledHooks = allHooks.filter { PreferencesManager.isHookEnabled(app, it.id) }.map { it.id }.toSet()
        val currentForceReelQuality = PreferencesManager.getForceReelQuality(app)

        _uiState.update { current ->
            current.copy(
                isModuleActive = ModuleStatus.isModuleActive.value,
                detectedFramework = ModuleStatus.frameworkName.value,
                themeMode = PreferencesManager.appThemeMode,
                isAppAmoledEnabled = PreferencesManager.isAppAmoledEnabled,
                showAppControls = PreferencesManager.showAppControls,
                downloadSafUri = PreferencesManager.downloadSafUri,
                enabledHookIds = enabledHooks,
                customShortcutIds = PreferencesManager.getCustomShortcuts(app),
                ghostMasterChipIds = PreferencesManager.getGhostMasterChips(app),
                allHooks = allHooks,
                forceReelQuality = currentForceReelQuality
            )
        }
    }

    fun detectInstagramStatus() {
        val pm = getApplication<Application>().packageManager
        val installed = mutableListOf<InstalledInstagramInfo>()

        for (pkg in CommonUtils.SUPPORTED_PACKAGES) {
            try {
                val info = pm.getPackageInfo(pkg, 0)
                val ver = info.versionName ?: "Unknown"
                val label = CommonUtils.getVariantLabel(pkg)
                installed.add(InstalledInstagramInfo(pkg, ver, label))
            } catch (ignored: PackageManager.NameNotFoundException) {}
        }

        val active = installed.firstOrNull { it.packageName == CommonUtils.IG_PACKAGE_NAME }
            ?: installed.firstOrNull()

        _uiState.update {
            it.copy(
                installedInstagramVariants = installed,
                activeInstagram = active
            )
        }
    }

    fun selectActiveInstagramVariant(pkg: String) {
        val target = _uiState.value.installedInstagramVariants.firstOrNull { it.packageName == pkg }
        if (target != null) {
            _uiState.update { it.copy(activeInstagram = target, showVariantDialog = false) }
        }
    }

    fun setShowVariantDialog(show: Boolean) {
        _uiState.update { it.copy(showVariantDialog = show) }
    }

    fun launchInstagram() {
        val pkg = _uiState.value.activeInstagram?.packageName ?: CommonUtils.IG_PACKAGE_NAME
        val app = getApplication<Application>()
        if (!AppUtils.openInstagram(app, pkg)) {
            notifyEffect(DashboardUiEffect.ShowToast("Instagram is not installed"))
        }
    }

    fun openInstagramAppDetails() {
        val pkg = _uiState.value.activeInstagram?.packageName ?: CommonUtils.IG_PACKAGE_NAME
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", pkg, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        getApplication<Application>().startActivity(intent)
    }

    fun forceStopInstagram() {
        val pkg = _uiState.value.activeInstagram?.packageName ?: CommonUtils.IG_PACKAGE_NAME
        val app = getApplication<Application>()
        viewModelScope.launch {
            val stopped = AppUtils.forceStopInstagram(app, pkg)
            val msg = if (stopped) {
                app.getString(R.string.msg_instagram_force_stopped)
            } else {
                app.getString(R.string.msg_opened_instagram_settings)
            }
            notifyEffect(DashboardUiEffect.ShowSnackbar(msg))
        }
    }

    fun setHookEnabled(id: String, value: Boolean) {
        if (id == "forceReelQuality") return
        val app = getApplication<Application>()
        PreferencesManager.setBoolean(app, id, value)

        if (id == "isExtremeMode" && value) {
            PreferencesManager.setBoolean(app, "isDistractionFree", true)
        } else if (id == "disableReels" && !value) {
            PreferencesManager.setBoolean(app, "disableReelsExceptDM", false)
        } else if (id == "disableReelsExceptDM" && value) {
            PreferencesManager.setBoolean(app, "disableReels", true)
        }

        refreshState()
    }

    fun setForceReelQuality(value: Int) {
        val app = getApplication<Application>()
        PreferencesManager.setInt(app, "forceReelQuality", value)
        _uiState.update { it.copy(forceReelQuality = value) }
    }

    fun setAllHooksEnabled(ids: List<String>, value: Boolean) {
        val app = getApplication<Application>()
        ids.forEach { id ->
            PreferencesManager.setBoolean(app, id, value, notifyIpc = false)
        }
        PreferencesManager.setBoolean(app, ids.firstOrNull() ?: "", value, notifyIpc = true)

        _uiState.update { current ->
            val updated = if (value) current.enabledHookIds + ids else current.enabledHookIds - ids.toSet()
            current.copy(enabledHookIds = updated)
        }
    }

    fun toggleShortcutSelection(id: String) {
        val app = getApplication<Application>()
        val current = _uiState.value.customShortcutIds.toMutableList()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        PreferencesManager.setCustomShortcuts(app, current)
        _uiState.update { it.copy(customShortcutIds = current) }
    }

    fun toggleGhostChipSelection(id: String) {
        val app = getApplication<Application>()
        val current = _uiState.value.ghostMasterChipIds.toMutableList()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        PreferencesManager.setGhostMasterChips(app, current)
        _uiState.update { it.copy(ghostMasterChipIds = current) }
    }

    fun setDownloadSafUri(uri: String) {
        val app = getApplication<Application>()
        PreferencesManager.setString(app, PreferencesManager.KEY_DOWNLOAD_SAF_URI, uri)
        _uiState.update { it.copy(downloadSafUri = uri) }
        notifyEffect(DashboardUiEffect.ShowToast("Download folder updated"))
    }

    fun setThemeMode(mode: PreferencesManager.ThemeMode) {
        val app = getApplication<Application>()
        PreferencesManager.setString(app, PreferencesManager.KEY_APP_THEME_MODE, mode.name)
        PreferencesManager.appThemeMode = mode
        _uiState.update { it.copy(themeMode = mode) }
    }

    fun setAppAmoled(enabled: Boolean) {
        val app = getApplication<Application>()
        PreferencesManager.setBoolean(app, PreferencesManager.KEY_APP_AMOLED_THEME, enabled)
        PreferencesManager.isAppAmoledEnabled = enabled
        _uiState.update { it.copy(isAppAmoledEnabled = enabled) }
    }

    fun setShowAppControls(enabled: Boolean) {
        val app = getApplication<Application>()
        PreferencesManager.setBoolean(app, PreferencesManager.KEY_SHOW_APP_CONTROLS, enabled)
        PreferencesManager.showAppControls = enabled
        _uiState.update { it.copy(showAppControls = enabled) }
    }

    fun selectDestination(destination: AppNavDestination) {
        _uiState.update { it.copy(currentDestination = destination, showSettings = false, showLogs = false, showFaq = false, settingsSubpage = null) }
    }

    fun showSettingsScreen() {
        _uiState.update { it.copy(showSettings = true, showLogs = false, showFaq = false, settingsSubpage = null) }
    }

    fun showLogsScreen() {
        _uiState.update { it.copy(showLogs = true, showSettings = false, showFaq = false) }
        fetchLogs()
    }

    fun showFaqScreen() {
        _uiState.update { it.copy(showFaq = true, showSettings = false, showLogs = false, settingsSubpage = null) }
    }

    fun selectSettingsSubpage(subpage: SettingsSubpage?) {
        _uiState.update { it.copy(settingsSubpage = subpage) }
    }

    fun handleBack(): Boolean {
        val current = _uiState.value
        if (current.isSearchActive) {
            _uiState.update { it.copy(isSearchActive = false, searchQuery = "") }
            return true
        }
        if (current.settingsSubpage != null) {
            _uiState.update { it.copy(settingsSubpage = null) }
            return true
        }
        if (current.showSettings) {
            _uiState.update { it.copy(showSettings = false) }
            return true
        }
        if (current.showLogs) {
            _uiState.update { it.copy(showLogs = false) }
            return true
        }
        if (current.showFaq) {
            _uiState.update { it.copy(showFaq = false) }
            return true
        }
        if (current.currentDestination != AppNavDestination.HOME) {
            _uiState.update { it.copy(currentDestination = AppNavDestination.HOME) }
            return true
        }
        return false
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setSearchActive(active: Boolean) {
        _uiState.update { it.copy(isSearchActive = active, searchQuery = if (!active) "" else it.searchQuery) }
    }

    fun fetchLogs() {
        _uiState.update { it.copy(isLogLoading = true) }
        val app = getApplication<Application>()
        try {
            val req = Intent(CommonUtils.ACTION_REQUEST_LOGS).apply {
                setPackage(_uiState.value.activeInstagram?.packageName ?: CommonUtils.IG_PACKAGE_NAME)
            }
            CommonUtils.broadcastToInstagram(app, req)
        } catch (ignored: Throwable) {}

        mainHandler.postDelayed({
            if (_uiState.value.isLogLoading) {
                val companion = Logging.getSnapshotForIpc()
                val fallback = "=== Companion App Logs ===\n" + (if (companion.isBlank()) "(No companion logs)" else companion)
                _uiState.update { it.copy(logContent = fallback, logLineCount = fallback.lines().size, isLogLoading = false) }
            }
        }, 1200)
    }

    fun refreshLogs() = fetchLogs()
    fun openInstagram() = launchInstagram()
    fun openInstagramAppInfo() = openInstagramAppDetails()
    fun toggleCustomShortcut(id: String) = toggleShortcutSelection(id)
    fun toggleGhostMasterChip(id: String) = toggleGhostChipSelection(id)
    fun setAppControlsEnabled(enabled: Boolean) = setShowAppControls(enabled)
    fun setActiveInstagramPackage(pkg: String) = selectActiveInstagramVariant(pkg)

    fun clearLogs() {
        val app = getApplication<Application>()
        Logging.clear()
        try {
            app.sendBroadcast(Intent(CommonUtils.ACTION_CLEAR_LOGS))
        } catch (ignored: Throwable) {}
        _uiState.update { it.copy(logContent = "", logLineCount = 0) }
        notifyEffect(DashboardUiEffect.ShowToast("Logs cleared"))
    }

    fun setLogSearchQuery(query: String) {
        _uiState.update { it.copy(logSearchQuery = query) }
    }

    fun exportConfigJson(): String = PreferencesManager.exportConfigJson(getApplication())

    fun importConfigJson(json: String) {
        try {
            val count = PreferencesManager.importConfigJson(getApplication(), json)
            refreshState()
            notifyEffect(DashboardUiEffect.ShowToast("Successfully imported $count settings"))
        } catch (t: Throwable) {
            notifyEffect(DashboardUiEffect.ShowToast("Import failed: ${t.message}"))
        }
    }

    fun notifyEffect(effect: DashboardUiEffect) {
        viewModelScope.launch {
            _effects.send(effect)
        }
    }
}
