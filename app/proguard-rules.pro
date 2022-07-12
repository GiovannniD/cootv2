# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*
# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform

-dontwarn com.google.firebase.messaging.FirebaseMessaging
-dontwarn com.google.firebase.messaging.FirebaseMessagingService
-dontwarn com.google.firebase.messaging.zza

###############################################################################################################################################################

#------Basic command area------
# Code obfuscation compression ratio, between 0 and 7, default to 5, generally not modified
-optimizationpasses 5
# Do not use case mixing when mixing. The mixed class name is lowercase
-dontusemixedcaseclassnames
# Specifies whether to confuse third-party jar s by not ignoring classes of non-public Libraries
-dontskipnonpubliclibraryclasses
# Specifies not to ignore class members of non-public Libraries
-dontskipnonpubliclibraryclassmembers
# Preverify is one of the four steps of proguard without pre verification. Android does not need preverify. Removing this step can speed up confusion
-dontpreverify
# Whether to record the log during obfuscation, including the mapping relationship of class name - > obfuscated class name
-verbose
#Obfuscation mapping file output
-printmapping proguardMapping.txt
#Keep annotations unambiguous
-keepattributes *Annotation*,InnerClasses
# Avoid confusing generics
-keepattributes Signature
# Keep the source file name and line number when throwing an exception
-keepattributes SourceFile,LineNumberTable
# Specify the algorithm to be used for obfuscation, and the following parameter is a filter. This filter is the algorithm recommended by Google, which is generally unchanged
-optimizations !code/simplification/cast,!field/*,!class/merging/*

# ------Default reserve------
#Keep the four major components we use, custom Application and so on, and these classes will not be confused
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService

# Keep all classes and their inner classes under support
-keep class android.support.** {*;}
# Keep the
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**
#Fragment does not need to be registered in AndroidManifest.xml. It needs additional protection
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment

#If a v4 or v7 package is referenced, the warning can be ignored because android.support is not available
-dontwarn android.support.**

# Keep native methods from being confused
-keepclasseswithmembernames class * {
    native <methods>;
}

# The method parameter retained in the Activity is the view method, so the onClick we write in the layout will not be affected
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# Keep our custom controls (inherited from View) unambiguous
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep custom control classes unambiguous
-keepclasseswithmembers class * {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep the Parcelable serialization class from being confused
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#Keep Serializable
-keepnames class * implements java.io.Serializable

# Keep Serializable serialized classes unambiguous
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#Keep enumerating enum classes from being confused. If there is an error, it is recommended to directly use the above - keepclassmembers class * implements java.io.Serializable
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#Keep entity classes from being confused
-keep class com.mys.example.common.entity.**{*;}

#Filter R file confusion:
-keep class **.R$* {
 *;
}

#------Third party framework to avoid confusion------
# butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector{ *; }#7 below
-keep class **$$ViewBinder { *; }#More than 7
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
#OkHttp3
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**
-keep class okio.**{*;}
#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
   **[] $VALUES;
   public *;
 }
#AndPermission
-dontwarn com.yanzhenjie.permission.**
-keep class com.yanzhenjie.permission.** { *; }
-keepclassmembers class ** {
    @com.yanzhenjie.permission.PermissionYes <methods>;
}
-keepclassmembers class ** {
    @com.yanzhenjie.permission.PermissionNo <methods>;
}
#leakcanary
-dontwarn com.squareup.leakcanary.**
-keep class com.squareup.leakcanary.** { *; }
#blockcanary
-dontwarn com.github.moduth.**
-keep class com.github.moduth.** { *; }
#Bugly log Sdk
-dontwarn com.tencent.bugly.**
-keep class com.tencent.bugly.**{*;}
#smartrefresh
-dontwarn com.scwang.smartrefresh.**
-keep class com.scwang.smartrefresh.** { *; }
#WheelPicker
-dontwarn cn.qqtheme.framework.**
-keep class cn.qqtheme.framework.** { *; }
#BaseRecyclerViewAdapterHelper
-dontwarn com.github.CymChad.**
-keep class com.github.CymChad.** { *; }
#firebase
-dontwarn com.google.firebase.**
-keep class com.google.firebase.** { *; }
-dontwarn com.google.android.gms.**
-keep class com.google.android.gms.** { *; }
#materialish-progress
-dontwarn com.pnikosis.**
-keep class com.pnikosis.** { *; }
#ultimatebar
-dontwarn com.github.zackratos.ultimatebar.**
-keep class com.github.zackratos.ultimatebar.** { *; }
# Gson related confusion configuration
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
# Annotation class to avoid confusion
-keep class * extends java.lang.annotation.Annotation { *; }
-keep interface * extends java.lang.annotation.Annotation { *; }
#Add according to the actual situation of the project
#-keep class com.my.example.common.utils.NotificationUtils { *; }