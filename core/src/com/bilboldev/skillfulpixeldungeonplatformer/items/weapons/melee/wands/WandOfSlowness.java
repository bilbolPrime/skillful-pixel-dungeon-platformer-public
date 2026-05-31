package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.GrandMaster;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.WandMaster;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.buffprojectiles.SlowProjectile;

public class WandOfSlowness extends Wand {

    {
        manaCost = 4;
        name = "Wand of Slowness";
        description = "A wand that burdens its targets with a heavy, dragging curse.";
        gs = new GameSprite("images/wands/WAND_OAK.png", 45, 45);
    }

    @Override
    public void addProjectile(float variance) {
        SlowProjectile projectile = new SlowProjectile();
        projectile.setRoom(owner.getRoom());
        projectile.isFriendly = owner.isFriendly;
        projectile.facingRight = owner.facingRight;
        projectile.x = owner.x;
        projectile.y = owner.y + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        projectile.setOwner(owner);
        projectile.setSpeedX(owner.facingRight ? 520 + variance : -520 + variance);
        projectile.modifyLifeSpan((getWandPowerMultiplier() - 1f) * 20f);

        if (owner.getBuff(WandMaster.class) != null) {
            projectile.modifyLifeSpan(15f);
        }

        if (owner.getBuff(GrandMaster.class) != null) {
            projectile.modifyLifeSpan(20f);
        }

        UnitHelper.getInstance().addUnit(projectile);
    }
}