# Maximum obfuscation
-repackageclasses 'o'
-allowaccessmodification
-useuniqueclassmembernames
-dontusemixedcaseclassnames
-overloadaggressively
-adaptclassstrings
-adaptresourcefilenames
-adaptresourcefilecontents

# Control flow obfuscation
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*,!code/allocation/variable
-keepattributes SourceFile,LineNumberTable,*Annotation*,EnclosingMethod,Signature,InnerClasses

# Anti-debugging
-assumevalues class android.os.Build$VERSION {
    int SDK_INT return 21..34;
}

# Crash reporting protection
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# Encrypt strings in code
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Additional anti-tamper checks
-keepclasseswithmembers class * {
    native <methods>;
}

# Prevent class enumeration
-keep,allowshrinking class * extends java.lang.Throwable
-keepclassmembers class * extends java.lang.Throwable { *; }

# Change package hierarchy
-repackageclasses ''

# Aggressive optimization
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*,!code/allocation/variable

# Keep WebView JavaScript interface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep only essential parts of activities and services
-keep class com.example.mywebviewapp.NotificationListener {
    public void onNotificationPosted(android.service.notification.StatusBarNotification);
    public void onNotificationRemoved(android.service.notification.StatusBarNotification);
}

# Keep Boot Receiver minimal functionality
-keep class com.example.mywebviewapp.BootReceiver {
    public void onReceive(android.content.Context, android.content.Intent);
}

# Keep Glide configuration
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# General Android rules
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.view.View
-keep public class * extends android.preference.Preference

# Remove debug logs in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}