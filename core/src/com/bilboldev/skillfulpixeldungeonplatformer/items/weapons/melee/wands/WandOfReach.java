package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.ReachBolt;

public class WandOfReach extends Wand {
    {
        manaCost = 3;
        name = "Wand of Reach";
        description = "A wand that lashes out with force, swapping places with the first enemy it catches.";
        gs = new GameSprite("images/wands/WAND_ROWAN.png", 45, 45);
    }

    @Override
    public void addProjectile(float variance) {
        ReachBolt projectile = new ReachBolt();
        projectile.setRoom(owner.getRoom());
        projectile.isFriendly = owner.isFriendly;
        projectile.facingRight = owner.facingRight;
        projectile.x = owner.x;
        projectile.y = owner.y + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        projectile.setOwner(owner);
        projectile.setAttackingItem(this);
        projectile.setSpeedX(owner.facingRight ? 600 + variance : -600 + variance);
        UnitHelper.getInstance().addUnit(projectile);
    }
}