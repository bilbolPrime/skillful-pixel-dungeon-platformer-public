-dontshrink
-dontoptimize
-dontnote
-dontwarn
-ignorewarnings
-keepdirectories

-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,Exceptions,SourceFile,LineNumberTable,StackMapTable,StackMap

# Save data and runtime systems resolve classes by stable ids and enum names, so
# class names must remain unchanged across Steam builds.
-keepnames class **

-keep class com.bilboldev.skillfulpixeldungeonplatformer.desktop.DesktopLauncher { *; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    public static final ** *;
}

-keepclassmembernames class * {
    native <methods>;
}