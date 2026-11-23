# RecipeBox release R8 rules.
# Prefer annotation-scoped and package-scoped keeps over library-wide -keep.

# Crash reports: keep line numbers, hide original file names.
-keepattributes SourceFile,LineNumberTable,*Annotation*,Signature,InnerClasses,EnclosingMethod
-renamesourcefileattribute SourceFile

# kotlinx.serialization only our @Serializable models + generated serializers.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
-keep,includedescriptorclasses class com.tharsis.recipebox.**$$serializer { *; }
-keepclassmembers class com.tharsis.recipebox.** {
    *** Companion;
}
-keepclasseswithmembers class com.tharsis.recipebox.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep @kotlinx.serialization.Serializable class com.tharsis.recipebox.** { *; }

# Coil / OkHttp ship their own consumer rules; only silence optional platform stubs.
-dontwarn coil3.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.codehaus.mojo.animal_sniffer.**

# Koin module graph (referenced from Application).
-keep class com.tharsis.recipebox.di.** { *; }
