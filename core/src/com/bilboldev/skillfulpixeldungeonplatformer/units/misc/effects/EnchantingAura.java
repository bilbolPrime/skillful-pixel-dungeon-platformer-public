package com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EnhancementVisualHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.Prefix;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class EnchantingAura extends Effect {
    private enum Phase {
        FADE_IN,
        STATIC,
        FADE_OUT,
        DONE
    }

    private static final float FADE_IN_TIME = 0.18f;
    private static final float STATIC_TIME = 0.65f;
    private static final float FADE_OUT_TIME = 0.35f;
    private static final float MAX_ALPHA = 0.95f;

    private Unit owner;
    private Phase phase;
    private float duration;
    private float passed;
    private float effectWidth;
    private float effectHeight;

    public Effect init(Unit owner, Item item, Prefix prefix) {
        this.owner = owner;

        GameSprite itemSprite = item != null ? item.getGameSprite() : null;
        gs = itemSprite != null
                ? itemSprite.clone()
                : new GameSprite("images/intro/fireball-front.png", 64, 64, 1f);

        effectWidth = Math.max(140f, gs.getWidth() * 3f);
        effectHeight = Math.max(140f, gs.getHeight() * 3f);
        gs.setWidth((int) effectWidth);
        gs.setHeight((int) effectHeight);
        gs.setRotation(0f);
        EnhancementVisualHelper.applyPrefixEnhancementPulse(gs, prefix);
        if (prefix == null || !prefix.isEnhancement()) {
            gs.setPulseTint(Color.CYAN, 0.45f);
        }
        syncPosition();
        gs.setAlpha(0f);

        phase = Phase.FADE_IN;
        duration = FADE_IN_TIME;
        passed = 0f;
        return this;
    }

    @Override
    public void act(float delta) {
        if (owner == null || gs == null || phase == Phase.DONE) {
            phase = Phase.DONE;
            return;
        }

        syncPosition();

        float progress = duration <= 0f ? 1f : Math.min(1f, passed / duration);
        if (phase == Phase.FADE_IN) {
            gs.setAlpha(progress * MAX_ALPHA);
        }
        else if (phase == Phase.STATIC) {
            gs.setAlpha(MAX_ALPHA);
        }
        else if (phase == Phase.FADE_OUT) {
            gs.setAlpha((1f - progress) * MAX_ALPHA);
        }

        passed += delta;
        if (passed < duration) {
            return;
        }

        if (phase == Phase.FADE_IN) {
            phase = Phase.STATIC;
            duration = STATIC_TIME;
        }
        else if (phase == Phase.STATIC) {
            phase = Phase.FADE_OUT;
            duration = FADE_OUT_TIME;
        }
        else {
            phase = Phase.DONE;
            gs.setAlpha(0f);
        }

        passed = 0f;
    }

    private void syncPosition() {
        if (owner == null || gs == null) {
            return;
        }

        x = owner.x + ConstantsHelper.UNIT_DIMENSIONS / 2f - effectWidth / 2f;
        y = owner.y - effectHeight * 0.2f;
        gs.setPosition(x, y);
    }

    @Override
    void gravity(float delta) {
    }

    @Override
    public boolean active() {
        return phase != Phase.DONE;
    }
}