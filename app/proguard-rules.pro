# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\studio_sdk/tools/proguard/proguard-android.txt
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

# JMessage
-dontwarn cn.jpush.**
-keepattributes  EnclosingMethod,Signature
-keep class cn.jpush.** { *; }
-keepclassmembers class ** {
    public void onEvent*(**);
}

#========================gson================================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}

#========================protobuf================================
#-dontwarn com.google.**
-keep class com.google.protobuf.** {*;}


# 腾讯云通信
-keep class com.tencent.**{*;}
-dontwarn com.tencent.**

-keep class tencent.**{*;}
-dontwarn tencent.**

-keep class qalsdk.**{*;}
-dontwarn qalsdk.**


# 3D 地图
    -keep   class com.amap.api.maps.**{*;}
    -keep   class com.autonavi.amap.mapcore.*{*;}
    -keep   class com.amap.api.trace.**{*;}

# 定位
    -keep class com.amap.api.location.**{*;}
    -keep class com.amap.api.fence.**{*;}
    -keep class com.autonavi.aps.amapapi.model.**{*;}

#  搜索
    -keep   class com.amap.api.services.**{*;}

#导航
    -keep class com.amap.api.navi.**{*;}
    -keep class com.autonavi.**{*;}


# Jmessage
-dontwarn cn.jpush.**
-keepattributes  EnclosingMethod,Signature
-keep class cn.jpush.** { *; }
-keepclassmembers class ** {
    public void onEvent*(**);
}

#========================gson================================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}

#========================protobuf================================
#-dontwarn com.google.**
-keep class com.google.protobuf.** {*;}