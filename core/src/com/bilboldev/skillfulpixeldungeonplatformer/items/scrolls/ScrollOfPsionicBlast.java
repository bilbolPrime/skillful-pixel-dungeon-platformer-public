package com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.TemporaryBlind;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class ScrollOfPsionicBlast extends Scroll {
    {
        name = "Scroll of Psionic Blast";
        description = "A psychic detonation that damages and briefly blinds every hostile creature nearby.";
        gs = new GameSprite("images/items/scroll-sanctuary.png", 45, 45);
        goldCost = 70;
    }

    @Override
    public void consume() {
        float damage = Math.max(15f, MapHelper.getInstance().getDepth() * 4f);
        int affected = 0;
        for (Mob mob : getActiveRoomMobs(false)) {
            mob.takeDamage(getHero(), null, damage);

            if (!mob.isDead()) {
                TemporaryBlind blind = (TemporaryBlind) mob.getBuff(TemporaryBlind.class);
                if (blind == null) {
                    new TemporaryBlind().setPermanent(false).setDuration(2.5f).setOwner(mob);
                } else if (blind.active()) {

                    mob.blinded();
                }
            }
            affected++;
        }

        EffectsHelper.getInstance().message(getHero(), affected > 0 ? "Blast" : "Silence", Color.MAGENTA, 0f);
        super.consume();
    }
}
