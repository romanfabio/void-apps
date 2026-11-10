# Shared R8 rules for every Void app (docs/SECURITY.md §4).
# App-specific rules go in apps/<app>/proguard-rules.pro.

# Strip verbose, debug and info logging from release builds.
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
