-verbose

-dontwarn android.support.**
-dontwarn com.badlogic.gdx.backends.android.AndroidFragmentApplication
-dontwarn com.badlogic.gdx.utils.GdxBuild
-dontwarn com.badlogic.gdx.physics.box2d.utils.Box2DBuild
-dontwarn com.badlogic.gdx.jnigen.BuildTarget*
-dontwarn com.badlogic.gdx.graphics.g2d.freetype.FreetypeBuild

-keep class com.badlogic.gdx.controllers.android.AndroidControllers

-keepclassmembers class com.badlogic.gdx.backends.android.AndroidInput* {
   <init>(com.badlogic.gdx.Application, android.content.Context, java.lang.Object, com.badlogic.gdx.backends.android.AndroidApplicationConfiguration);
}

-keepclassmembers class com.badlogic.gdx.physics.box2d.World {
   boolean contactFilter(long, long);
   void    beginContact(long);
   void    endContact(long);
   boolean getUseDefaultContactFilter();
   void    preSolve(long, long);
   void    postSolve(long, long);
   boolean reportFixture(long);
   float   reportRayFixture(long, float, float, float, float, float);
}

-keep class com.badlogic.gdx.graphics.g2d.GlyphLayout$GlyphRun {
   <init>();
}

-keepclassmembers class * implements com.badlogic.gdx.utils.Pool$Poolable {
   <init>();
}

-keep class com.badlogic.gdx.scenes.scene2d.InputEvent {
   <init>();
}

-keep class com.badlogic.gdx.scenes.scene2d.Stage$TouchFocus {
   <init>();
}

-keep class com.badlogic.gdx.backends.android.DefaultAndroidInput$KeyEvent {
   <init>();
}

-keep class com.badlogic.gdx.backends.android.DefaultAndroidInput$TouchEvent {
   <init>();
}

-keep class com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.** {
   <init>(...);
}

-keepnames class com.bilboldev.skillfulpixeldungeonplatformer.items.**

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
