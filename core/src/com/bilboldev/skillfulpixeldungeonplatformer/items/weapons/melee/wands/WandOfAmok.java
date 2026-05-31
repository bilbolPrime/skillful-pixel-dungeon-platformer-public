package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.buffprojectiles.ConfuseProjectile;

public class WandOfAmok extends Wand {
    {
        manaCost = 3;
        name = "Wand of Amok";
        description = "A wand that drives struck creatures into a violent, confused frenzy.";
        gs = new GameSprite("images/wands/WAND_HOLLY.png", 45, 45);
    }

    @Override
    public void addProjectile(float variance) {
        ConfuseProjectile projectile = new ConfuseProjectile();
        projectile.setRoom(owner.getRoom());
        projectile.isFriendly = owner.isFriendly;
        projectile.facingRight = owner.facingRight;
        projectile.x = owner.x;
        projectile.y = owner.y + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        projectile.setOwner(owner);
        projectile.setSpeedX(owner.facingRight ? 520 + variance : -520 + variance);
        projectile.modifyLifeSpan((getWandPowerMultiplier() - 1f) * 20f);
        UnitHelper.getInstance().addUnit(projectile);
    }
}