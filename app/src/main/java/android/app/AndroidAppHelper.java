package android.app;

import java.lang.reflect.Method;

public final class AndroidAppHelper {
    private AndroidAppHelper() {}

    public static Application currentApplication() {
        try {
            Class<?> atClass = Class.forName("android.app.ActivityThread");
            Method m = atClass.getDeclaredMethod("currentApplication");
            m.setAccessible(true);
            return (Application) m.invoke(null);
        } catch (Throwable t) {
            return null;
        }
    }

    public static String currentProcessName() {
        try {
            Class<?> atClass = Class.forName("android.app.ActivityThread");
            Method m = atClass.getDeclaredMethod("currentProcessName");
            m.setAccessible(true);
            return (String) m.invoke(null);
        } catch (Throwable t) {
            return null;
        }
    }
}
