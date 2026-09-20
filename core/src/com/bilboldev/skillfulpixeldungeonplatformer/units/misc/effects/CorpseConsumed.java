package com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.CorpseRecord;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;


public final class CorpseConsumed extends Effect {
    private static final float DURATION = .65f;
    private final Hero hero;
    private final String room;
    private final int placement;
    private final GameSprite particle = new GameSprite("images/misc/black-particle.png", 8f, 8f);
    private float remaining = DURATION;

    public CorpseConsumed(Hero hero, float centerX, float floorY) {
        this.hero = hero;
        room = hero.getRoom();
        placement = hero.getPresentationPlacementVersion();
        x = centerX;
        y = floorY;
        gs = new GameSprite(NewClassAssets.ItemArt.CORPSE.key(), CorpseRecord.ART_SIZE, CorpseRecord.ART_SIZE, .88f);
        gs.setColor(new Color(.84f, .86f, .82f, 1f));
        gs.setPosition(x - CorpseRecord.HALF_WIDTH, y - CorpseRecord.BOTTOM_PADDING);
        particle.setColor(new Color(.42f, .28f, .55f, 1f));
    }

    @Override public boolean active() {
        return remaining > 0f && hero == UnitHelper.getInstance().getHero() && !hero.isDead()
                && placement == hero.getPresentationPlacementVersion() && room != null
                && room.equals(hero.getRoom()) && room.equals(MapHelper.getInstance().getActiveRoomIdentifier());
    }

    @Override public void act(float delta) {
        if (!active()) { remaining = 0f; return; }
        if (WindowHelper.getInstance().windowOpen() || hero.getNewClassActions().isSuspended()) return;
        if (Float.isFinite(delta) && delta > 0f) remaining = Math.max(0f, remaining - delta);
        float progress = 1f - remaining / DURATION;
        gs.setAlpha(.88f * (1f - progress * progress * (3f - 2f * progress)));
    }

    @Override public void draw(Batch batch) {
        if (!active()) return;
        gs.draw(batch);
        float progress = 1f - remaining / DURATION;
        int count = GameSettingsHelper.getInstance().isReducedVisualEffects() ? 3 : 9;
        particle.setAlpha(.9f * (1f - progress));
        for (int i = 0; i < count; i++) {
            float angle = 360f * i / count + progress * 80f;
            float radius = 18f + progress * 26f;
            particle.setPosition(x + MathUtils.cosDeg(angle) * radius - 4f,
                    y + 26f + MathUtils.sinDeg(angle) * radius * .45f + progress * 32f);
            particle.draw(batch);
        }
    }
}
