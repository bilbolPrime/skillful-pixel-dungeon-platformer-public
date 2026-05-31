package com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class ScrollOfBloodyRitual extends Scroll {
    {
        name = "Scroll of Bloody Ritual";
        description = "A vicious rite that restores the reader completely while bleeding every other creature in the room.";
        gs = new GameSprite("images/misc/extracted items/SCROLL_BLOODY.png", 45, 45);
        goldCost = 75;
    }

    @Override
    public void consume() {
        getHero().setHP(getHero().getMaxHP());
        EffectsHelper.getInstance().heal(getHero());

        float damage = Math.max(10f, MapHelper.getInstance().getDepth() * 3f);
        int affected = 0;
        for (Mob mob : getActiveRoomMobs(true)) {
            mob.takeDamage(getHero(), null, damage);
            affected++;
        }

        EffectsHelper.getInstance().message(getHero(), affected > 0 ? "Ritual!" : "Restored", Color.RED, 0f);
        super.consume();
    }
}