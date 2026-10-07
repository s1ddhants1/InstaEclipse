package ps.reso.instaeclipse.utils.feature;

import ps.reso.instaeclipse.R;

public class FeatureManager {

    private static void update(boolean enabled, String featureName, int stringResId) {
        if (enabled) {
            FeatureStatusTracker.setEnabled(featureName, stringResId);
        } else {
            FeatureStatusTracker.setDisabled(featureName);
        }
    }

    public static void refreshFeatureStatus() {
        // Developer Options
        update(FeatureFlags.isDevEnabled, "DevOptions", R.string.ig_dialog_section_dev_options);

        // Ghost Mode
        update(FeatureFlags.isGhostSeen, "GhostSeen", R.string.ig_dialog_ghost_hide_dm_seen);
        update(FeatureFlags.isGhostTyping, "GhostTyping", R.string.ig_dialog_ghost_hide_typing);
        update(FeatureFlags.isGhostScreenshot, "GhostScreenshot", R.string.ig_dialog_ghost_bypass_screenshot);
        update(FeatureFlags.isGhostViewOnce, "GhostViewOnce", R.string.ig_dialog_ghost_hide_view_once);
        update(FeatureFlags.isGhostStory, "GhostStories", R.string.ig_dialog_ghost_hide_story_views);
        update(FeatureFlags.isGhostLive, "GhostLive", R.string.ig_dialog_ghost_hide_live_presence);
        update(FeatureFlags.allowScreenshots, "AllowScreenshots", R.string.ig_dialog_ghost_allow_screenshots_dms);
        update(FeatureFlags.keepEphemeralMessages, "KeepEphemeralMessages", R.string.ig_dialog_ghost_keep_disappearing);
        update(FeatureFlags.permanentViewMode, "PermanentViewMode", R.string.ig_dialog_ghost_permanent_view_once);
        update(FeatureFlags.keepUnsentMessages, "KeepUnsentMessages", R.string.ig_dialog_ghost_keep_unsent);

        // Security & UI
        update(FeatureFlags.removeMetaAI, "RemoveMetaAI", R.string.ig_dialog_misc_remove_meta_ai);
        update(FeatureFlags.lockDirectMessages, "LockDirectMessages", R.string.ig_dialog_misc_lock_dms);
        update(FeatureFlags.hideSpecificChats, "HideSpecificChats", R.string.ig_hide_chats_title);

        // Clean Feed
        update(FeatureFlags.hideSuggestionsInFeed, "HideSuggestionsInFeed", R.string.ig_dialog_clean_feed_hide_suggested);
        update(FeatureFlags.hideThreadsSuggestions, "HideThreadsSuggestions", R.string.ig_dialog_clean_feed_hide_threads);
        update(FeatureFlags.limitFollowingFeed, "LimitFeedToFollowing", R.string.ig_dialog_clean_feed_limit_following);

        // Miscellaneous
        update(FeatureFlags.disableTrackingLinks, "DisableTrackingLinks", R.string.ig_dialog_ad_disable_tracking);
        update(FeatureFlags.showFollowerToast, "FollowerToast", R.string.ig_dialog_misc_show_follower_toast);
        update(FeatureFlags.enableStoryMentions, "StoryMentions", R.string.ig_dialog_misc_view_story_mentions);
        update(FeatureFlags.disableDiscoverPeople, "DisableDiscoverPeople", R.string.ig_dialog_misc_disable_discover_people);
        update(FeatureFlags.forceReelQuality > 0, "ForceReelQuality", R.string.ig_dialog_quality_force_reels);
        update(FeatureFlags.spoofLocation, "SpoofLocation", R.string.ig_dialog_location_spoof_enable);
        update(FeatureFlags.spoofLastSeen, "SpoofLastSeen", R.string.ig_dialog_misc_spoof_last_seen);
        update(FeatureFlags.customThemeEnabled, "CustomTheme", R.string.theme_title);
        update(FeatureFlags.removeBuildExpiredPopup, "RemoveBuildExpiredPopup", R.string.ig_dialog_dev_remove_build_expired);
        update(FeatureFlags.enablePostDownload, "PostDownload", R.string.ig_dialog_downloader_posts);
        update(FeatureFlags.copyMediaLink, "CopyMediaLink", R.string.ig_copy_link_title);
        update(FeatureFlags.saveInstants, "SaveInstants", R.string.ig_instant_save_title);
        update(FeatureFlags.uploadInstants, "UploadInstants", R.string.ig_instant_upload_title);
        update(FeatureFlags.cacheStories, "CacheStories", R.string.ig_story_cache_title);
        update(FeatureFlags.customFontEnabled, "CustomFont", R.string.ig_custom_font_title);
        update(FeatureFlags.customEmojiEnabled, "CustomEmoji", R.string.ig_custom_emoji_title);
        update(FeatureFlags.enableStoryDownload, "StoryDownload", R.string.ig_dialog_downloader_stories);
        update(FeatureFlags.enableReelDownload, "ReelDownload", R.string.ig_dialog_downloader_reels);
        update(FeatureFlags.enableProfileDownload, "ProfileDownload", R.string.ig_dialog_downloader_profiles);
        update(FeatureFlags.disableDoubleTapLike, "DisableDoubleTapLike", R.string.ig_dialog_misc_disable_double_tap_like);
    }
}
