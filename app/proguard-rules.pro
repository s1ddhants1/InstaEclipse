# Keep everything — no obfuscation, no shrinking
-dontobfuscate
-dontoptimize
-keepattributes *Annotation*, SourceFile, LineNumberTable

# Keep ALL classes & members (methods, fields)
-keep class ps.reso.instaeclipse.** { *; }

# Keep everything related to Xposed API
-keep class de.robv.android.xposed.** { *; }

# Keep reflection / DexKit-accessed symbols
-keep class * {
    public protected *;
}

# Keep any dynamically called methods (like URI matchers)
-keepclassmembers class * {
    *** get*();
    void set*(***);
}

# Avoid warnings from missing Android APIs
-dontwarn android.support.**
-dontwarn androidx.**
-dontwarn com.android.**
-dontwarn org.lsposed.**
# Suppress missing javax.lang.model warnings
-dontwarn javax.lang.model.**
-dontwarn com.google.errorprone.annotations.**
-dontwarn org.checkerframework.**


