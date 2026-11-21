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

-keep,allowobfuscation class com.bilboldev.skillfulpixeldungeonplatformer.units.** {
    <fields>;
    <init>(...);
}
-keepclassmembers class com.bilboldev.skillfulpixeldungeonplatformer.party.PartyReplica$* { *; }
-keepclassmembers class com.bilboldev.skillfulpixeldungeonplatformer.party.PartySprite$* { *; }
-keepclassmembers class com.bilboldev.skillfulpixeldungeonplatformer.party.PartyMenuViews$* { *; }
-keepclassmembers class com.bilboldev.skillfulpixeldungeonplatformer.helpers.SaveHelper$* { *; }
-keepclassmembers class com.bilboldev.skillfulpixeldungeonplatformer.helpers.ItemIdentityHelper$* { *; }
-keepclassmembers class com.bilboldev.skillfulpixeldungeonplatformer.helpers.QuestManager$* { *; }
-keepclassmembers class com.bilboldev.skillfulpixeldungeonplatformer.units.** { <fields>; }

-keep,allowobfuscation class * extends com.bilboldev.skillfulpixeldungeonplatformer.units.Unit { <init>(...); }
-keepclassmembers class * extends com.bilboldev.skillfulpixeldungeonplatformer.units.Unit { <fields>; }
