package com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.*;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;


public final class NewClassBurst extends Effect {
    private static final float DURATION = .55f;
    private final TextureRegion mote = new TextureRegion(TextureHelper.GetSingleton().getSolidPixel());
    private Hero hero;
    private final String room;
    private final int placement;
    private final boolean rising;
    private final boolean smoke, facingRight;
    private final boolean gunImpact;
    private final float duration;
    private int motes;
    private float remaining = DURATION;

    public NewClassBurst(Hero hero, float x, float y) {
        this(hero, x, y, false);
    }
    public NewClassBurst(Hero hero, float x, float y, boolean rising) {
        this(hero, x, y, rising, false);
    }
    public static NewClassBurst gunSmoke(Hero hero, float x, float y) {

        return new NewClassBurst(hero, x + (hero.facingRight ? 28f : -28f), y, false, true);
    }
    private NewClassBurst(Hero hero, float x, float y, boolean rising, boolean smoke) {
        this(hero, x, y, rising, smoke, false);
    }
    public static NewClassBurst gunImpact(Hero hero, float x, float y) {
        return new NewClassBurst(hero, x, y, false, false, true);
    }
    private NewClassBurst(Hero hero, float x, float y, boolean rising, boolean smoke, boolean gunImpact) {
        this.hero = hero;
        this.rising = rising;
        this.smoke = smoke;
        this.gunImpact = gunImpact;
        this.facingRight = hero.facingRight;
        duration = smoke ? .75f : DURATION;
        remaining = duration;
        this.room = hero.getRoom();
        this.placement = hero.getPresentationPlacementVersion();
        this.x = x;
        this.y = y;
        motes = maximumMotes();
    }
    private static boolean reduced() { return GameSettingsHelper.getInstance().isReducedVisualEffects(); }
    static int budget() { return reduced() ? 8 : 24; }
    private int maximumMotes() { return smoke ? (reduced() ? 1 : 3) : rising ? (reduced() ? 2 : 6) : (reduced() ? 4 : 12); }
    int moteCount() { return active() ? Math.min(motes, maximumMotes()) : 0; }
    void limitMotes(int available) { motes = Math.max(0, Math.min(motes, available)); }

    @Override public boolean active() {
        return remaining > 0f && motes > 0 && hero != null && hero == UnitHelper.getInstance().getHero()
                && !hero.isDead() && hero.getHP() > 0 && placement == hero.getPresentationPlacementVersion()
                && room != null && room.equals(hero.getRoom()) && room.equals(MapHelper.getInstance().getActiveRoomIdentifier());
    }
    @Override public void act(float delta) {
        if (!active()) { remaining = 0f; hero = null; return; }
        if (WindowHelper.getInstance().windowOpen() || hero.getNewClassActions().isSuspended()) return;
        if (Float.isFinite(delta) && delta > 0f) remaining = Math.max(0f, remaining - delta);
        if (remaining <= 0f) hero = null;
    }
    @Override public void draw(Batch batch) { drawMotes(batch, budget()); }
    int drawMotes(Batch batch, int available) {
        int count = Math.min(moteCount(), Math.max(0, available));
        if (count == 0) return 0;
        Color color = batch.getColor();
        float r = color.r, g = color.g, b = color.b, a = color.a;
        float age = 1f - remaining / duration;
        float expansion = (float)Math.sqrt(age);
        if (smoke) {

            float opacity = age < .2f ? age * 2f : (1f - age) * .5f;
            if (reduced()) opacity *= 1.5f;
            batch.setColor(r * .60f, g * .59f, b * .57f, a * opacity);
            for (int i = 0; i < count; i++) {
                float size = (24f + 24f * age) * (1f + .12f * i);
                float drift = (facingRight ? 1f : -1f) * age * (24f + 9f * i);
                float spread = i - (count - 1) * .5f;
                batch.draw(mote, x + drift + spread * 4f - size / 2f,
                        y + age * (20f + 7f * i) + spread * 3f - size / 2f, size, size);
            }
            batch.setColor(r, g, b, a);
            return count;
        }
        batch.setColor(r * (gunImpact ? .92f : .53f), g * (gunImpact ? .70f : .94f),
                b * (gunImpact ? .38f : .68f), a * .85f * (1f - age));
        for (int i = 0; i < count; i++) {
            if (rising) {
                float offset = -26f + 52f * i / Math.max(1, count - 1);
                batch.draw(mote, x + offset, y + 8f + age * (46f + 12f * (i % 3)), 6f, 9f * (1f - age));
                continue;
            }
            float angle = gunImpact ? 360f * i / count : 18f + 144f * i / Math.max(1, count - 1);
            float radius = 18f + expansion * (142f + 16f * (i % 3));
            float size = (i % 3 == 0 ? 12f : 8f) * (1f - .4f * age);
            batch.draw(mote, x + MathUtils.cosDeg(angle) * radius - size / 2f,
                    y + MathUtils.sinDeg(angle) * radius - size / 2f, size, size);
        }
        batch.setColor(r, g, b, a);
        return count;
    }
}
