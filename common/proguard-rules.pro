# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/chenjie/Library/Android/sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#XiaoMi SDK
-keep class com.xiaomi.ad.**{*;}
-keep class com.miui.analytics.**{*;}
-keep class com.xiaomi.analytics.*{public protected *;}

#gdt sdk
-keep class com.qq.e.** {public protected *;}
-keep class android.support.v4.app.NotificationCompat**{public *;}

#bd sdk
-keep class com.baidu.** {public protected *;}

#oppo sdk
-keep class com.oppo.** {
    public protected *;
}
-keep public class com.cdo.oaps.base.**{ *; }
-keep class okio.**{ *; }
-keep class com.squareup.wire.**{ *; }
-keep public class * extends com.squareup.wire.**{ *; }
# Keep methods with Wire annotations (e.g. @ProtoField)
-keepclassmembers class ** {
    @com.squareup.wire.ProtoField public *;
    @com.squareup.wire.ProtoEnum public *;
}


#umeng sdk
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-dontwarn org.springframework.**
-dontwarn org.apache.httpcomponents.**
-dontwarn com.androidquery.**
-dontwarn com.oppo.**

#oppo sdk
-keep class com.oppo.** {
    public protected *;
}
-keep class okio.**{ *; }
-keep class com.squareup.wire.**{ *; }
-keep public class * extends com.squareup.wire.**{ *; }
# Keep methods with Wire annotations (e.g. @ProtoField)
-keepclassmembers class ** {
 @com.squareup.wire.ProtoField public *;
 @com.squareup.wire.ProtoEnum public *;
}
-keep public class com.cdo.oaps.base.**{ *; }
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*
