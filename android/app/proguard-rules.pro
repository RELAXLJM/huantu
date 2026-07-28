# ==================== Retrofit & OkHttp ====================
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * { @retrofit2.http.* <methods>; }
-dontwarn okhttp3.**
-dontwarn okio.**

# ==================== Gson ====================
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class com.gdpt.huantu.core.model.** { *; }
-keep class com.gdpt.huantu.core.model.request.** { *; }
-keep class com.gdpt.huantu.core.network.ApiResponse { *; }

# ==================== Room ====================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ==================== Hilt ====================
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# ==================== Glide ====================
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl

# ==================== ViewBinding ====================
-keep class * implements androidx.viewbinding.ViewBinding { *; }

# ==================== Tencent Map ====================
-keep class com.tencent.** { *; }
-dontwarn com.tencent.**

# ==================== General ====================
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
