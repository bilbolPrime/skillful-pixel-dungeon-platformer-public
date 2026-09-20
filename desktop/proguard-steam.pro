-dontshrink
-dontoptimize
-dontnote
-dontwarn
-ignorewarnings
-keepdirectories

-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,Exceptions,SourceFile,LineNumberTable,StackMapTable,StackMap



-keepnames class **


-keepclassmembers class * implements java.io.Serializable { *; }

-keep class com.bilboldev.skillfulpixeldungeonplatformer.desktop.DesktopLauncher { *; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    public static final ** *;
}

-keepclassmembernames class * {
    native <methods>;
}
