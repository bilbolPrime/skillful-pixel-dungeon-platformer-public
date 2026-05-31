package com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Slow;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class ScrollOfFrost extends Scroll {
    {
        name = "Scroll of Frost";
        description = "A winter sigil that chills every hostile creature in the room, slowing and damaging them.";
        gs = new GameSprite("images/misc/extracted items/SCROLL_FROST.png", 45, 45);
        goldCost = 55;
    }

    @Override
    public void consume() {
        float damage = Math.max(8f, MapHelper.getInstance().getDepth() * 2f);
        int affected = 0;
        for (Mob mob : getActiveRoomMobs(false)) {
            new Slow().setPermanent(false).setDuration(6f).setOwner(mob);
            mob.takeDamage(getHero(), null, damage);
            affected++;
        }

        EffectsHelper.getInstance().message(getHero(), affected > 0 ? "Frozen" : "No targets", Color.CYAN, 0f);
        super.consume();
    }
}