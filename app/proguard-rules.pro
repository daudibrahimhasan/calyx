# Add project specific ProGuard rules here.

# Keep data classes
-keep class com.calyx.app.data.models.** { *; }

# Keep navigation classes (sealed class and its objects)
-keep class com.calyx.app.ui.navigation.** { *; }
-keepclassmembers class com.calyx.app.ui.navigation.BottomNavItem$* { *; }

# Keep ViewModel classes
-keep class * extends androidx.lifecycle.ViewModel { *; }

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Jetpack Compose
-keep class androidx.compose.** { *; }
-keep class kotlin.Metadata { *; }

# Coil
-keep class coil.** { *; }
-keep class io.coil.** { *; }

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
