# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/vladimirfx/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontwarn java.util.concurrent.Flow*
-dontwarn org.conscrypt.**

-keepattributes SourceFile, LineNumberTable, *Annotation*, InnerClasses
-keep public class * extends java.lang.Exception

-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class com.astroid.yodha.**$$serializer { *; }
-keepclassmembers class com.astroid.yodha.** {
    *** Companion;
}
-keepclasseswithmembers class com.astroid.yodha.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-dontobfuscate
