# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\SDK/tools/proguard/proguard-android.txt
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

#忽略警告
-ignorewarning

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

#==================gson==========================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}

#==================protobuf======================
-dontwarn com.google.**
-keep class com.google.protobuf.** {*;}
-keep class com.baidu.**{*;}
-keep class vi.com.gdi.bgl.**{*;}

-keepattributes EnclosingMetho

#---------------------------------与js互调的类------------------------------------
-keep class com.dgj.onedream.utils.JSObject {*;}    #该类中用到js的接口
#---------------------------------实体类------------------------------------------
-keep class com.boer.jiaweishi.model.** {*;}  #保护Gson的javabean对象类和类中的所有成员变量

-keepattributes *Annotation*
-keepattributes Signature

-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
 -keep public class * extends android.support.v4.**
-keep class android.support.v4.** { *; }
-keep public class * extends android.view.View

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
#================okhttp================================#
-dontwarn com.squareup.okhttp.**

-keep class com.squareup.okhttp.** { *;}

-dontwarn okio.**

 #=======================================#
-dontwarn com.handmark.pulltorefresh.library.**
-keep class com.handmark.pulltorefresh.library.** { *;}

-dontwarn com.example.colorarcprogressbarlibrary.main.**
-keep class com.example.colorarcprogressbarlibrary.main.** { *;}

-dontwarn com.triggertrap.seekarc.**
-keep class com.triggertrap.seekarc.** { *;}

-dontwarn com.allen.expandablelistview.**
-keep class com.allen.expandablelistview.** { *;}

-dontwarn com.baoyz.expandablelistview.**
-keep class com.baoyz.expandablelistview.** { *;}

-dontwarn com.jzxiang.pickerview.**
-keep class com.jzxiang.pickerview.** { *;}

-dontwarn cn.semtec.www.nebular.**
-keep class cn.semtec.www.nebular.** { *;}

-dontwarn org.linphone.**
-keep class org.linphone.** { *;}

-keepclasseswithmembernames class * {   #不混淆所有的JNI方法
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {    #不混淆activity中所有参数类型为View的方法
    public void *(android.view.View);
}
-keepclassmembers enum * {  #不混淆枚举类，这两个方法会被反射调用
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers class android.content.res.Resources {     #不混淆资源文件的私有属性
    private <fields>;
}
-keepclassmembers class * {     #不混淆jsonObject类型
    public <init>(org.json.JSONObject);
}
-keepclassmembers public class * extends android.view.View {    #所有view的子类的get、set方法不混淆
    void set*(***);
    *** get*();
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * implements java.io.Serializable {     #不混淆序列化类
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
