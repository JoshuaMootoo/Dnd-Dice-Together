# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the Android SDK's default ProGuard configuration.

# Keep kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.dnd.dicelobby.**$$serializer { *; }
-keepclassmembers class com.dnd.dicelobby.** {
    *** Companion;
}
-keepclasseswithmembers class com.dnd.dicelobby.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**
