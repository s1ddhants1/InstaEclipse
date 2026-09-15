package ps.reso.instaeclipse.mods.feed;

import org.luckypray.dexkit.DexKitBridge;
import org.luckypray.dexkit.query.FindClass;
import org.luckypray.dexkit.query.matchers.ClassMatcher;
import org.luckypray.dexkit.result.ClassData;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import ps.reso.instaeclipse.utils.core.DexKitCache;
import ps.reso.instaeclipse.utils.feature.FeatureFlags;
import ps.reso.instaeclipse.utils.feature.FeatureStatusTracker;
import ps.reso.instaeclipse.utils.log.ModuleLog;

public class LimitFeedHook {

    private static final String CACHE_KEY_CLASS = "LimitFeedRequestClass";
    private static final String PAGINATION_HEADER = "pagination_source";
    private static final String DEFAULT_SOURCE = "feed_recs";
    private static final String FOLLOWING_SOURCE = "following";

    public void install(DexKitBridge bridge, ClassLoader classLoader) {
        if (DexKitCache.isCacheValid()) {
            String cached = DexKitCache.loadString(CACHE_KEY_CLASS);
            if (cached != null) {
                try {
                    Class<?> cls = Class.forName(cached, false, classLoader);
                    if (hookConstructors(cls) > 0) {
                        FeatureStatusTracker.setHooked("LimitFeedToFollowing");
                        ModuleLog.line("(InstaEclipse | LimitFeed): ✅ Hooked via cache: " + cached);
                        return;
                    }
                } catch (Throwable t) {
                    ModuleLog.line("(InstaEclipse | LimitFeed): ⚠️ Cache hook failed: " + t.getMessage());
                }
            }
        }

        if (bridge == null) {
            ModuleLog.line("(InstaEclipse | LimitFeed): ❌ DexKitBridge is null");
            if (FeatureFlags.limitFollowingFeed) {
                FeatureStatusTracker.setBroken("LimitFeedToFollowing");
            }
            return;
        }

        try {
            List<ClassData> classes = bridge.findClass(FindClass.create()
                    .matcher(ClassMatcher.create().usingStrings("Request{mReason=", ", mInstanceNumber=")));

            if (classes == null || classes.isEmpty()) {
                ModuleLog.line("(InstaEclipse | LimitFeed): ❌ Request class not found.");
                if (FeatureFlags.limitFollowingFeed) {
                    FeatureStatusTracker.setBroken("LimitFeedToFollowing");
                }
                return;
            }

            int totalHooked = 0;
            String hookedClassName = null;
            for (ClassData classData : classes) {
                try {
                    Class<?> cls = Class.forName(classData.getName(), false, classLoader);
                    int count = hookConstructors(cls);
                    if (count > 0) {
                        totalHooked += count;
                        hookedClassName = classData.getName();
                    }
                } catch (Throwable t) {
                    ModuleLog.line("(InstaEclipse | LimitFeed): ⚠️ Failed hooking " + classData.getName() + ": " + t.getMessage());
                }
            }

            if (totalHooked > 0) {
                if (hookedClassName != null) {
                    DexKitCache.saveString(CACHE_KEY_CLASS, hookedClassName);
                }
                FeatureStatusTracker.setHooked("LimitFeedToFollowing");
                ModuleLog.line("(InstaEclipse | LimitFeed): ✅ Hooked " + totalHooked + " constructors");
            } else {
                ModuleLog.line("(InstaEclipse | LimitFeed): ❌ Failed to hook any constructors");
                if (FeatureFlags.limitFollowingFeed) {
                    FeatureStatusTracker.setBroken("LimitFeedToFollowing");
                }
            }
        } catch (Throwable t) {
            ModuleLog.line("(InstaEclipse | LimitFeed): ❌ Exception: " + t.getMessage());
            if (FeatureFlags.limitFollowingFeed) {
                FeatureStatusTracker.setBroken("LimitFeedToFollowing");
            }
        }
    }

    private int hookConstructors(Class<?> cls) {
        int hooked = 0;
        for (Constructor<?> ctor : cls.getDeclaredConstructors()) {
            try {
                ctor.setAccessible(true);
                XposedBridge.hookMethod(ctor, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        if (!FeatureFlags.limitFollowingFeed) return;
                        Object instance = param.thisObject;
                        if (instance == null) return;
                        try {
                            patchHeaderMap(instance);
                        } catch (Throwable t) {
                            ModuleLog.line("(InstaEclipse | LimitFeed): ⚠️ Header patch failed: " + t.getMessage());
                        }
                    }
                });
                hooked++;
            } catch (Throwable t) {
                ModuleLog.line("(InstaEclipse | LimitFeed): ⚠️ ctor hook failed on " + cls.getName() + ": " + t.getMessage());
            }
        }
        return hooked;
    }

    @SuppressWarnings("unchecked")
    private void patchHeaderMap(Object instance) throws IllegalAccessException {
        Class<?> cls = instance.getClass();
        while (cls != null && cls != Object.class) {
            for (Field field : cls.getDeclaredFields()) {
                if (!Map.class.isAssignableFrom(field.getType())) continue;
                field.setAccessible(true);
                Object obj = field.get(instance);
                if (!(obj instanceof Map)) continue;
                Map<String, String> map = (Map<String, String>) obj;
                if (!map.containsKey(PAGINATION_HEADER)) continue;

                String current = map.get(PAGINATION_HEADER);
                if (current != null && !current.equals(DEFAULT_SOURCE)) continue;

                Map<String, String> patched = new HashMap<>(map);
                patched.put(PAGINATION_HEADER, FOLLOWING_SOURCE);
                field.set(instance, patched);
                return;
            }
            cls = cls.getSuperclass();
        }
    }
}
