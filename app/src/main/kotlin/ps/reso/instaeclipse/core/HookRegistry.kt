package ps.reso.instaeclipse.core

object HookRegistry {

    private val hooks = listOf(
        Hook(
            id = "enablePostDownload",
            name = "Download Posts",
            description = "Save single photos and full carousels",
            category = HookCategory.DOWNLOADER,
            defaultEnabled = true
        ),
        Hook(
            id = "enableStoryDownload",
            name = "Download Stories",
            description = "Grab stories before they expire, including your own with the music kept intact",
            category = HookCategory.DOWNLOADER,
            defaultEnabled = true
        ),
        Hook(
            id = "enableReelDownload",
            name = "Download Reels",
            description = "Save reels straight to your gallery",
            category = HookCategory.DOWNLOADER,
            defaultEnabled = true
        ),
        Hook(
            id = "enableProfileDownload",
            name = "Download Profile Pictures",
            description = "Long press a profile to save the full-size picture",
            category = HookCategory.DOWNLOADER,
            defaultEnabled = true
        ),
        Hook(
            id = "downloaderUsernameFolder",
            name = "Save in Username Subfolder",
            description = "Sort saved media into a folder per account automatically",
            category = HookCategory.DOWNLOADER,
            defaultEnabled = false
        ),
        Hook(
            id = "downloaderAddTimestamp",
            name = "Add Timestamp to Filename",
            description = "Add the download date and time to each saved file",
            category = HookCategory.DOWNLOADER,
            defaultEnabled = false
        ),
        Hook(
            id = "copyMediaLink",
            name = "Copy Media Link",
            description = "Copy the direct CDN link for a post, reel, or a specific carousel slide",
            category = HookCategory.DOWNLOADER,
            defaultEnabled = false
        ),
        Hook(
            id = "saveInstants",
            name = "Save Instants (long-press to save)",
            description = "Long press to save an Instant you are viewing",
            category = HookCategory.DOWNLOADER,
            defaultEnabled = false
        ),
        Hook(
            id = "uploadInstants",
            name = "Upload Instant from Gallery",
            description = "Pick an image from your gallery and send it as an Instant",
            category = HookCategory.DOWNLOADER,
            defaultEnabled = false
        ),
        Hook(
            id = "cacheStories",
            name = "Cache Stories for 24h",
            description = "Quietly keep viewed stories for a day so you can reopen or save ones that were deleted or expired",
            category = HookCategory.DOWNLOADER,
            defaultEnabled = false
        ),

        Hook(
            id = "isGhostSeen",
            name = "Hide DM Seen",
            description = "Read messages without sending a read receipt",
            category = HookCategory.GHOST,
            defaultEnabled = false
        ),
        Hook(
            id = "isGhostTyping",
            name = "Hide Typing Indicator",
            description = "Type freely, the other person never sees the dots",
            category = HookCategory.GHOST,
            defaultEnabled = false
        ),
        Hook(
            id = "isGhostStory",
            name = "Hide Story Views",
            description = "Watch stories without showing up in the viewer list",
            category = HookCategory.GHOST,
            defaultEnabled = false
        ),
        Hook(
            id = "isGhostLive",
            name = "Hide Live Presence",
            description = "Join lives without being counted as a viewer",
            category = HookCategory.GHOST,
            defaultEnabled = false
        ),
        Hook(
            id = "allowScreenshots",
            name = "Allow Screenshots in DMs",
            description = "Re-enable screenshots where Instagram blocks them",
            category = HookCategory.GHOST,
            defaultEnabled = false
        ),
        Hook(
            id = "isGhostScreenshot",
            name = "Bypass Screenshot Detection",
            description = "Screenshot disappearing DM media without tipping anyone off",
            category = HookCategory.GHOST,
            defaultEnabled = false
        ),
        Hook(
            id = "isGhostViewOnce",
            name = "Hide View Once Opened",
            description = "Open view-once media without marking it as seen",
            category = HookCategory.GHOST,
            defaultEnabled = false
        ),
        Hook(
            id = "permanentViewMode",
            name = "Unlock View-Once/Twice",
            description = "Keep view-once and view-twice media around instead of it vanishing",
            category = HookCategory.GHOST,
            defaultEnabled = false
        ),
        Hook(
            id = "keepEphemeralMessages",
            name = "Keep Disappearing Messages",
            description = "Stop ephemeral messages from deleting themselves",
            category = HookCategory.GHOST,
            defaultEnabled = false
        ),
        Hook(
            id = "keepUnsentMessages",
            name = "Keep Unsent Messages",
            description = "When someone unsends a message, keep a private copy grouped per person",
            category = HookCategory.GHOST,
            defaultEnabled = false
        ),

        Hook(
            id = "lockDirectMessages",
            name = "Lock DMs",
            description = "Passcode gate just the inbox, leave the rest of the app open",
            category = HookCategory.LOCK,
            defaultEnabled = false
        ),
        Hook(
            id = "lockWholeApp",
            name = "Lock whole app",
            description = "Require a passcode (or your fingerprint) to open Instagram at all",
            category = HookCategory.LOCK,
            defaultEnabled = false
        ),
        Hook(
            id = "lockUseFingerprint",
            name = "Use fingerprint to unlock",
            description = "Use the device biometric prompt instead of typing the code",
            category = HookCategory.LOCK,
            defaultEnabled = true
        ),

        Hook(
            id = "hideSpecificChats",
            name = "Hide Specific Chats",
            description = "Pick chats that should quietly disappear from your inbox, manage them later from the menu",
            category = HookCategory.HIDE_CHATS,
            defaultEnabled = false
        ),

        Hook(
            id = "hideSuggestionsInFeed",
            name = "Hide Suggestions in Feed",
            description = "Strip out suggested posts, suggested reels, and other non-followed clutter",
            category = HookCategory.CLEAN_FEED,
            defaultEnabled = false
        ),
        Hook(
            id = "hideThreadsSuggestions",
            name = "Hide Threads Suggestions",
            description = "Drop the Threads cross-promo units on their own",
            category = HookCategory.CLEAN_FEED,
            defaultEnabled = false
        ),
        Hook(
            id = "limitFollowingFeed",
            name = "Limit feed to following profiles",
            description = "Filters the home feed to display only content from profiles you follow",
            category = HookCategory.CLEAN_FEED,
            defaultEnabled = false
        ),

        Hook(
            id = "isExtremeMode",
            name = "Extreme Mode (Irreversible until reinstall)",
            description = "Strip distractions hard until you reinstall",
            category = HookCategory.DISTRACTION_FREE,
            defaultEnabled = false,
            isExtreme = true,
            requiresAnyOf = listOf(
                "disableStories", "disableFeed", "disableReels",
                "disableReelsExceptDM", "disableExplore", "disableComments"
            )
        ),
        Hook(
            id = "disableStories",
            name = "Disable Stories",
            description = "Turn off Stories in Instagram",
            category = HookCategory.DISTRACTION_FREE,
            defaultEnabled = false,
            disabledWhenTrue = "isExtremeMode"
        ),
        Hook(
            id = "disableFeed",
            name = "Disable Feed",
            description = "Turn off the home timeline feed",
            category = HookCategory.DISTRACTION_FREE,
            defaultEnabled = false,
            disabledWhenTrue = "isExtremeMode"
        ),
        Hook(
            id = "disableReels",
            name = "Disable Reels",
            description = "Turn off Reels in Instagram",
            category = HookCategory.DISTRACTION_FREE,
            defaultEnabled = false,
            disabledWhenTrue = "isExtremeMode",
            cascadeOffKey = "disableReelsExceptDM"
        ),
        Hook(
            id = "disableReelsExceptDM",
            name = "Disable Reels Except in DMs",
            description = "Allow viewing reels links received directly in private messages",
            category = HookCategory.DISTRACTION_FREE,
            defaultEnabled = false,
            disabledWhenTrue = "isExtremeMode",
            dependsOn = "disableReels"
        ),
        Hook(
            id = "disableExplore",
            name = "Disable Explore",
            description = "Turn off the Explore page discovery grid",
            category = HookCategory.DISTRACTION_FREE,
            defaultEnabled = false,
            disabledWhenTrue = "isExtremeMode"
        ),
        Hook(
            id = "disableComments",
            name = "Disable Comments",
            description = "Turn off comments across all posts and reels",
            category = HookCategory.DISTRACTION_FREE,
            defaultEnabled = false,
            disabledWhenTrue = "isExtremeMode"
        ),

        Hook(
            id = "forceReelQuality",
            name = "Force Reels Quality",
            description = "Pin reels to a fixed quality instead of the adaptive bitrate",
            category = HookCategory.QUALITY,
            defaultEnabled = true
        ),

        Hook(
            id = "customThemeEnabled",
            name = "Enable Custom Theme",
            description = "Recolor the app with built-in presets or a full custom color palette",
            category = HookCategory.THEME,
            defaultEnabled = false
        ),
        Hook(
            id = "customFontEnabled",
            name = "Custom Font",
            description = "Load your own .ttf or .otf and use it across the app, chats and captions included",
            category = HookCategory.THEME,
            defaultEnabled = false
        ),
        Hook(
            id = "customEmojiEnabled",
            name = "Custom Emoji",
            description = "Swap in your own color emoji font",
            category = HookCategory.THEME,
            defaultEnabled = false
        ),

        Hook(
            id = "isAdBlockEnabled",
            name = "Block Ads",
            description = "Drop sponsored posts and ad units",
            category = HookCategory.ADS,
            defaultEnabled = true
        ),
        Hook(
            id = "isAnalyticsBlocked",
            name = "Block Analytics",
            description = "Cut Instagram's analytics and telemetry calls",
            category = HookCategory.ADS,
            defaultEnabled = true
        ),
        Hook(
            id = "disableTrackingLinks",
            name = "Disable Tracking Links",
            description = "Strip tracking parameters from links you share and copy, referral tokens included",
            category = HookCategory.ADS,
            defaultEnabled = true
        ),

        Hook(
            id = "spoofLocation",
            name = "Spoof GPS Location",
            description = "Report a location of your choosing to Instagram",
            category = HookCategory.LOCATION,
            defaultEnabled = false
        ),

        Hook(
            id = "disableStoryFlipping",
            name = "Disable Story Auto-Swipe",
            description = "Stop stories from advancing on their own",
            category = HookCategory.MISC,
            defaultEnabled = false
        ),
        Hook(
            id = "disableVideoAutoPlay",
            name = "Disable Video Autoplay",
            description = "Videos wait until you tap them",
            category = HookCategory.MISC,
            defaultEnabled = false
        ),
        Hook(
            id = "spoofLastSeen",
            name = "Spoof Last Seen (freeze)",
            description = "Freeze your \"active\" status instead of updating it live",
            category = HookCategory.MISC,
            defaultEnabled = false
        ),
        Hook(
            id = "disableRepost",
            name = "Disable Repost",
            description = "Keep the repost button from actually reposting",
            category = HookCategory.MISC,
            defaultEnabled = false
        ),
        Hook(
            id = "showFollowerToast",
            name = "Show Follower Toast",
            description = "Get a heads-up on whether someone follows you back when you open their profile",
            category = HookCategory.MISC,
            defaultEnabled = false
        ),
        Hook(
            id = "showFeatureToasts",
            name = "Show Feature Toasts",
            description = "Shows a toast checklist of loaded features",
            category = HookCategory.MISC,
            defaultEnabled = false
        ),
        Hook(
            id = "enableStoryMentions",
            name = "View Story Mentions",
            description = "See every @mention in a story at once",
            category = HookCategory.MISC,
            defaultEnabled = true
        ),
        Hook(
            id = "disableDiscoverPeople",
            name = "Disable Discover People",
            description = "Remove the \"people you may know\" row",
            category = HookCategory.MISC,
            defaultEnabled = false
        ),
        Hook(
            id = "enableCopyComment",
            name = "Copy Comment",
            description = "Copy any comment with one tap",
            category = HookCategory.MISC,
            defaultEnabled = true
        ),
        Hook(
            id = "disableDoubleTapLike",
            name = "Disable Double Tap to Like",
            description = "Stop accidental likes from a stray double tap",
            category = HookCategory.MISC,
            defaultEnabled = false
        ),
        Hook(
            id = "enableCaptionCopy",
            name = "Copy Caption",
            description = "Copy a post or reel caption from the overflow menu",
            category = HookCategory.MISC,
            defaultEnabled = true
        ),
        Hook(
            id = "enablePhotoZoom",
            name = "Photo Zoom (Long-Press)",
            description = "Long press a feed photo to open it full screen with pinch to zoom",
            category = HookCategory.MISC,
            defaultEnabled = true
        ),
        Hook(
            id = "removeMetaAI",
            name = "Remove Meta AI",
            description = "Take the Meta AI entry points out of search, the composer, and the reel more-options button",
            category = HookCategory.MISC,
            defaultEnabled = false
        ),

        Hook(
            id = "isDevEnabled",
            name = "Enable Developer Mode",
            description = "Open the full internal developer/QE panel",
            category = HookCategory.DEV_OPTIONS,
            defaultEnabled = true
        ),
        Hook(
            id = "removeBuildExpiredPopup",
            name = "Remove Build Expired Popup",
            description = "Dismiss the \"build expired\" nag on older builds",
            category = HookCategory.DEV_OPTIONS,
            defaultEnabled = true
        ),
        Hook(
            id = "autoClearCache",
            name = "Auto-Clear Cache",
            description = "Clears Instagram's media cache when you leave the app, if it's over the chosen size",
            category = HookCategory.DEV_OPTIONS,
            defaultEnabled = false
        )
    )

    private val hooksById: Map<String, Hook> by lazy { hooks.associateBy { it.id } }

    fun getAllHooks(): List<Hook> = hooks

    fun getHook(id: String): Hook? = hooksById[id]

}
