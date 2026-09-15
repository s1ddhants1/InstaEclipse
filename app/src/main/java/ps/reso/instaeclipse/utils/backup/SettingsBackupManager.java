package ps.reso.instaeclipse.utils.backup;

import org.json.JSONException;
import org.json.JSONObject;

import ps.reso.instaeclipse.utils.feature.FeatureFlags;

public class SettingsBackupManager {

    private static final int VERSION = 1;

    /** Serialises every known FeatureFlag into a versioned JSON string. */
    public static String toJson() throws JSONException {
        JSONObject s = new JSONObject();

        // Developer
        s.put("isDevEnabled",            FeatureFlags.isDevEnabled);
        s.put("removeBuildExpiredPopup", FeatureFlags.removeBuildExpiredPopup);

        // Ghost Mode
        s.put("isGhostModeEnabled",      FeatureFlags.isGhostModeEnabled);
        s.put("isGhostSeen",             FeatureFlags.isGhostSeen);
        s.put("isGhostTyping",           FeatureFlags.isGhostTyping);
        s.put("isGhostScreenshot",       FeatureFlags.isGhostScreenshot);
        s.put("isGhostViewOnce",         FeatureFlags.isGhostViewOnce);
        s.put("isGhostStory",            FeatureFlags.isGhostStory);
        s.put("isGhostLive",             FeatureFlags.isGhostLive);
        s.put("allowScreenshots",        FeatureFlags.allowScreenshots);
        s.put("keepEphemeralMessages",   FeatureFlags.keepEphemeralMessages);

        s.put("permanentViewMode",       FeatureFlags.permanentViewMode);
        s.put("keepUnsentMessages",      FeatureFlags.keepUnsentMessages);

        // Quick Toggles
        s.put("quickToggleSeen",         FeatureFlags.quickToggleSeen);
        s.put("quickToggleTyping",       FeatureFlags.quickToggleTyping);
        s.put("quickToggleScreenshot",   FeatureFlags.quickToggleScreenshot);
        s.put("quickToggleViewOnce",     FeatureFlags.quickToggleViewOnce);
        s.put("quickToggleStory",        FeatureFlags.quickToggleStory);
        s.put("quickToggleLive",         FeatureFlags.quickToggleLive);
        s.put("quickToggleEphemeral",    FeatureFlags.quickToggleEphemeral);
        s.put("quickTogglePermanentView",FeatureFlags.quickTogglePermanentView);
        s.put("quickToggleAllowScreenshots", FeatureFlags.quickToggleAllowScreenshots);

        // Clean Feed
        s.put("hideSuggestionsInFeed",      FeatureFlags.hideSuggestionsInFeed);
        s.put("hideThreadsSuggestions",     FeatureFlags.hideThreadsSuggestions);
        s.put("limitFollowingFeed",         FeatureFlags.limitFollowingFeed);

        // Ads
        s.put("isAdBlockEnabled",        FeatureFlags.isAdBlockEnabled);
        s.put("isAnalyticsBlocked",      FeatureFlags.isAnalyticsBlocked);
        s.put("disableTrackingLinks",    FeatureFlags.disableTrackingLinks);

        // Distraction Free
        s.put("isExtremeMode",           FeatureFlags.isExtremeMode);
        s.put("disableStories",          FeatureFlags.disableStories);
        s.put("disableFeed",             FeatureFlags.disableFeed);
        s.put("disableReels",            FeatureFlags.disableReels);
        s.put("disableReelsExceptDM",    FeatureFlags.disableReelsExceptDM);
        s.put("disableExplore",          FeatureFlags.disableExplore);
        s.put("disableComments",         FeatureFlags.disableComments);
        s.put("disableDiscoverPeople",   FeatureFlags.disableDiscoverPeople);

        // Miscellaneous
        s.put("disableStoryFlipping",    FeatureFlags.disableStoryFlipping);
        s.put("disableVideoAutoPlay",    FeatureFlags.disableVideoAutoPlay);
        s.put("spoofLastSeen",           FeatureFlags.spoofLastSeen);
        s.put("spoofLocation",           FeatureFlags.spoofLocation);
        s.put("spoofLat",                String.valueOf(FeatureFlags.spoofLat));
        s.put("spoofLng",                String.valueOf(FeatureFlags.spoofLng));
        s.put("forceReelQuality",        FeatureFlags.forceReelQuality);
        s.put("disableRepost",           FeatureFlags.disableRepost);
        s.put("showFollowerToast",       FeatureFlags.showFollowerToast);
        s.put("showFeatureToasts",       FeatureFlags.showFeatureToasts);
        s.put("enableStoryMentions",     FeatureFlags.enableStoryMentions);

        // Downloader
        s.put("enablePostDownload",      FeatureFlags.enablePostDownload);
        s.put("enableStoryDownload",     FeatureFlags.enableStoryDownload);
        s.put("enableReelDownload",      FeatureFlags.enableReelDownload);
        s.put("enableProfileDownload",   FeatureFlags.enableProfileDownload);
        s.put("downloaderUsernameFolder",FeatureFlags.downloaderUsernameFolder);
        s.put("downloaderAddTimestamp",  FeatureFlags.downloaderAddTimestamp);
        s.put("copyMediaLink",           FeatureFlags.copyMediaLink);
        s.put("saveInstants",            FeatureFlags.saveInstants);
        s.put("uploadInstants",          FeatureFlags.uploadInstants);

        s.put("isDistractionFree",       FeatureFlags.isDistractionFree);
        s.put("isMiscEnabled",           FeatureFlags.isMiscEnabled);
        s.put("enableCopyComment",       FeatureFlags.enableCopyComment);
        s.put("enableCaptionCopy",       FeatureFlags.enableCaptionCopy);
        s.put("disableDoubleTapLike",    FeatureFlags.disableDoubleTapLike);
        s.put("enablePhotoZoom",         FeatureFlags.enablePhotoZoom);
        s.put("cacheStories",            FeatureFlags.cacheStories);
        s.put("removeMetaAI",            FeatureFlags.removeMetaAI);
        s.put("autoClearCache",          FeatureFlags.autoClearCache);
        s.put("autoClearCacheSizeMb",    FeatureFlags.autoClearCacheSizeMb);
        s.put("customThemeEnabled",      FeatureFlags.customThemeEnabled);
        s.put("themePresetId",           FeatureFlags.themePresetId);
        s.put("themePaletteJson",        FeatureFlags.themePaletteJson);
        s.put("customFontEnabled",       FeatureFlags.customFontEnabled);
        s.put("customFontPath",          FeatureFlags.customFontPath);
        s.put("customEmojiEnabled",      FeatureFlags.customEmojiEnabled);
        s.put("customEmojiPath",         FeatureFlags.customEmojiPath);
        s.put("downloaderCustomPath",    FeatureFlags.downloaderCustomPath);
        s.put("downloaderCustomUri",     FeatureFlags.downloaderCustomUri);

        s.put("lockDirectMessages",      FeatureFlags.lockDirectMessages);
        s.put("lockDirectAlways",        FeatureFlags.lockDirectAlways);
        s.put("lockWholeApp",            FeatureFlags.lockWholeApp);
        s.put("lockUseFingerprint",      FeatureFlags.lockUseFingerprint);
        s.put("hideSpecificChats",       FeatureFlags.hideSpecificChats);

        JSONObject root = new JSONObject();
        root.put("version",  VERSION);
        root.put("settings", s);
        return root.toString(2);
    }

