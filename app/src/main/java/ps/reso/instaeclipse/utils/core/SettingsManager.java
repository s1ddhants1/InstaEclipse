package ps.reso.instaeclipse.utils.core;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ps.reso.instaeclipse.utils.feature.FeatureFlags;
import ps.reso.instaeclipse.utils.feature.FeatureManager;

public class SettingsManager {
    private static final String PREF_NAME = "instaeclipse_prefs";
    private static SharedPreferences prefs;
    private static Context appContext;

    private static final ExecutorService syncExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "InstaEclipse-SettingsSync");
        t.setDaemon(true);
        return t;
    });

    public static void init(Context context) {
        if (context != null) {
            Context app = context.getApplicationContext();
            if (app != null) {
                appContext = app;
            } else if (appContext == null) {
                appContext = context;
            }
        }
        if (prefs == null && context != null) {
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    public static void setPrefs(SharedPreferences customPrefs) {
        prefs = customPrefs;
    }

    public static void saveAllFlags() {
        if (prefs == null && appContext != null) {
            prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        if (prefs == null) return;
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("isDevEnabled", FeatureFlags.isDevEnabled);

        // Ghost Mode
        editor.putBoolean("isGhostModeEnabled", FeatureFlags.isGhostModeEnabled);
        editor.putBoolean("isGhostSeen", FeatureFlags.isGhostSeen);
        editor.putBoolean("isGhostTyping", FeatureFlags.isGhostTyping);
        editor.putBoolean("isGhostScreenshot", FeatureFlags.isGhostScreenshot);
        editor.putBoolean("isGhostViewOnce", FeatureFlags.isGhostViewOnce);
        editor.putBoolean("isGhostStory", FeatureFlags.isGhostStory);
        editor.putBoolean("isGhostLive", FeatureFlags.isGhostLive);
        editor.putBoolean("allowScreenshots", FeatureFlags.allowScreenshots);
        editor.putBoolean("keepEphemeralMessages", FeatureFlags.keepEphemeralMessages);
        editor.putBoolean("permanentViewMode", FeatureFlags.permanentViewMode);
        editor.putBoolean("keepUnsentMessages", FeatureFlags.keepUnsentMessages);
        editor.putBoolean("autoClearCache", FeatureFlags.autoClearCache);
        editor.putInt("autoClearCacheSizeMb", FeatureFlags.autoClearCacheSizeMb);
        editor.putBoolean("removeMetaAI", FeatureFlags.removeMetaAI);
        editor.putBoolean("lockDirectMessages", FeatureFlags.lockDirectMessages);
        editor.putString("lockDirectPasscode", FeatureFlags.lockDirectPasscode);
        editor.putString("lockDirectSalt", FeatureFlags.lockDirectSalt);
        editor.putBoolean("lockDirectAlways", FeatureFlags.lockDirectAlways);
        editor.putBoolean("lockWholeApp", FeatureFlags.lockWholeApp);
        editor.putBoolean("hideSpecificChats", FeatureFlags.hideSpecificChats);
        editor.putBoolean("lockUseFingerprint", FeatureFlags.lockUseFingerprint);

        // Quick Toggles
        editor.putBoolean("quickToggleSeen", FeatureFlags.quickToggleSeen);
        editor.putBoolean("quickToggleTyping", FeatureFlags.quickToggleTyping);
        editor.putBoolean("quickToggleScreenshot", FeatureFlags.quickToggleScreenshot);
        editor.putBoolean("quickToggleViewOnce", FeatureFlags.quickToggleViewOnce);
        editor.putBoolean("quickToggleStory", FeatureFlags.quickToggleStory);
        editor.putBoolean("quickToggleLive", FeatureFlags.quickToggleLive);
        editor.putBoolean("quickToggleEphemeral", FeatureFlags.quickToggleEphemeral);
        editor.putBoolean("quickTogglePermanentView", FeatureFlags.quickTogglePermanentView);
        editor.putBoolean("quickToggleAllowScreenshots", FeatureFlags.quickToggleAllowScreenshots);

        // Distraction Free
        editor.putBoolean("isExtremeMode", FeatureFlags.isExtremeMode);
        editor.putBoolean("isDistractionFree", FeatureFlags.isDistractionFree);
        editor.putBoolean("disableStories", FeatureFlags.disableStories);
        editor.putBoolean("disableFeed", FeatureFlags.disableFeed);
        editor.putBoolean("disableReels", FeatureFlags.disableReels);
        editor.putBoolean("disableReelsExceptDM", FeatureFlags.disableReelsExceptDM);
        editor.putBoolean("disableExplore", FeatureFlags.disableExplore);
        editor.putBoolean("disableComments", FeatureFlags.disableComments);

        // Clean Feed
        editor.putBoolean("hideSuggestionsInFeed", FeatureFlags.hideSuggestionsInFeed);
        editor.putBoolean("hideThreadsSuggestions", FeatureFlags.hideThreadsSuggestions);
        editor.putBoolean("limitFollowingFeed", FeatureFlags.limitFollowingFeed);

        // Ads
        editor.putBoolean("isAdBlockEnabled", FeatureFlags.isAdBlockEnabled);
        editor.putBoolean("isAnalyticsBlocked", FeatureFlags.isAnalyticsBlocked);
        editor.putBoolean("disableTrackingLinks", FeatureFlags.disableTrackingLinks);

        // Misc
        editor.putBoolean("isMiscEnabled", FeatureFlags.isMiscEnabled);
        editor.putBoolean("disableStoryFlipping", FeatureFlags.disableStoryFlipping);
        editor.putBoolean("disableVideoAutoPlay", FeatureFlags.disableVideoAutoPlay);
        editor.putBoolean("spoofLastSeen", FeatureFlags.spoofLastSeen);
        editor.putBoolean("spoofLocation", FeatureFlags.spoofLocation);
        editor.putString("spoofLat", String.valueOf(FeatureFlags.spoofLat));
        editor.putString("spoofLng", String.valueOf(FeatureFlags.spoofLng));
        editor.putInt("forceReelQuality", FeatureFlags.forceReelQuality);
        editor.putBoolean("disableRepost", FeatureFlags.disableRepost);
        editor.putBoolean("showFollowerToast", FeatureFlags.showFollowerToast);
        editor.putBoolean("showFeatureToasts", FeatureFlags.showFeatureToasts);
        editor.putBoolean("enableStoryMentions", FeatureFlags.enableStoryMentions);
        editor.putBoolean("disableDiscoverPeople", FeatureFlags.disableDiscoverPeople);
        editor.putBoolean("removeBuildExpiredPopup", FeatureFlags.removeBuildExpiredPopup);
        editor.putBoolean("enableCopyComment", FeatureFlags.enableCopyComment);
        editor.putBoolean("enableCaptionCopy", FeatureFlags.enableCaptionCopy);
        editor.putBoolean("disableDoubleTapLike", FeatureFlags.disableDoubleTapLike);
        editor.putBoolean("enablePhotoZoom", FeatureFlags.enablePhotoZoom);
        editor.putBoolean("enablePostDownload", FeatureFlags.enablePostDownload);
        editor.putBoolean("enableStoryDownload", FeatureFlags.enableStoryDownload);
        editor.putBoolean("enableReelDownload", FeatureFlags.enableReelDownload);
        editor.putBoolean("enableProfileDownload", FeatureFlags.enableProfileDownload);
        editor.putBoolean("downloaderUsernameFolder", FeatureFlags.downloaderUsernameFolder);
        editor.putBoolean("downloaderAddTimestamp", FeatureFlags.downloaderAddTimestamp);
        editor.putBoolean("copyMediaLink", FeatureFlags.copyMediaLink);
        editor.putBoolean("saveInstants", FeatureFlags.saveInstants);
        editor.putBoolean("uploadInstants", FeatureFlags.uploadInstants);
        editor.putBoolean("cacheStories", FeatureFlags.cacheStories);
        editor.putBoolean("customFontEnabled", FeatureFlags.customFontEnabled);
        editor.putString("customFontPath", FeatureFlags.customFontPath);
        editor.putBoolean("customEmojiEnabled", FeatureFlags.customEmojiEnabled);
        editor.putString("customEmojiPath", FeatureFlags.customEmojiPath);
        editor.putString("downloaderCustomPath", FeatureFlags.downloaderCustomPath);
        editor.putString("downloaderCustomUri",  FeatureFlags.downloaderCustomUri);

        // Custom Theme
        editor.putBoolean("customThemeEnabled", FeatureFlags.customThemeEnabled);
        editor.putInt("themePresetId", FeatureFlags.themePresetId);
        editor.putString("themePaletteJson", FeatureFlags.themePaletteJson);

        editor.apply();

        FeatureManager.refreshFeatureStatus();

        if (CommonUtils.isCompanionAppInstalled(appContext)) {
            syncToCompanionApp();
        }
    }

    public static void loadAllFlags(Context context) {
        init(context);
        if (prefs == null && context != null) {
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }

        FeatureFlags.isDevEnabled = prefs.getBoolean("isDevEnabled", false);

        // Ghost Mode
        FeatureFlags.isGhostModeEnabled = prefs.getBoolean("isGhostModeEnabled", false);
        FeatureFlags.isGhostSeen = prefs.getBoolean("isGhostSeen", false);
        FeatureFlags.isGhostTyping = prefs.getBoolean("isGhostTyping", false);
        FeatureFlags.isGhostScreenshot = prefs.getBoolean("isGhostScreenshot", false);
        FeatureFlags.isGhostViewOnce = prefs.getBoolean("isGhostViewOnce", false);
        FeatureFlags.isGhostStory = prefs.getBoolean("isGhostStory", false);
        FeatureFlags.isGhostLive = prefs.getBoolean("isGhostLive", false);
        FeatureFlags.allowScreenshots = prefs.getBoolean("allowScreenshots", false);
        FeatureFlags.keepEphemeralMessages = prefs.getBoolean("keepEphemeralMessages", false);
        FeatureFlags.permanentViewMode = prefs.getBoolean("permanentViewMode", false);
        FeatureFlags.keepUnsentMessages = prefs.getBoolean("keepUnsentMessages", false);
        FeatureFlags.autoClearCache = prefs.getBoolean("autoClearCache", false);
        FeatureFlags.autoClearCacheSizeMb = prefs.getInt("autoClearCacheSizeMb", 500);
        FeatureFlags.removeMetaAI = prefs.getBoolean("removeMetaAI", false);
        FeatureFlags.lockDirectMessages = prefs.getBoolean("lockDirectMessages", false);
        FeatureFlags.lockDirectPasscode = prefs.getString("lockDirectPasscode", "");
        FeatureFlags.lockDirectSalt = prefs.getString("lockDirectSalt", "");
        FeatureFlags.lockDirectAlways = prefs.getBoolean("lockDirectAlways", false);
        FeatureFlags.lockWholeApp = prefs.getBoolean("lockWholeApp", false);
        FeatureFlags.hideSpecificChats = prefs.getBoolean("hideSpecificChats", false);
        FeatureFlags.lockUseFingerprint = prefs.getBoolean("lockUseFingerprint", true);

        // Quick Toggles
        FeatureFlags.quickToggleSeen = prefs.getBoolean("quickToggleSeen", false);
        FeatureFlags.quickToggleTyping = prefs.getBoolean("quickToggleTyping", false);
        FeatureFlags.quickToggleScreenshot = prefs.getBoolean("quickToggleScreenshot", false);
        FeatureFlags.quickToggleViewOnce = prefs.getBoolean("quickToggleViewOnce", false);
        FeatureFlags.quickToggleStory = prefs.getBoolean("quickToggleStory", false);
        FeatureFlags.quickToggleLive = prefs.getBoolean("quickToggleLive", false);
        FeatureFlags.quickToggleEphemeral = prefs.getBoolean("quickToggleEphemeral", false);
        FeatureFlags.quickTogglePermanentView = prefs.getBoolean("quickTogglePermanentView", false);
        FeatureFlags.quickToggleAllowScreenshots = prefs.getBoolean("quickToggleAllowScreenshots", false);

        // Distraction Free
        FeatureFlags.isExtremeMode = prefs.getBoolean("isExtremeMode", false);
        FeatureFlags.isDistractionFree = prefs.getBoolean("isDistractionFree", false);
        FeatureFlags.disableStories = prefs.getBoolean("disableStories", false);
        FeatureFlags.disableFeed = prefs.getBoolean("disableFeed", false);
        FeatureFlags.disableReels = prefs.getBoolean("disableReels", false);
        FeatureFlags.disableReelsExceptDM = prefs.getBoolean("disableReelsExceptDM", false);
        FeatureFlags.disableExplore = prefs.getBoolean("disableExplore", false);
        FeatureFlags.disableComments = prefs.getBoolean("disableComments", false);

        // Clean Feed
        FeatureFlags.hideSuggestionsInFeed = prefs.getBoolean("hideSuggestionsInFeed", false);
        FeatureFlags.hideThreadsSuggestions = prefs.getBoolean("hideThreadsSuggestions", false);
        FeatureFlags.limitFollowingFeed = prefs.getBoolean("limitFollowingFeed", false);

        // Ads
        FeatureFlags.isAdBlockEnabled = prefs.getBoolean("isAdBlockEnabled", false);
        FeatureFlags.isAnalyticsBlocked = prefs.getBoolean("isAnalyticsBlocked", false);
        FeatureFlags.disableTrackingLinks = prefs.getBoolean("disableTrackingLinks", false);

        // Misc
        FeatureFlags.isMiscEnabled = prefs.getBoolean("isMiscEnabled", false);
        FeatureFlags.disableStoryFlipping = prefs.getBoolean("disableStoryFlipping", false);
        FeatureFlags.disableVideoAutoPlay = prefs.getBoolean("disableVideoAutoPlay", false);
        FeatureFlags.spoofLastSeen = prefs.getBoolean("spoofLastSeen", false);
        FeatureFlags.spoofLocation = prefs.getBoolean("spoofLocation", false);
        FeatureFlags.spoofLat = readDoublePref(prefs, "spoofLat", 0.0);
        FeatureFlags.spoofLng = readDoublePref(prefs, "spoofLng", 0.0);
        FeatureFlags.forceReelQuality = readIntCompat(prefs, "forceReelQuality", 0);
        FeatureFlags.disableRepost = prefs.getBoolean("disableRepost", false);
        FeatureFlags.showFollowerToast = prefs.getBoolean("showFollowerToast", false);
        FeatureFlags.showFeatureToasts = prefs.getBoolean("showFeatureToasts", false);
        FeatureFlags.enableStoryMentions = prefs.getBoolean("enableStoryMentions", false);
        FeatureFlags.disableDiscoverPeople = prefs.getBoolean("disableDiscoverPeople", false);
        FeatureFlags.removeBuildExpiredPopup = prefs.getBoolean("removeBuildExpiredPopup", false);
        FeatureFlags.enableCopyComment = prefs.getBoolean("enableCopyComment", false);
        FeatureFlags.enableCaptionCopy = prefs.getBoolean("enableCaptionCopy", false);
        FeatureFlags.disableDoubleTapLike = prefs.getBoolean("disableDoubleTapLike", false);
        FeatureFlags.enablePhotoZoom = prefs.getBoolean("enablePhotoZoom", false);
        FeatureFlags.enablePostDownload = prefs.getBoolean("enablePostDownload", false);
        FeatureFlags.enableStoryDownload = prefs.getBoolean("enableStoryDownload", false);
        FeatureFlags.enableReelDownload = prefs.getBoolean("enableReelDownload", false);
        FeatureFlags.enableProfileDownload = prefs.getBoolean("enableProfileDownload", false);
        FeatureFlags.downloaderUsernameFolder = prefs.getBoolean("downloaderUsernameFolder", false);
        FeatureFlags.downloaderAddTimestamp   = prefs.getBoolean("downloaderAddTimestamp", false);
        FeatureFlags.copyMediaLink            = prefs.getBoolean("copyMediaLink", false);
        FeatureFlags.saveInstants             = prefs.getBoolean("saveInstants", false);
        FeatureFlags.uploadInstants           = prefs.getBoolean("uploadInstants", false);
        FeatureFlags.cacheStories             = prefs.getBoolean("cacheStories", false);
        FeatureFlags.customFontEnabled        = prefs.getBoolean("customFontEnabled", false);
        FeatureFlags.customFontPath           = prefs.getString("customFontPath", "");
        FeatureFlags.customEmojiEnabled       = prefs.getBoolean("customEmojiEnabled", false);
        FeatureFlags.customEmojiPath          = prefs.getString("customEmojiPath", "");
        FeatureFlags.downloaderCustomPath     = prefs.getString("downloaderCustomPath", "");
        FeatureFlags.downloaderCustomUri      = prefs.getString("downloaderCustomUri",  "");

        // Custom Theme
        FeatureFlags.customThemeEnabled = prefs.getBoolean("customThemeEnabled", false);
        FeatureFlags.themePresetId = prefs.getInt("themePresetId", 1);
        FeatureFlags.themePaletteJson = prefs.getString("themePaletteJson", "");

        FeatureManager.refreshFeatureStatus();
    }

    private static double readDoublePref(SharedPreferences p, String key, double fallback) {
        try {
            return Double.parseDouble(p.getString(key, String.valueOf(fallback)));
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    private static int readIntCompat(SharedPreferences p, String key, int fallback) {
        try {
            return p.getInt(key, fallback);
        } catch (ClassCastException e) {
            boolean legacy = false;
            try {
                legacy = p.getBoolean(key, false);
            } catch (ClassCastException ignored) {
            }
            int migrated = legacy ? 1080 : fallback;
            try {
                p.edit().putInt(key, migrated).apply();
            } catch (Throwable ignored) {
            }
            return migrated;
        }
    }

    public static void syncToCompanionApp() {
        if (prefs == null) {
            android.util.Log.w("InstaEclipse.Sync", "(SettingsManager) syncToCompanionApp aborted: prefs is null");
            return;
        }
        if (appContext == null) {
            android.util.Log.w("InstaEclipse.Sync", "(SettingsManager) syncToCompanionApp aborted: appContext is null");
            return;
        }
        if (!CommonUtils.isCompanionAppInstalled(appContext)) {
            return;
        }
        final Bundle bundle = new Bundle();
        for (Map.Entry<String, ?> entry : prefs.getAll().entrySet()) {
            Object val = entry.getValue();
            if (val instanceof Boolean) {
                bundle.putBoolean(entry.getKey(), (Boolean) val);
            } else if (val instanceof Integer) {
                bundle.putInt(entry.getKey(), (Integer) val);
            } else if (val instanceof Long) {
                bundle.putLong(entry.getKey(), (Long) val);
            } else if (val instanceof Float) {
                bundle.putFloat(entry.getKey(), (Float) val);
            } else if (val instanceof String) {
                bundle.putString(entry.getKey(), (String) val);
            }
        }
        android.util.Log.d("InstaEclipse.Sync", "(SettingsManager) syncToCompanionApp: sending " + bundle.size() + " entries");

        syncExecutor.execute(() -> {
            try {
                Uri uri = Uri.parse("content://ps.reso.instaeclipse.preferences");
                Bundle res = appContext.getContentResolver().call(uri, "saveAll", null, bundle);
                android.util.Log.d("InstaEclipse.Sync", "(SettingsManager) ContentProvider saveAll success: " + res);
            } catch (Throwable t) {
                android.util.Log.e("InstaEclipse.Sync", "(SettingsManager) ContentProvider saveAll failed: " + t.getMessage(), t);
            }
            try {
                Intent reply = new Intent("ps.reso.instaeclipse.ACTION_SEND_PREFS");
                reply.setPackage(CommonUtils.MY_PACKAGE_NAME);
                reply.putExtras(bundle);
                appContext.sendBroadcast(reply);
                android.util.Log.d("InstaEclipse.Sync", "(SettingsManager) Broadcast ACTION_SEND_PREFS sent");
            } catch (Throwable t) {
                android.util.Log.e("InstaEclipse.Sync", "(SettingsManager) Broadcast ACTION_SEND_PREFS failed: " + t.getMessage(), t);
            }
        });
    }

    public static void syncFromCompanionProvider(Context context) {
        init(context);
        if (appContext == null || prefs == null) return;
        if (!CommonUtils.isCompanionAppInstalled(appContext)) return;
        syncExecutor.execute(() -> {
            try {
                Uri uri = Uri.parse("content://ps.reso.instaeclipse.preferences");
                Bundle bundle = appContext.getContentResolver().call(uri, "getAll", null, null);
                if (bundle != null && !bundle.isEmpty()) {
                    SharedPreferences.Editor editor = prefs.edit();
                    boolean hasChanges = false;
                    for (String key : bundle.keySet()) {
                        Object val = bundle.get(key);
                        if (val instanceof Boolean) {
                            editor.putBoolean(key, (Boolean) val);
                            hasChanges = true;
                        } else if (val instanceof Integer) {
                            editor.putInt(key, (Integer) val);
                            hasChanges = true;
                        } else if (val instanceof Long) {
                            editor.putLong(key, (Long) val);
                            hasChanges = true;
                        } else if (val instanceof Float) {
                            editor.putFloat(key, (Float) val);
                            hasChanges = true;
                        } else if (val instanceof String) {
                            editor.putString(key, (String) val);
                            hasChanges = true;
                        }
                    }
                    if (hasChanges) {
                        editor.apply();
                        loadAllFlags(appContext);
                        FeatureManager.refreshFeatureStatus();
                    }
                }
            } catch (Throwable ignored) {
            }
        });
    }
}
