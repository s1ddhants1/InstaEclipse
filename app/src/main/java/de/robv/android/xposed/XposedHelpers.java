package de.robv.android.xposed;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public final class XposedHelpers {

    private static final Map<String, Field> fieldCache = new ConcurrentHashMap<>();
    private static final Map<String, Method> methodCache = new ConcurrentHashMap<>();
    private static final Map<String, Constructor<?>> constructorCache = new ConcurrentHashMap<>();
    private static final WeakHashMap<Object, Map<String, Object>> additionalFields = new WeakHashMap<>();

    private XposedHelpers() {}

    public static Class<?> findClass(String className, ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundError(e);
        }
    }

    public static Class<?> findClassIfExists(String className, ClassLoader classLoader) {
        try {
            return findClass(className, classLoader);
        } catch (ClassNotFoundError | NullPointerException e) {
            return null;
        }
    }

    public static XC_MethodHook.Unhook findAndHookMethod(Class<?> clazz, String methodName, Object... parameterTypesAndCallback) {
        if (parameterTypesAndCallback.length == 0 || !(parameterTypesAndCallback[parameterTypesAndCallback.length - 1] instanceof XC_MethodHook)) {
            throw new IllegalArgumentException("No callback specified in parameterTypesAndCallback");
        }
        XC_MethodHook callback = (XC_MethodHook) parameterTypesAndCallback[parameterTypesAndCallback.length - 1];
        Class<?>[] parameterTypes = getParameterTypes(parameterTypesAndCallback, clazz.getClassLoader());
        Method method = findMethodExact(clazz, methodName, (Object[]) parameterTypes);
        return XposedBridge.hookMethod(method, callback);
    }

    public static XC_MethodHook.Unhook findAndHookMethod(String className, ClassLoader classLoader, String methodName, Object... parameterTypesAndCallback) {
        return findAndHookMethod(findClass(className, classLoader), methodName, parameterTypesAndCallback);
    }

    public static XC_MethodHook.Unhook findAndHookConstructor(Class<?> clazz, Object... parameterTypesAndCallback) {
        if (parameterTypesAndCallback.length == 0 || !(parameterTypesAndCallback[parameterTypesAndCallback.length - 1] instanceof XC_MethodHook)) {
            throw new IllegalArgumentException("No callback specified in parameterTypesAndCallback");
        }
        XC_MethodHook callback = (XC_MethodHook) parameterTypesAndCallback[parameterTypesAndCallback.length - 1];
        Class<?>[] parameterTypes = getParameterTypes(parameterTypesAndCallback, clazz.getClassLoader());
        Constructor<?> constructor = findConstructorExact(clazz, (Object[]) parameterTypes);
        return XposedBridge.hookMethod(constructor, callback);
    }

    public static XC_MethodHook.Unhook findAndHookConstructor(String className, ClassLoader classLoader, Object... parameterTypesAndCallback) {
        return findAndHookConstructor(findClass(className, classLoader), parameterTypesAndCallback);
    }

    private static Class<?>[] getParameterTypes(Object[] parameterTypesAndCallback, ClassLoader cl) {
        int length = parameterTypesAndCallback.length - 1;
        Class<?>[] parameterTypes = new Class<?>[length];
        for (int i = 0; i < length; i++) {
            Object type = parameterTypesAndCallback[i];
            if (type instanceof Class<?>) {
                parameterTypes[i] = (Class<?>) type;
            } else if (type instanceof String) {
                parameterTypes[i] = findClass((String) type, cl);
            } else {
                throw new IllegalArgumentException("Parameter type at index " + i + " must be Class or String, got: " + type);
            }
        }
        return parameterTypes;
    }

    public static Method findMethodExact(Class<?> clazz, String methodName, Object... parameterTypes) {
        Class<?>[] types = new Class<?>[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i] instanceof Class<?>) {
                types[i] = (Class<?>) parameterTypes[i];
            } else if (parameterTypes[i] instanceof String) {
                types[i] = findClass((String) parameterTypes[i], clazz.getClassLoader());
            } else {
                throw new IllegalArgumentException("Parameter at " + i + " must be Class or String");
            }
        }
        String key = clazz.getName() + "#" + methodName + getParametersString(types);
        Method cached = methodCache.get(key);
        if (cached != null) return cached;

        Class<?> clz = clazz;
        do {
            try {
                Method m = clz.getDeclaredMethod(methodName, types);
                m.setAccessible(true);
                methodCache.put(key, m);
                return m;
            } catch (NoSuchMethodException ignored) {
                clz = clz.getSuperclass();
            }
        } while (clz != null);

        throw new NoSuchMethodError(key);
    }

    public static Method findMethodExactIfExists(Class<?> clazz, String methodName, Object... parameterTypes) {
        try {
            return findMethodExact(clazz, methodName, parameterTypes);
        } catch (Throwable t) {
            return null;
        }
    }

    public static Method[] findMethodsByExactParameters(Class<?> clazz, Class<?> returnType, Class<?>... parameterTypes) {
        List<Method> result = new ArrayList<>();
        for (Method m : clazz.getDeclaredMethods()) {
            if (returnType != null && m.getReturnType() != returnType) continue;
            Class<?>[] params = m.getParameterTypes();
            if (params.length != parameterTypes.length) continue;
            boolean match = true;
            for (int i = 0; i < params.length; i++) {
                if (params[i] != parameterTypes[i]) {
                    match = false;
                    break;
                }
            }
            if (match) {
                m.setAccessible(true);
                result.add(m);
            }
        }
        return result.toArray(new Method[0]);
    }

    public static Constructor<?> findConstructorExact(Class<?> clazz, Object... parameterTypes) {
        Class<?>[] types = new Class<?>[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i] instanceof Class<?>) {
                types[i] = (Class<?>) parameterTypes[i];
            } else if (parameterTypes[i] instanceof String) {
                types[i] = findClass((String) parameterTypes[i], clazz.getClassLoader());
            } else {
                throw new IllegalArgumentException("Parameter at " + i + " must be Class or String");
            }
        }
        String key = clazz.getName() + getParametersString(types);
        Constructor<?> cached = constructorCache.get(key);
        if (cached != null) return cached;

        try {
            Constructor<?> ctor = clazz.getDeclaredConstructor(types);
            ctor.setAccessible(true);
            constructorCache.put(key, ctor);
            return ctor;
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(key);
        }
    }

    public static Constructor<?> findConstructorExactIfExists(Class<?> clazz, Object... parameterTypes) {
        try {
            return findConstructorExact(clazz, parameterTypes);
        } catch (Throwable t) {
            return null;
        }
    }

    public static Field findField(Class<?> clazz, String fieldName) {
        String key = clazz.getName() + "#" + fieldName;
        Field cached = fieldCache.get(key);
        if (cached != null) return cached;

        Class<?> clz = clazz;
        do {
            try {
                Field f = clz.getDeclaredField(fieldName);
                f.setAccessible(true);
                fieldCache.put(key, f);
                return f;
            } catch (NoSuchFieldException ignored) {
                clz = clz.getSuperclass();
            }
        } while (clz != null);

        throw new NoSuchFieldError(key);
    }

    public static Field findFieldIfExists(Class<?> clazz, String fieldName) {
        try {
            return findField(clazz, fieldName);
        } catch (Throwable t) {
            return null;
        }
    }

    public static Object getObjectField(Object obj, String fieldName) {
        try {
            return findField(obj.getClass(), fieldName).get(obj);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public static void setObjectField(Object obj, String fieldName, Object value) {
        try {
            findField(obj.getClass(), fieldName).set(obj, value);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public static boolean getBooleanField(Object obj, String fieldName) {
        try {
            return findField(obj.getClass(), fieldName).getBoolean(obj);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public static void setBooleanField(Object obj, String fieldName, boolean value) {
        try {
            findField(obj.getClass(), fieldName).setBoolean(obj, value);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public static int getIntField(Object obj, String fieldName) {
        try {
            return findField(obj.getClass(), fieldName).getInt(obj);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public static void setIntField(Object obj, String fieldName, int value) {
        try {
            findField(obj.getClass(), fieldName).setInt(obj, value);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public static long getLongField(Object obj, String fieldName) {
        try {
            return findField(obj.getClass(), fieldName).getLong(obj);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public static void setLongField(Object obj, String fieldName, long value) {
        try {
            findField(obj.getClass(), fieldName).setLong(obj, value);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public static float getFloatField(Object obj, String fieldName) {
        try {
            return findField(obj.getClass(), fieldName).getFloat(obj);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public static void setFloatField(Object obj, String fieldName, float value) {
        try {
            findField(obj.getClass(), fieldName).setFloat(obj, value);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public static Object getStaticObjectField(Class<?> clazz, String fieldName) {
        try {
            return findField(clazz, fieldName).get(null);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public static void setStaticObjectField(Class<?> clazz, String fieldName, Object value) {
        try {
            findField(clazz, fieldName).set(null, value);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public static boolean getStaticBooleanField(Class<?> clazz, String fieldName) {
        try {
            return findField(clazz, fieldName).getBoolean(null);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public static void setStaticBooleanField(Class<?> clazz, String fieldName, boolean value) {
        try {
            findField(clazz, fieldName).setBoolean(null, value);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public static int getStaticIntField(Class<?> clazz, String fieldName) {
        try {
            return findField(clazz, fieldName).getInt(null);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public static void setStaticIntField(Class<?> clazz, String fieldName, int value) {
        try {
            findField(clazz, fieldName).setInt(null, value);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public static Object callMethod(Object obj, String methodName, Object... args) {
        try {
            Class<?>[] parameterTypes = getParameterTypesFromValues(args);
            Method method = findMethodExact(obj.getClass(), methodName, (Object[]) parameterTypes);
            return method.invoke(obj, args);
        } catch (InvocationTargetException e) {
            throw new InvocationTargetError(e.getCause());
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public static Object callStaticMethod(Class<?> clazz, String methodName, Object... args) {
        try {
            Class<?>[] parameterTypes = getParameterTypesFromValues(args);
            Method method = findMethodExact(clazz, methodName, (Object[]) parameterTypes);
            return method.invoke(null, args);
        } catch (InvocationTargetException e) {
            throw new InvocationTargetError(e.getCause());
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public static Object newInstance(Class<?> clazz, Object... args) {
        try {
            Class<?>[] parameterTypes = getParameterTypesFromValues(args);
            Constructor<?> constructor = findConstructorExact(clazz, (Object[]) parameterTypes);
            return constructor.newInstance(args);
        } catch (InvocationTargetException e) {
            throw new InvocationTargetError(e.getCause());
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        } catch (InstantiationException e) {
            throw new InstantiationError(e.getMessage());
        }
    }

    public static void setAdditionalInstanceField(Object obj, String key, Object value) {
        if (obj == null) return;
        synchronized (additionalFields) {
            Map<String, Object> map = additionalFields.computeIfAbsent(obj, k -> new HashMap<>());
            map.put(key, value);
        }
    }

    public static Object getAdditionalInstanceField(Object obj, String key) {
        if (obj == null) return null;
        synchronized (additionalFields) {
            Map<String, Object> map = additionalFields.get(obj);
            return map != null ? map.get(key) : null;
        }
    }

    public static Object removeAdditionalInstanceField(Object obj, String key) {
        if (obj == null) return null;
        synchronized (additionalFields) {
            Map<String, Object> map = additionalFields.get(obj);
            return map != null ? map.remove(key) : null;
        }
    }

    private static Class<?>[] getParameterTypesFromValues(Object... args) {
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i] != null ? args[i].getClass() : Object.class;
        }
        return types;
    }

    private static String getParametersString(Class<?>... parameterTypes) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(parameterTypes[i] != null ? parameterTypes[i].getName() : "null");
        }
        sb.append(")");
        return sb.toString();
    }

    public static class ClassNotFoundError extends RuntimeException {
        public ClassNotFoundError(Throwable cause) { super(cause); }
        public ClassNotFoundError(String message) { super(message); }
    }

    public static class InvocationTargetError extends RuntimeException {
        public InvocationTargetError(Throwable cause) { super(cause); }
        public InvocationTargetError(String message) { super(message); }
    }
}
