package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;


public final class MenuHeroPreview {

    private static final int CELL_WIDTH = 12, CELL_HEIGHT = 15, MAX_ARMOR_ROW = 90;
    private static final int[] IDLE = {0, 1, 1}, WALK = {2, 3, 4, 5, 6, 7};
    public static final float SCALE = 12f, WIDTH = CELL_WIDTH * SCALE, HEIGHT = CELL_HEIGHT * SCALE;
    private final HeroClass heroClass;
    private final Texture sheet, pixel;
    private final Rectangle bounds = new Rectangle();
    private float centerX, footY, idleClock, walkClock;
    private boolean moving, returning, facingLeft;

    public MenuHeroPreview(HeroClass heroClass, float centerX, float footY, float phase) {
        if (heroClass == null || heroClass == HeroClass.NEUTRAL)
            throw new IllegalArgumentException("A menu preview requires a playable class");
        this.heroClass = heroClass;
        sheet = TextureHelper.GetSingleton().getTexture(heroClass.getFilm());
        pixel = TextureHelper.GetSingleton().getSolidPixel();
        if (sheet.getWidth() < 8 * CELL_WIDTH || sheet.getHeight() < MAX_ARMOR_ROW + CELL_HEIGHT)
            throw new IllegalArgumentException("Missing native maximum-armor frames: " + heroClass);
        idleClock = Float.isNaN(phase) || Float.isInfinite(phase) ? 0f : Math.abs(phase) % IDLE.length;
        setPosition(centerX, footY);
    }

    public void act(float delta) {
        if (Float.isNaN(delta) || Float.isInfinite(delta) || delta <= 0) return;
        float step = Math.min(delta, 0.1f);
        idleClock = (idleClock + step) % IDLE.length;
        if (moving) walkClock = (walkClock + step * (returning ? -10f : 10f) + WALK.length) % WALK.length;
    }


    public void setMovement(boolean walking, boolean backwards) {
        if (walking && !moving) walkClock = 0f;
        moving = walking;
        returning = walking && backwards;
    }

    public void setPosition(float centerX, float footY) {
        this.centerX = centerX; this.footY = footY;
        bounds.set(centerX - WIDTH / 2f, footY, WIDTH, HEIGHT);
    }

    public void setFacingLeft(boolean value) { facingLeft = value; }
    public HeroClass heroClass() { return heroClass; }
    public float centerX() { return centerX; }
    public float footY() { return footY; }
    public int frame() { return moving ? WALK[(int) walkClock] : IDLE[(int) idleClock]; }
    public boolean contains(float x, float y) { return bounds.contains(x, y); }
    public boolean moving() { return moving; }
    public boolean returning() { return returning; }


    public static TextureRegion maximumArmorFrame(HeroClass heroClass) {
        return new TextureRegion(TextureHelper.GetSingleton().getTexture(heroClass.getFilm()),
                0, MAX_ARMOR_ROW, CELL_WIDTH, CELL_HEIGHT);
    }

    public void draw(Batch batch) {
        draw(batch, 1f);
    }

    public void draw(Batch batch, float alpha) {
        float packed = batch.getPackedColor();

        float x = Math.round((centerX - WIDTH / 2f) / 2f) * 2f, y = Math.round(footY / 2f) * 2f;
        batch.setColor(0.015f, 0.025f, 0.03f, 0.45f * alpha);
        batch.draw(pixel, x + 12, y - 4, WIDTH - 24, 8);
        batch.setColor(1f, 1f, 1f, alpha);
        batch.draw(sheet, x, y, WIDTH, HEIGHT, frame() * CELL_WIDTH, MAX_ARMOR_ROW,
                CELL_WIDTH, CELL_HEIGHT, facingLeft, false);
        batch.setPackedColor(packed);
    }
}
