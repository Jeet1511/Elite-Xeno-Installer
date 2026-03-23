# Keep all your app classes (safe start)
-keep class com.elitexeno.installer.** { *; }

# Prevent warnings
-dontwarn **

# Keep broadcast receiver
-keep class com.elitexeno.installer.core.InstallReceiver { *; }