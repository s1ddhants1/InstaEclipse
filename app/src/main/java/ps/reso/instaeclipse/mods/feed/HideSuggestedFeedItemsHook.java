package ps.reso.instaeclipse.mods.feed;

import org.luckypray.dexkit.DexKitBridge;
import org.luckypray.dexkit.query.FindMethod;
import org.luckypray.dexkit.query.matchers.MethodMatcher;
import org.luckypray.dexkit.result.MethodData;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import ps.reso.instaeclipse.utils.core.DexKitCache;
import ps.reso.instaeclipse.utils.feature.FeatureFlags;
import ps.reso.instaeclipse.utils.feature.FeatureStatusTracker;
import ps.reso.instaeclipse.utils.log.ModuleLog;

public class HideSuggestedFeedItemsHook {

    private static final String CACHE_KEY_PARSER = "FeedItemParserClass";

    public void install(DexKitBridge bridge, ClassLoader classLoader) {
        XC_MethodHook filterHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                if (!FeatureFlags.hideSuggestionsInFeed && !FeatureFlags.hideThreadsSuggestions) return;

                Object result = param.getResult();
                if (result == null) return;

                // The parsed FeedItem is a large union type — exactly one of its many
                // optional fields is populated per server-sent unit. A real post always
                // has its Media field set; every suggestion/injection type (clips netego,
                // suggested users, Threads cross-promo units, ...) leaves it null.
                boolean hasMedia = false;
                boolean isThreadsUnit = false;
                for (Field f : result.getClass().getDeclaredFields()) {
                    String typeName = f.getType().getSimpleName();
                    if (typeName.equals("Media")) {
                        try {
                            f.setAccessible(true);
                            if (f.get(result) != null) { hasMedia = true; break; }
                        } catch (Throwable ignored) {}
                    } else if (typeName.contains("Threads") || typeName.startsWith("TextApp") || typeName.startsWith("XDTTextApp")) {
                        // Threads-in-feed cross-promo unit types (ThreadsInFeedUnit...,
                        // TextApp...UnitDictImpl, XDTTextApp...UnitDictImpl) — matched by
                        // simple name since these are stable, unobfuscated GraphQL-generated
                        // class names, unlike the surrounding obfuscated field names.
                        try {
                            f.setAccessible(true);
                            if (f.get(result) != null) isThreadsUnit = true;
                        } catch (Throwable ignored) {}
                    }
                }

                if (hasMedia) return; // real post — never hide

                boolean shouldHide = isThreadsUnit ? FeatureFlags.hideThreadsSuggestions
                                                    : FeatureFlags.hideSuggestionsInFeed;
                if (!shouldHide) return;

                param.setResult(null);
            }
        };

        if (DexKitCache.isCacheValid()) {
            String cached = DexKitCache.loadString(CACHE_KEY_PARSER);
            if (cached != null) {
                try {
                    hookBridgeMethod(cached, classLoader, filterHook);
                    markHookedForEnabledFlags();
                    return;
                } catch (Throwable t) {
                    ModuleLog.line("(InstaEclipse | HideSuggested): Cache hook failed: " + t.getMessage());
                }
            }
        }

        try {
            List<MethodData> methods = bridge.findMethod(
                    FindMethod.create().matcher(
                            MethodMatcher.create().usingStrings(
                                    "clips_netego", "suggested_users", "Unknown FeedItem Type"
                            )
                    )
            );

            if (methods.isEmpty()) {
                methods = bridge.findMethod(
                        FindMethod.create().matcher(
                                MethodMatcher.create().usingStrings("clips_netego", "media_or_ad")
                        )
                );
            }

            if (methods.isEmpty()) {
                methods = bridge.findMethod(
                        FindMethod.create().matcher(
                                MethodMatcher.create().usingStrings("clips_netego", "stories_netego", "bloks_netego")
                        )
                );
            }

            if (methods.isEmpty()) {
                ModuleLog.line("(InstaEclipse | HideSuggested): FeedItem parser not found.");
                return;
            }

            String targetClass = methods.get(0).getClassName();
            DexKitCache.saveString(CACHE_KEY_PARSER, targetClass);
            hookBridgeMethod(targetClass, classLoader, filterHook);
            markHookedForEnabledFlags();
            ModuleLog.line("(InstaEclipse | HideSuggested): Hooked: " + targetClass);

        } catch (Throwable t) {
            ModuleLog.line("(InstaEclipse | HideSuggested): Exception: " + t.getMessage());
        }
    }

    private void hookBridgeMethod(String className, ClassLoader classLoader, XC_MethodHook hook)
            throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className, false, classLoader);
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isBridge()) {
                XposedBridge.hookMethod(m, hook);
            }
        }
    }

    // Mark the feature(s) as hooked as soon as the filter is successfully attached, rather
    // than waiting for a matching feed item to actually be filtered at runtime. The status
    // toast is built ~1.5s after the main activity's onCreate — whether a suggestion/Threads
    // unit has scrolled into the feed by then is essentially random, so gating the status on
    // that produced a false even when the hook was installed and working correctly.
    private void markHookedForEnabledFlags() {
        if (FeatureFlags.hideSuggestionsInFeed) FeatureStatusTracker.setHooked("HideSuggestionsInFeed");
        if (FeatureFlags.hideThreadsSuggestions) FeatureStatusTracker.setHooked("HideThreadsSuggestions");
    }
}
