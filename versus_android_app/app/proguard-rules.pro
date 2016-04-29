# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/schoi/Library/Android/sdk/tools/proguard/proguard-android.txt
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

# Autogenerated definitions which include:
## injectables returned by  grep "return new" src/main/java/com/tagged/modules/TaggedModule.java | sed 's/.*new \(.*\)(.*/**.\1/m' | tr '\n' ',' | sed '$s/.$/\n/'
## injects returned by grep ".class," src/main/java/com/tagged/modules/TaggedModule.java | sed 's/\s\+\(.*\).class.*/**.\1/m' | tr '\n' ',' | sed '$s/.$/\n/'

-keepattributes *Annotation*,EnclosingMethod
-keepattributes Signature

# Square Dagger
-dontwarn dagger.internal.codegen.**
-keepclassmembers,allowobfuscation class * {
    @javax.inject.* *;
    @dagger.* *;
    <init>();
}
-keep class dagger.* { *; }
-keep class javax.inject.* { *; }
-keep class * extends dagger.internal.Binding
-keep class * extends dagger.internal.ModuleAdapter
-keep class * extends dagger.internal.StaticInjection
-keep @dagger.Module class *

# Square Retrofit
-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.* { *; }
-keep class javax.** { *; }
-keep class retrofit.** { *; }
-keep class com.squareup.** { *; }
-dontwarn retrofit.**
-dontwarn okio.**
-dontwarn com.squareup.**
-dontwarn com.quantcast.**

# Square Retrofit 2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Square OkHttp3
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Facebook
-keep class com.facebook.** { *; }
-keepattributes Signature

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }

# gms
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep class com.google.android.gms.** { *; }

# generic
-keep public class java.lang.**
-keep public class javax.annotation.**
-keep public class javax.lang.**
-keep class javax.lang.model.**
-dontwarn javax.lang.model.**
-dontwarn com.google.**

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# general android
-keep public class * extends android.app.Activity { *; }
-keep class android.app.Activity { *; }
-keep public class * extends android.support.v7.app.ActionBarActivity { *; }
-keep class android.support.v7.app.ActionBarActivity { *; }
-keep class android.support.v7.**$* { *; }
-keep public class * extends android.support.v4.app.FragmentActivity { *; }
-keep public class * extends android.support.v4.app.Fragment { *; }
-keep public class * extends android.support.v4.app.DialogFragment { *; }
-keep public class * extends android.app.Application { *; }
-keep public class * extends android.app.Service { *; }
-keep public class * extends android.content.BroadcastReceiver { *; }
-keep public class * extends android.content.ContentProvider { *; }
-keep public class * extends android.preference.Preference { *; }
-keep class android.content.**  { *; }
-keep class android.webkit.** { *; }
-keep class android.support.v4.app.DialogFragment { *; }
-keep class android.support.v4.app.**  { *; }
-dontwarn android.support.**

# Allow obfuscation to avoid problem on Samsung 4.2.2 devices with appcompat v23.1.1
# see https://code.google.com/p/android/issues/detail?id=78377
-keep class !android.support.v7.view.menu.*MenuBuilder*, android.support.v7.** { *; }
-keep interface android.support.v7.* { *; }

# Android design support library (turned off for now, prevents samsung fix from working)
#-dontwarn android.support.design.**
#-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

# Android appcompat library
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

# Android Grid Layout support library
-keep class android.support.v7.widget.** { *; }
-dontwarn android.support.v7.**
-dontwarn android.support.v8.**

-keepclassmembernames class * {
    ** _nr_trace;
}

# framework specific ones
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# Butterknife
-dontwarn butterknife.internal.**
-keep class butterknife.** { *; }
-keep class **$$ViewInjector { *; }

-keepclasseswithmembernames class * {
    @butterknife.InjectView <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.OnClick <methods>;
    @butterknife.OnEditorAction <methods>;
    @butterknife.OnItemClick <methods>;
    @butterknife.OnItemLongClick <methods>;
    @butterknife.OnLongClick <methods>;
}

# Versus
-keep class co.ifwe.versus.modules.** {*;}
-keep class com.tagged.caspr.** { *; }
-keep class co.ifwe.versus.models.** { *; }
-keep class co.ifwe.versus.services.**
-keep class **.CursorPagerAdapter
-keep class co.ifwe.fragments.**$* { *; }
-keep public class * extends com.tagged.caspr.ICasprService { *; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# Retro-lambda
-dontwarn java.lang.invoke.*

# RoundedImageView
-keep class com.makeramen.roundedimageview.** { *; }