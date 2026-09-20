package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.MirrorImage;

public class WandOfFlock extends Wand {
    {
        manaCost = 4;
        name = "Wand of Flock";
        description = "A wand that conjures a clutch of short-lived mirror images to clog the path ahead.";
        gs = new GameSprite("images/wands/WAND_WILLOW.png", 45, 45);
    }

    @Override
    public void addProjectile(float variance) {

        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (unit instanceof MirrorImage && unit.isFriendly == owner.isFriendly && unit.isSummoned && !unit.showOnly()) {
                unit.unSummon();
            }
        }

        int summons = getWandPowerMultiplier() >= 1.5f ? 3 : 2;
        float[] offsets = new float[]{-ConstantsHelper.TILE, ConstantsHelper.TILE, ConstantsHelper.TILE * 2f};
        int spawned = 0;
        for (float offset : offsets) {
            if (spawned >= summons) {
                break;
            }

            float spawnX = owner.x + (owner.facingRight ? offset : -offset);
            float spawnY = owner.y;
            if (!UnitHelper.getInstance().freeSpace((int) spawnX, (int) spawnY, owner.getRoom())) {
                continue;
            }

            MirrorImage image = new MirrorImage();
            if (owner instanceof Hero) {
                image.initFromHero((Hero) owner);
            }
            image.makeFriendly();
            image.isFriendly = owner.isFriendly;
            image.isSummoned = true;
            image.setRoom(owner.getRoom());
            image.x = spawnX;
            image.y = spawnY;
            image.floorY = spawnY;
            UnitHelper.getInstance().addUnit(image);
            spawned++;
        }
    }
}
