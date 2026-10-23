package ps.reso.instaeclipse.mods.misc;

import org.luckypray.dexkit.DexKitBridge;
import org.luckypray.dexkit.query.FindMethod;
import org.luckypray.dexkit.query.matchers.MethodMatcher;
import org.luckypray.dexkit.result.MethodData;

import java.lang.reflect.Method;
import java.util.List;

import ps.reso.instaeclipse.xposedcompat.XC_MethodHook;
import ps.reso.instaeclipse.xposedcompat.XposedBridge;
import ps.reso.instaeclipse.Xposed.Module;
import ps.reso.instaeclipse.utils.core.DexKitCache;
import ps.reso.instaeclipse.utils.feature.FeatureFlags;
import ps.reso.instaeclipse.utils.feature.FeatureStatusTracker;
import ps.reso.instaeclipse.utils.log.ModuleLog;
import ps.reso.instaeclipse.utils.core.DexKitQueryCache;

public class DisableVideoAutoPlayHook {

    public void handleAutoPlayDisable(DexKitBridge bridge) {
        if (DexKitCache.isCacheValid()) {
            Method cached = DexKitCache.loadMethod("AutoPlayDisable", Module.hostClassLoader);
            if (cached != null) {
                hookMethod(cached);
                return;
            }
        }
        try {
            findAndHookDynamicMethod(bridge);
        } catch (Exception e) {
            ModuleLog.line("(InstaEclipse | AutoPlayDisable): Error: " + e.getMessage());
        }
    }

    private void findAndHookDynamicMethod(DexKitBridge bridge) {
        try {
            // Step 1: Find methods referencing "ig_disable_video_autoplay"
            List<MethodData> methods = DexKitQueryCache.findMethod(bridge, FindMethod.create()
                    .matcher(MethodMatcher.create()
                            .usingStrings("ig_disable_video_autoplay")
                    )
            );

            if (methods.isEmpty()) {
                ModuleLog.line("(InstaEclipse | AutoPlayDisable): ❌ No matching methods found.");
                return;
            }

            // Step 2: Find the correct method: boolean return type, 1 parameter
            for (MethodData method : methods) {
                boolean returnTypeMatch = String.valueOf(method.getReturnType()).contains("boolean");
                boolean paramTypesMatch = method.getParamTypes().size() == 1;

                if (returnTypeMatch && paramTypesMatch) {
                    hookMethod(method);
                    return;
                }
            }

            ModuleLog.line("(InstaEclipse | AutoPlayDisable): ❌ No matching methods with correct signature.");
        } catch (Exception e) {
            ModuleLog.line("(InstaEclipse | AutoPlayDisable): ❌ Error during method discovery: " + e.getMessage());
        }
    }

    private void hookMethod(MethodData method) {
        try {
            Method targetMethod = method.getMethodInstance(Module.hostClassLoader);
            DexKitCache.saveMethod("AutoPlayDisable", targetMethod);
            hookMethod(targetMethod);
            FeatureStatusTracker.setHooked("DisableVideoAutoPlay");
            ModuleLog.line("(InstaEclipse | AutoPlayDisable): ✅ Hooked (dynamic check): " +
                    method.getClassName() + "." + method.getName());
        } catch (Exception e) {
            ModuleLog.line("(InstaEclipse | AutoPlayDisable): ❌ Error hooking method: " + e.getMessage());
        }
    }

    private void hookMethod(Method targetMethod) {
        XposedBridge.hookMethod(targetMethod, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                ps.reso.instaeclipse.utils.core.JitGuard.tick(param.method);
                if (FeatureFlags.disableVideoAutoPlay) param.setResult(true);
            }
        });
    }
}
