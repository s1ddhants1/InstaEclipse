package de.robv.android.xposed;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.github.libxposed.api.XposedInterface;
import ps.reso.instaeclipse.Xposed.Module;

public final class XposedBridge {
    public static final String TAG = "InstaEclipse";
    private static final Set<Member> hookedMembers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private XposedBridge() {}

    public static void log(String text) {
        Log.i(TAG, text);
        if (Module.INSTANCE != null) {
            Module.INSTANCE.log(Log.INFO, TAG, text);
        }
    }

    public static void log(Throwable t) {
        Log.e(TAG, "Exception: " + t.getMessage(), t);
        if (Module.INSTANCE != null) {
            Module.INSTANCE.log(Log.ERROR, TAG, "Exception: " + t.getMessage(), t);
        }
    }

    public static XC_MethodHook.Unhook hookMethod(Member hookMethod, final XC_MethodHook callback) {
        if (!(hookMethod instanceof Executable)) {
            throw new IllegalArgumentException("Only methods and constructors can be hooked: " + hookMethod);
        }
        final Executable executable = (Executable) hookMethod;
        final boolean isConstructor = executable instanceof Constructor;

        if (Module.INSTANCE == null) {
            Log.w(TAG, "Module.INSTANCE is null; cannot hook " + executable);
            return new XC_MethodHook.Unhook(null);
        }

        try {
            XposedInterface.HookHandle handle = Module.INSTANCE.hook(executable)
                .setPriority(callback.priority)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(chain -> {
                    XC_MethodHook.MethodHookParam param = new XC_MethodHook.MethodHookParam();
                    param.method = executable;
                    param.thisObject = chain.getThisObject();
                    param.args = chain.getArgs().toArray();

                    try {
                        callback.beforeHookedMethod(param);
                    } catch (Throwable t) {
                        log(t);
                    }

                    if (param.returnEarly) {
                        if (param.hasThrowable()) {
                            throw param.getThrowable();
                        }
                        if (isConstructor) {
                            return null;
                        }
                        return param.getResult();
                    }

                    Object result = null;
                    try {
                        Object[] argsToPass = param.args != null ? param.args : new Object[0];
                        result = chain.proceed(argsToPass);
                        param.setResult(result);
                    } catch (Throwable t) {
                        param.setThrowable(t);
                    }

                    try {
                        callback.afterHookedMethod(param);
                    } catch (Throwable t) {
                        log(t);
                    }

                    if (param.hasThrowable()) {
                        throw param.getThrowable();
                    }
                    if (isConstructor) {
                        return null;
                    }
                    return param.getResult();
                });

            hookedMembers.add(hookMethod);
            return new XC_MethodHook.Unhook(handle);
        } catch (Throwable t) {
            log(t);
            return new XC_MethodHook.Unhook(null);
        }
    }

    public static java.util.Set<XC_MethodHook.Unhook> hookAllMethods(Class<?> hookClass, String methodName, XC_MethodHook callback) {
        java.util.Set<XC_MethodHook.Unhook> unhooks = new java.util.HashSet<>();
        for (Method method : hookClass.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                unhooks.add(hookMethod(method, callback));
            }
        }
        return unhooks;
    }

    public static java.util.Set<XC_MethodHook.Unhook> hookAllConstructors(Class<?> hookClass, XC_MethodHook callback) {
        java.util.Set<XC_MethodHook.Unhook> unhooks = new java.util.HashSet<>();
        for (Constructor<?> constructor : hookClass.getDeclaredConstructors()) {
            unhooks.add(hookMethod(constructor, callback));
        }
        return unhooks;
    }
}