    /**
     * Applies a backup JSON string to the in-memory FeatureFlags.
     * Supports both the versioned {"version":1,"settings":{...}} format
     * and a flat {key:value} format for forward-compatibility.
     * Unknown keys are silently ignored so older backups work on newer builds.
     */
    public static void fromJson(String json) throws JSONException {
        JSONObject root = new JSONObject(json);
        JSONObject s = root.has("settings") ? root.getJSONObject("settings") : root;

        if (s.has("isDevEnabled"))            FeatureFlags.isDevEnabled            = s.getBoolean("isDevEnabled");
        if (s.has("removeBuildExpiredPopup")) FeatureFlags.removeBuildExpiredPopup = s.getBoolean("removeBuildExpiredPopup");

        if (s.has("isGhostModeEnabled"))     FeatureFlags.isGhostModeEnabled     = s.getBoolean("isGhostModeEnabled");
        if (s.has("isGhostSeen"))            FeatureFlags.isGhostSeen            = s.getBoolean("isGhostSeen");
        if (s.has("isGhostTyping"))          FeatureFlags.isGhostTyping          = s.getBoolean("isGhostTyping");
        if (s.has("isGhostScreenshot"))      FeatureFlags.isGhostScreenshot      = s.getBoolean("isGhostScreenshot");
        if (s.has("isGhostViewOnce"))        FeatureFlags.isGhostViewOnce        = s.getBoolean("isGhostViewOnce");
        if (s.has("isGhostStory"))           FeatureFlags.isGhostStory           = s.getBoolean("isGhostStory");
        if (s.has("isGhostLive"))            FeatureFlags.isGhostLive            = s.getBoolean("isGhostLive");
        if (s.has("allowScreenshots"))         FeatureFlags.allowScreenshots         = s.getBoolean("allowScreenshots");
        if (s.has("keepEphemeralMessages"))    FeatureFlags.keepEphemeralMessages    = s.getBoolean("keepEphemeralMessages");
        if (s.has("permanentViewMode"))        FeatureFlags.permanentViewMode        = s.getBoolean("permanentViewMode");
        if (s.has("keepUnsentMessages"))       FeatureFlags.keepUnsentMessages       = s.getBoolean("keepUnsentMessages");

        if (s.has("quickToggleSeen"))        FeatureFlags.quickToggleSeen        = s.getBoolean("quickToggleSeen");
        if (s.has("quickToggleTyping"))      FeatureFlags.quickToggleTyping      = s.getBoolean("quickToggleTyping");
        if (s.has("quickToggleScreenshot"))  FeatureFlags.quickToggleScreenshot  = s.getBoolean("quickToggleScreenshot");
        if (s.has("quickToggleViewOnce"))    FeatureFlags.quickToggleViewOnce    = s.getBoolean("quickToggleViewOnce");
        if (s.has("quickToggleStory"))       FeatureFlags.quickToggleStory       = s.getBoolean("quickToggleStory");
        if (s.has("quickToggleLive"))        FeatureFlags.quickToggleLive        = s.getBoolean("quickToggleLive");
        if (s.has("quickToggleEphemeral"))   FeatureFlags.quickToggleEphemeral   = s.getBoolean("quickToggleEphemeral");
        if (s.has("quickTogglePermanentView")) FeatureFlags.quickTogglePermanentView = s.getBoolean("quickTogglePermanentView");
        if (s.has("quickToggleAllowScreenshots")) FeatureFlags.quickToggleAllowScreenshots = s.getBoolean("quickToggleAllowScreenshots");

        if (s.has("hideSuggestionsInFeed"))     FeatureFlags.hideSuggestionsInFeed     = s.getBoolean("hideSuggestionsInFeed");
        if (s.has("hideThreadsSuggestions"))    FeatureFlags.hideThreadsSuggestions    = s.getBoolean("hideThreadsSuggestions");
        if (s.has("limitFollowingFeed"))        FeatureFlags.limitFollowingFeed        = s.getBoolean("limitFollowingFeed");

        if (s.has("isAdBlockEnabled"))       FeatureFlags.isAdBlockEnabled       = s.getBoolean("isAdBlockEnabled");
        if (s.has("isAnalyticsBlocked"))     FeatureFlags.isAnalyticsBlocked     = s.getBoolean("isAnalyticsBlocked");
        if (s.has("disableTrackingLinks"))   FeatureFlags.disableTrackingLinks   = s.getBoolean("disableTrackingLinks");

        if (s.has("isExtremeMode"))          FeatureFlags.isExtremeMode          = s.getBoolean("isExtremeMode");
        if (s.has("disableStories"))         FeatureFlags.disableStories         = s.getBoolean("disableStories");
        if (s.has("disableFeed"))            FeatureFlags.disableFeed            = s.getBoolean("disableFeed");
        if (s.has("disableReels"))           FeatureFlags.disableReels           = s.getBoolean("disableReels");
        if (s.has("disableReelsExceptDM"))   FeatureFlags.disableReelsExceptDM   = s.getBoolean("disableReelsExceptDM");
        if (s.has("disableExplore"))         FeatureFlags.disableExplore         = s.getBoolean("disableExplore");
        if (s.has("disableComments"))        FeatureFlags.disableComments        = s.getBoolean("disableComments");
        if (s.has("disableDiscoverPeople"))  FeatureFlags.disableDiscoverPeople  = s.getBoolean("disableDiscoverPeople");

        if (s.has("disableStoryFlipping"))   FeatureFlags.disableStoryFlipping   = s.getBoolean("disableStoryFlipping");
        if (s.has("disableVideoAutoPlay"))   FeatureFlags.disableVideoAutoPlay   = s.getBoolean("disableVideoAutoPlay");
        if (s.has("spoofLastSeen"))          FeatureFlags.spoofLastSeen          = s.getBoolean("spoofLastSeen");
        if (s.has("spoofLocation"))          FeatureFlags.spoofLocation          = s.getBoolean("spoofLocation");
        if (s.has("spoofLat"))               FeatureFlags.spoofLat               = parseDouble(s.get("spoofLat"), 0.0);
        if (s.has("spoofLng"))               FeatureFlags.spoofLng               = parseDouble(s.get("spoofLng"), 0.0);
        if (s.has("forceReelQuality"))       FeatureFlags.forceReelQuality       = parseQuality(s.get("forceReelQuality"), 0);
        if (s.has("disableRepost"))          FeatureFlags.disableRepost          = s.getBoolean("disableRepost");
        if (s.has("showFollowerToast"))      FeatureFlags.showFollowerToast      = s.getBoolean("showFollowerToast");
        if (s.has("showFeatureToasts"))      FeatureFlags.showFeatureToasts      = s.getBoolean("showFeatureToasts");
        if (s.has("enableStoryMentions"))    FeatureFlags.enableStoryMentions    = s.getBoolean("enableStoryMentions");

        if (s.has("enablePostDownload"))     FeatureFlags.enablePostDownload     = s.getBoolean("enablePostDownload");
        if (s.has("enableStoryDownload"))    FeatureFlags.enableStoryDownload    = s.getBoolean("enableStoryDownload");
        if (s.has("enableReelDownload"))     FeatureFlags.enableReelDownload     = s.getBoolean("enableReelDownload");
        if (s.has("enableProfileDownload"))  FeatureFlags.enableProfileDownload  = s.getBoolean("enableProfileDownload");
        if (s.has("downloaderUsernameFolder")) FeatureFlags.downloaderUsernameFolder = s.getBoolean("downloaderUsernameFolder");
        if (s.has("downloaderAddTimestamp")) FeatureFlags.downloaderAddTimestamp  = s.getBoolean("downloaderAddTimestamp");
        if (s.has("copyMediaLink"))          FeatureFlags.copyMediaLink          = s.getBoolean("copyMediaLink");
        if (s.has("saveInstants"))           FeatureFlags.saveInstants           = s.getBoolean("saveInstants");
        if (s.has("uploadInstants"))         FeatureFlags.uploadInstants         = s.getBoolean("uploadInstants");

        if (s.has("isDistractionFree"))       FeatureFlags.isDistractionFree       = s.getBoolean("isDistractionFree");
        if (s.has("isMiscEnabled"))          FeatureFlags.isMiscEnabled          = s.getBoolean("isMiscEnabled");
        if (s.has("enableCopyComment"))      FeatureFlags.enableCopyComment      = s.getBoolean("enableCopyComment");
        if (s.has("enableCaptionCopy"))      FeatureFlags.enableCaptionCopy      = s.getBoolean("enableCaptionCopy");
        if (s.has("disableDoubleTapLike"))   FeatureFlags.disableDoubleTapLike   = s.getBoolean("disableDoubleTapLike");
        if (s.has("enablePhotoZoom"))        FeatureFlags.enablePhotoZoom        = s.getBoolean("enablePhotoZoom");
        if (s.has("cacheStories"))           FeatureFlags.cacheStories           = s.getBoolean("cacheStories");
        if (s.has("removeMetaAI"))           FeatureFlags.removeMetaAI           = s.getBoolean("removeMetaAI");
        if (s.has("autoClearCache"))         FeatureFlags.autoClearCache         = s.getBoolean("autoClearCache");
        if (s.has("autoClearCacheSizeMb"))   FeatureFlags.autoClearCacheSizeMb   = parseInt(s.get("autoClearCacheSizeMb"), 100);
        if (s.has("customThemeEnabled"))     FeatureFlags.customThemeEnabled     = s.getBoolean("customThemeEnabled");
        if (s.has("themePresetId"))          FeatureFlags.themePresetId          = parseInt(s.get("themePresetId"), 1);
        if (s.has("themePaletteJson"))       FeatureFlags.themePaletteJson       = s.getString("themePaletteJson");
        if (s.has("customFontEnabled"))      FeatureFlags.customFontEnabled      = s.getBoolean("customFontEnabled");
        if (s.has("customFontPath"))         FeatureFlags.customFontPath         = s.getString("customFontPath");
        if (s.has("customEmojiEnabled"))     FeatureFlags.customEmojiEnabled     = s.getBoolean("customEmojiEnabled");
        if (s.has("customEmojiPath"))        FeatureFlags.customEmojiPath        = s.getString("customEmojiPath");
        if (s.has("downloaderCustomPath"))   FeatureFlags.downloaderCustomPath   = s.getString("downloaderCustomPath");
        if (s.has("downloaderCustomUri"))    FeatureFlags.downloaderCustomUri    = s.getString("downloaderCustomUri");

        if (s.has("lockDirectMessages"))     FeatureFlags.lockDirectMessages     = s.getBoolean("lockDirectMessages");
        if (s.has("lockDirectAlways"))       FeatureFlags.lockDirectAlways       = s.getBoolean("lockDirectAlways");
        if (s.has("lockWholeApp"))           FeatureFlags.lockWholeApp           = s.getBoolean("lockWholeApp");
        if (s.has("lockUseFingerprint"))     FeatureFlags.lockUseFingerprint     = s.getBoolean("lockUseFingerprint");
        if (s.has("hideSpecificChats"))      FeatureFlags.hideSpecificChats      = s.getBoolean("hideSpecificChats");
    }

    private static int parseQuality(Object raw, int fallback) {
        if (raw instanceof Boolean) return ((Boolean) raw) ? 1080 : 0;
        return parseInt(raw, fallback);
    }

    private static int parseInt(Object raw, int fallback) {
        if (raw instanceof Number) return ((Number) raw).intValue();
        if (raw instanceof String) {
            try {
                return Integer.parseInt((String) raw);
            } catch (Throwable ignored) {}
        }
        return fallback;
    }

    private static double parseDouble(Object raw, double fallback) {
        if (raw instanceof Number) return ((Number) raw).doubleValue();
        if (raw instanceof String) {
            try {
                return Double.parseDouble((String) raw);
            } catch (Throwable ignored) {}
        }
        return fallback;
    }
}
