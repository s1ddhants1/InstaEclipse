# R8 & ProGuard Optimization Rules for InstaEclipse
# ----------------------------------------------------

# Keep essential attributes for debugging and reflection
-keepattributes *Annotation*, SourceFile, LineNumberTable, InnerClasses, EnclosingMethod, Signature

# Keep LibXposed Module Entry Classes & Service Provider
-keep public class * extends io.github.libxposed.api.XposedModule {
    public <init>();
    public void on*(...);
}
-keep class io.github.libxposed.service.XposedProvider { *; }
-dontwarn io.github.libxposed.api.**

# Keep InstaEclipse module entry point and all classes/members
# Required for Xposed assets/xposed_init, reflection hooks, and internal IPC
-keep class ps.reso.instaeclipse.** { *; }
-keepclassmembers class ps.reso.instaeclipse.** { *; }

# Keep native method signatures
-keepclasseswithmembernames class * {
    native <methods>;
}

# DexKit JNI and runtime reflection
-keep class org.luckypray.dexkit.** { *; }
-dontwarn org.luckypray.dexkit.**

# osmdroid map integration
-keep class org.osmdroid.** { *; }
-dontwarn org.osmdroid.**

# Keep getters/setters that may be dynamically invoked
-keepclassmembers class * {
    *** get*();
    void set*(***);
}

# Suppress harmless warnings from build tools and optional dependencies
-dontwarn android.support.**
-dontwarn androidx.**
-dontwarn com.android.**
-dontwarn org.lsposed.**
-dontwarn javax.lang.model.**
-dontwarn com.google.errorprone.annotations.**
-dontwarn org.checkerframework.**
